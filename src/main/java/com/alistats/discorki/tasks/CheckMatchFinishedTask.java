package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.JDASingleton;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.personal_post_game.PersonalPostGameNotification;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.notification.team_post_game.TeamPostGameNotification;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Component
public final class CheckMatchFinishedTask extends Task {
    @Autowired
    private List<TeamPostGameNotification> teamNotificationCheckers;
    @Autowired
    private List<PersonalPostGameNotification> personalNotificationCheckers;

    // Run every minute at second :30
    @Scheduled(cron = "30 * * 1/1 * ?")
    public void checkMatchesFinished() throws RuntimeException {
        logger.debug("Running task {}", this.getClass().getSimpleName());

        // Get all matches in progress
        ArrayList<Match> matchesInProgress = matchRepo.findByStatus(Status.IN_PROGRESS).orElseThrow();
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
            }
        }
    }

    private void checkForNotableEvents(MatchDto match, Set<Summoner> trackedPlayers) {
        Set<ParticipantDto> trackedParticipants = filterTrackedParticipants(trackedPlayers,
                match.getInfo().getParticipants());
        // create hashmap of summoner and participant
        HashMap<Summoner, ParticipantDto> summonerParticipantMap = new HashMap<Summoner, ParticipantDto>();
        trackedParticipants.forEach(participant -> trackedPlayers.stream()
                .filter(summoner -> summoner.getId().equals(participant.getSummonerId()))
                .forEach(summoner -> summonerParticipantMap.put(summoner, participant)));

        HashMap<Summoner, Set<MessageEmbed>> embeds = new HashMap<Summoner, Set<MessageEmbed>>();
        final AtomicBoolean notificationFound = new AtomicBoolean(false);

        // For each tracked and participating summoner, check for personal notifications
        for (Summoner summoner : trackedPlayers) {
            personalNotificationCheckers
                    .forEach(checker -> {
                        logger.debug("Checking for '{}'  for {}", checker.getClass().getSimpleName(),
                                summoner.getName());
                        Optional<PersonalPostGameNotificationResult> result = checker.check(match, summoner);
                        if (result.isPresent()) {
                            notificationFound.set(true);
                            if (embeds.containsKey(summoner)) {
                                embeds.get(summoner).add(embedFactory.getEmbed(result.get()));
                            } else {
                                embeds.put(summoner, new HashSet<MessageEmbed>());
                                embeds.get(summoner).add(embedFactory.getEmbed(result.get()));
                            }
                        }
                    });
        }

        // Check for team notifications
        teamNotificationCheckers.forEach(checker -> {
            logger.debug("Checking for '{}' for {}", checker.getClass().getSimpleName(),
                    match.getInfo().getGameId());
            Optional<TeamPostGameNotificationResult> result = checker.check(match, summonerParticipantMap);
            if (result.isPresent()) {
                notificationFound.set(true);
                for (Summoner summoner : trackedPlayers) {
                    // check if summoner is in the key of hashmap subjects
                    if (!result.get().getSubjects().containsKey(summoner)) {
                        continue;
                    }
                    if (embeds.containsKey(summoner)) {
                        embeds.get(summoner).addAll(embedFactory.getEmbeds(result.get()));
                    } else {
                        embeds.put(summoner, new HashSet<MessageEmbed>());
                        embeds.get(summoner).addAll(embedFactory.getEmbeds(result.get()));
                    }
                }
            }
        });

        if (!notificationFound.get()) {
            logger.info("No notable events found for {}", match.getInfo().getGameId());
            return;
        }

        // Build match embed with ranks
        try {
            MessageEmbed matchEmbed = embedFactory.getMatchEmbed(match,
                    getParticipantRanks(match.getInfo().getParticipants()));

            // Add to embeds
            for (Summoner summoner : embeds.keySet()) {
                if (embeds.get(summoner).isEmpty()) {
                    continue;
                }
                embeds.get(summoner).add(matchEmbed);
            }

            // Find unique guilds for each summoner and send unique embeds to each guild
            HashMap<Guild, Set<MessageEmbed>> guildEmbeds = new HashMap<Guild, Set<MessageEmbed>>();
            embeds.forEach((summoner, embed) -> {
                summoner.getUsers().forEach(user -> {
                    Guild guild = user.getGuild();
                    guildEmbeds.putIfAbsent(guild, new HashSet<>());
                    guildEmbeds.get(guild).addAll(embed);
                });
            });

            // Send embeds
            JDA jda = JDASingleton.getJDA();
            for (Guild guild : guildEmbeds.keySet()) {
                TextChannel channel = jda.getTextChannelById(guild.getDefaultChannelId());
                logger.debug("Sending embeds to channel {} in guild {}", channel.getName(), guild.getName());
                if (channel != null) {
                    channel.sendMessageEmbeds(guildEmbeds.get(guild)).queue();
                }
            }
        } catch (Exception e) {
            logger.error("Error while building match embed for game {}: {}", match.getInfo().getGameId(),
                    e.getMessage());
            return;
        }

    }

    private HashMap<ParticipantDto, Rank> getParticipantRanks(ParticipantDto[] participants) {
        HashMap<ParticipantDto, Rank> participantRanks = new HashMap<ParticipantDto, Rank>();

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

    public Set<ParticipantDto> filterTrackedParticipants(Set<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        return Arrays.stream(participants)
                .filter(p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                .collect(Collectors.toCollection(HashSet::new));
    }

}
