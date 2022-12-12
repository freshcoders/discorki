package com.alistats.discorki.notification;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import com.alistats.discorki.notification.team_post_game.TeamPostGameNotificationResult;
import com.alistats.discorki.notification.team_post_game.TopDpsNotification;
import com.alistats.discorki.riot.dto.match.InfoDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@SpringBootTest
@TestConfiguration
class TopDpsNotificationTests {
    @Autowired
    TopDpsNotification dpsNotif;

    @Test
    void testTopDpsCondition() {
        // Set up the match, if we create a match with a single participant and he is
        // tracked, we will have 1 top dpsser
        // TODO: create fixture for this, but want to set up a working sample first.
        MatchDto match = new MatchDto();
        InfoDto info = new InfoDto();
        info.setGameDuration(600L);
        ParticipantDto topDpsParticipant = new ParticipantDto();
        topDpsParticipant.setTotalDamageDealtToChampions(1);
        topDpsParticipant.setChampionName("CHAMPION");
        topDpsParticipant.setSummonerName("SUMMONER");
        topDpsParticipant.setKills(0);
        topDpsParticipant.setDeaths(0);
        topDpsParticipant.setAssists(0);
        ParticipantDto[] participants = new ParticipantDto[] { topDpsParticipant };
        info.setParticipants(participants);
        match.setInfo(info);
        Optional<TeamPostGameNotificationResult> result = dpsNotif.check(match, new HashSet<>(Arrays.asList(topDpsParticipant)));

        assertTrue(result.isPresent());
    }

}
