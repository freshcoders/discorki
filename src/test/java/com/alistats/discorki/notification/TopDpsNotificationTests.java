package com.alistats.discorki.notification;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
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
        Summoner summoner = new Summoner();
        summoner.setName("SUMMONER");
        match.setInfo(info);
        HashMap<Summoner, ParticipantDto> participants = new HashMap<>();
        participants.put(summoner, topDpsParticipant);
        Optional<TeamPostGameNotificationResult> result = dpsNotif.check(match, participants);

        assertTrue(result.isPresent());
    }

}
