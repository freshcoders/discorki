package com.alistats.discorki;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.SummonerDto;
import com.alistats.discorki.repository.SummonerRepo;

@SpringBootApplication
@EnableScheduling
public class DiscorkiApplication implements CommandLineRunner {

	@Value("#{'${riot.usernames}'.split(',')}")
	private List<String> summonerNames;
	@Autowired private LeagueApiController leagueApiController;
	@Autowired private SummonerRepo summonerRepo;

	public static void main(String[] args) {
		SpringApplication.run(DiscorkiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Check if every summoner is in the database
		for (String summonerName : summonerNames) {
			// If it isn't, add it
			if (!summonerRepo.findByName(summonerName).isPresent()) {
				SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
				summonerRepo.save(summonerDto.toSummoner());
			}
		}
	}

}
