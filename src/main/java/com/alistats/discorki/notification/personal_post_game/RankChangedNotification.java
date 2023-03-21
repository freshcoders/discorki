package com.alistats.discorki.notification.personal_post_game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.model.QueueType;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.riot.dto.LeagueEntryDto;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.service.ImageService;

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
    @Autowired
    private ImageService imageService;

    @Override
    public Optional<PersonalPostGameNotificationResult> check(MatchDto match, Summoner summoner) {
        // Check if it was a ranked game
        if (!match.getInfo().isRanked()) {
            return Optional.empty();
        }

        // Get the ranked queue type
        QueueType rankedQueueType = QueueType.getQueueType(match.getInfo().getQueueId());

        // Get latest rank from db
        Optional<Rank> currentRankOptional = rankRepo.findFirstBySummonerAndQueueTypeOrderByIdDesc(summoner,
                rankedQueueType);

        // Get current ranks
        List<LeagueEntryDto> leagueEntries = Arrays
                .asList(leagueApiController.getLeagueEntries(summoner.getId()));
        
        // If no rank was found, save new rank and return
        if (currentRankOptional.isEmpty()) {
            saveRank(summoner, leagueEntries);
            return Optional.empty();
        }

        // Find rank for queue type
        Rank newRank = null;
        for (LeagueEntryDto leagueEntry : leagueEntries) {
            if (leagueEntry.getQueueType().equals(rankedQueueType.name())) {
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
        result.setThumbnail(imageService.getRankEmblemUrl(newRank.getLeague().getTier()));
        result.setSubject(summoner);
        result.setMatch(match);
        // Depending on the result of the comparison, set the title
        if (compareResult > 0) {
            result.setTitle(String.format("%s just demoted to %s!", summoner.getName(), newRank.getLeague().getName()));
        } else {
            result.setTitle(String.format("%s just promoted to %s!", summoner.getName(), newRank.getLeague().getName()));
        }
        result.addExtraArgument("promoted", compareResult > 0);
        result.addExtraArgument("queueDescription", queueDescription);
        result.addExtraArgument("newRank", newRank);

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
