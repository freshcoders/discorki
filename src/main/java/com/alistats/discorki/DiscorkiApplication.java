package com.alistats.discorki;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.riot.summoner.SummonerDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.SummonerRepo;

@SpringBootApplication
@EnableScheduling
public class DiscorkiApplication implements CommandLineRunner {

	@Autowired private CustomConfigProperties customConfigProperties;
	@Autowired private LeagueApiController leagueApiController;
	@Autowired private SummonerRepo summonerRepo;

	public static void main(String[] args) {
		SpringApplication.run(DiscorkiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			loadTrackedSummoners();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}

	private void loadTrackedSummoners() throws Exception {
		List<String> summonerNames = customConfigProperties.getUsernames();
		// Check for summoners in the database
		for (String summonerName : summonerNames) {
			if (!summonerRepo.findByName(summonerName).isPresent()) {
				// If a summoner is not found, add it
				SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
				Summoner summoner = summonerDto.toSummoner();
				summoner.setIsTracked(true);
				summonerRepo.save(summonerDto.toSummoner());
			} else {
				// If a summoner is found, make sure it is tracked
				Summoner summoner = summonerRepo.findByName(summonerName).get();
				summoner.setIsTracked(true);
				summonerRepo.save(summoner);
			}
		}

		// Check if tracked summors still need to be tracked
		summonerRepo.findByIsTracked(true).get().forEach(summoner -> {
			if (!summonerNames.contains(summoner.getName())) {
				summoner.setIsTracked(false);
				summonerRepo.save(summoner);
			}
		});
	}

}
