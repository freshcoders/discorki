package com.alistats.discorki;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TopDpsNotificationTests {

	@Test
	void testTopDpsCondition() {
		// Set up the match, if we create a match with a single participant and he is tracked, we will have 1 top dpsser
        // TODO: create fixture for this, but want to set up a working sample first.
        MatchDto match = new MatchDto();
        InfoDto info = new InfoDto();
        info.setGameDuration(600);
        ParticipantDto topDpsParticipant = new ParticipantDto();
        topDpsParticipant.setTotalDamageDealtToChampions(1);
        topDpsParticipant.setChampionName("CHAMPION");
        topDpsParticipant.setSummonerName("SUMMONER");
        topDpsParticipant.setKills(0);
        topDpsParticipant.setDeaths(0);
        topDpsParticipant.setAssists(0));
        ParticipantDto[] participants = ParticipantDto[1]{topDpsParticipant};
        info.setParticipants(topDpsParticipant);

        TopDpsNotification dpsNotif = new TopDpsNotification();
        List<EmbedDto> embeds = dpsNotif.check(match, participants);

        assertThat(embeds.size().equals(1));
        assertThat(embeds.get(0).getTitle().contains(topDpsParticipant.getSummonerName()));
	}

}
