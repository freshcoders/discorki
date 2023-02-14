package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alistats.discorki.discord.JDASingleton;
import com.alistats.discorki.model.EmbedContainer;
import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.personal_post_game.PersonalPostGameNotification;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.notification.team_post_game.TeamPostGameNotification;
import com.alistats.discorki.riot.dto.LeagueEntryDto;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Component
public class CheckMatchFinishedTask extends Task {
    @Autowired
    private List<TeamPostGameNotification> teamNotificationCheckers;
    @Autowired
    private List<PersonalPostGameNotification> personalNotificationCheckers;

    // Run every minute at second :30
    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    @Transactional
    public void checkMatchesFinished() {
        logger.debug("Running task {}", this.getClass().getSimpleName());

        // Get all matches in progress
        Set<Match> matchesInProgress = matchRepo.findByStatus(Status.IN_PROGRESS).orElseThrow();
        logger.info("Found {} matches in progress.", matchesInProgress.size());

        // Check if the games are finished
        for (Match match : matchesInProgress) {
            checkMatchFinished(match);
        }
    }

    private void checkMatchFinished(Match match) {
        logger.info("Checking if game {} is finished...", match.getId());
        Set<Summoner> summoners = match.getTrackedSummoners();
        try {
            MatchDto matchDto = leagueApiController.getMatch(match.getId());
            if (matchDto.getInfo().isAborted()) {
                logger.info("Game {} is finished, but seems to be a remake, not checking!", match.getId());
            } else {
                logger.info("Game {} is finished, checking for notable events...", match.getId());
                checkForNotableEvents(matchDto, summoners);
            }
            logger.debug("Setting {} to finished.", match.getId());
            match.setStatus(Status.FINISHED);
            matchRepo.save(match);
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                logger.debug("Game {} is not finished yet.", match.getId());
            } else {
                logger.error("Error while checking if game {} is finished. {}", match.getId(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("null")
    private void checkForNotableEvents(MatchDto match, Set<Summoner> trackedPlayers) {
        HashMap<Summoner, ParticipantDto> trackedParticipantsMap = mapTrackedParticipants(trackedPlayers,
                match.getInfo().getParticipants());

        EmbedContainer embeds = new EmbedContainer();

        // For each tracked and participating summoner, check for personal notifications
        for (Summoner summoner : trackedPlayers) {
            personalNotificationCheckers
                    .forEach(checker -> {
                        logger.debug("Checking for '{}'  for {}", checker.getClass().getSimpleName(),
                                summoner.getName());
                        Optional<PersonalPostGameNotificationResult> result = checker.check(match, summoner);
                        result.ifPresent(personalPostGameNotificationResult -> embeds.addPersonalEmbed(summoner, embedFactory.getEmbed(personalPostGameNotificationResult)));
                    });
        }

        // Check for team notifications
        teamNotificationCheckers.forEach(checker -> {
            logger.debug("Checking for '{}' for {}", checker.getClass().getSimpleName(),
                    match.getInfo().getGameId());
            Optional<TeamPostGameNotificationResult> result = checker.check(match, trackedParticipantsMap);
            if (result.isPresent()) {
                for (Summoner summoner : trackedPlayers) {
                    // check if summoner is in the key of hashmap subjects
                    if (!result.get().getSubjects().containsKey(summoner)) {
                        continue;
                    }
                    embeds.addTeamEmbeds(summoner, embedFactory.getEmbeds(result.get()));
                }
            }
        });

        if (embeds.isEmpty()) {
            logger.info("No notable events found for {}", match.getInfo().getGameId());
            return;
        }

        // Get participants' ranks if one team notification was found
        HashMap<ParticipantDto, Rank> participantRanks = new HashMap<>();
        if (embeds.hasTeamEmbeds()) {
            logger.debug("Getting ranks for {}", match.getInfo().getGameId());
            participantRanks = getParticipantRanks(match.getInfo().getParticipants());
        }

        // Initialize JDA
        JDA jda = JDASingleton.getJDA();
        for (Server server : embeds.getGuilds()) {
            logger.debug("Building embeds for guild {}", server.getName());
            List<MessageEmbed> guildEmbedList = new ArrayList<>(embeds.getGuildEmbeds(server));
            try {
                if (embeds.guildHasTeamEmbeds(server)) {
                    guildEmbedList.add(embedFactory.getMatchEmbed(server, match, participantRanks));
                }
                TextChannel channel = jda.getTextChannelById(server.getDefaultChannelId());
                logger.debug("Sending {} embeds to channel {} in guild {}", guildEmbedList.size(), channel.getName(),
                        server.getName());
                channel.sendMessageEmbeds(guildEmbedList).queue();
            } catch (Exception e) {
                logger.error("Error while building/sending embeds for game {}: {}", match.getInfo().getGameId(),
                        e.getMessage());
                return;
            }
        }

    }

    private HashMap<ParticipantDto, Rank> getParticipantRanks(ParticipantDto[] participants) {
        HashMap<ParticipantDto, Rank> participantRanks = new HashMap<>();

        // fetch all the soloq ranks off all team members
        for (ParticipantDto participant : participants) {
            LeagueEntryDto[] leagueEntries = leagueApiController.getLeagueEntries(participant.getSummonerId());
            if (leagueEntries != null) {
                for (LeagueEntryDto leagueEntry : leagueEntries) {
                    if (leagueEntry.getQueueType().equals("RANKED_SOLO_5x5")) {
                        participantRanks.put(participant, leagueEntry.toRank());
                        break;
                    }
                }
            }
        }

        return participantRanks;
    }

    public HashMap<Summoner, ParticipantDto> mapTrackedParticipants(Set<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        Set<ParticipantDto> filteredParticipants = Arrays.stream(participants)
                .filter(p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                .collect(Collectors.toCollection(HashSet::new));

        HashMap<Summoner, ParticipantDto> summonerParticipantMap = new HashMap<>();
        filteredParticipants.forEach(participant -> trackedSummoners.stream()
                .filter(summoner -> summoner.getId().equals(participant.getSummonerId()))
                .forEach(summoner -> summonerParticipantMap.put(summoner, participant)));

        return summonerParticipantMap;
    }

}
