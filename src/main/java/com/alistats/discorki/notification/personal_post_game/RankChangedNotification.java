package com.alistats.discorki.notification.personal_post_game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.match.MatchDto;

@Component
public class RankChangedNotification extends Notification implements PersonalPostGameNotification {
    @Override
    public String getName() {
        return "RankChangedNotification";
    }
    @Override
    public String getFancyName() {
        return "Rank changed notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a summoner's rank changes.";
    }
    
    @Autowired
    private RankRepo rankRepo;

    @Override
    public Optional<PersonalPostGameNotificationResult> check(MatchDto match, Summoner summoner) {
        // Check if it was a ranked game
        if (!match.getInfo().isRanked()) {
            return Optional.empty();
        }

        // Get the ranked queue type
        String rankedQueueType = Rank.getQueueTypeByQueueId(match.getInfo().getQueueId());

        // Get latest rank from db
        Optional<Rank> currentRankOptional = rankRepo.findFirstBySummonerAndQueueTypeOrderByIdDesc(summoner,
                rankedQueueType);

        // Get current ranks
        List<LeagueEntryDto> leagueEntries = Arrays
                .asList(leagueApiController.getLeagueEntries(summoner.getId()));
        
        // If no rank was found, save new rank and return
        if (!currentRankOptional.isPresent()) {
            saveRank(summoner, leagueEntries);
            return Optional.empty();
        }

        // Find rank for queue type
        Rank newRank = null;
        for (LeagueEntryDto leagueEntry : leagueEntries) {
            if (leagueEntry.getQueueType().equals(rankedQueueType)) {
                newRank = leagueEntry.toRank();
                newRank.setSummoner(summoner);
                break;
            }
        }
        if (newRank == null) {
            return Optional.empty();
        }

        // Compare rank
        Rank currentRank = currentRankOptional.get();
        int compareResult = currentRank.getLeague().compareTo(newRank.getLeague());
        String queueDescription = leagueGameConstantsController.getQueue(match.getInfo().getQueueId())
                .getDescription();

        if (compareResult == 0) {
            // Rank didn't change
            return Optional.empty();
        }

        // Save new rank
        saveRank(summoner, leagueEntries);
        
        PersonalPostGameNotificationResult result = new PersonalPostGameNotificationResult();
        result.setNotification(this);
        result.setSubject(summoner);
        result.setMatch(match);
        result.setTitle(String.format("%s just promoted to %s!", summoner.getName(), newRank.getLeague().getName()));
        result.addExtraArgument("queueDescription", queueDescription);

        return Optional.of(result);
    }

    private void saveRank(Summoner summoner, List<LeagueEntryDto> leagueEntryDtos) {
        for (LeagueEntryDto leagueEntryDto : leagueEntryDtos) {
            Rank rank = leagueEntryDto.toRank();
            rank.setSummoner(summoner);
            rankRepo.save(rank);
        }
    }
}
