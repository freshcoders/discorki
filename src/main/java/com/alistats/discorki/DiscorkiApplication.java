package com.alistats.discorki;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.HttpClientErrorException;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.summoner.SummonerDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.SummonerRepo;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DiscorkiApplication implements CommandLineRunner {

	@Autowired
	private CustomConfigProperties customConfigProperties;
	@Autowired
	private LeagueApiController leagueApiController;
	@Autowired
	private SummonerRepo summonerRepo;
	@Autowired
	private RankRepo rankRepo;
	Logger logger = LoggerFactory.getLogger(DiscorkiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DiscorkiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		try {
			loadSummoners();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Load summoners defined in application.properties
	 * New summoners are fetched from Riot API and saved to database
	 */
	private void loadSummoners() throws Exception {
		List<String> summonerNames = customConfigProperties.getUsernames();
		// Check for summoners in the database
		for (String summonerName : summonerNames) {
			if (!summonerRepo.findByName(summonerName).isPresent()) {
				// If a summoner is not found, add it
				try {
					SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
					Summoner summoner = summonerDto.toSummoner();
					summoner.setTracked(true);
					summonerRepo.save(summoner);

					// Fetch rank
					List<LeagueEntryDto> leagueEntryDtos = Arrays
							.asList(leagueApiController.getLeagueEntries(summoner.getId()));

					// Save entries
					for (LeagueEntryDto leagueEntryDto : leagueEntryDtos) {
						rankRepo.save(leagueEntryDto.toRank(summoner));
					}
				} catch (HttpClientErrorException e) {
					if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
						logger.warn("Summoner not found: {}", summonerName);
					} else {
						throw new Exception(e);
					}
				}
				
			} else {
				// If a summoner is found, make sure it is tracked
				Summoner summoner = summonerRepo.findByName(summonerName).orElseThrow();
				summoner.setTracked(true);
				summonerRepo.save(summoner);
			}
		}

		// Check if tracked summoners still need to be tracked
		summonerRepo.findByTracked(true).orElseThrow().stream()
				.filter(s -> !summonerNames.contains(s.getName()))
				.forEach(s -> {
					s.setTracked(true);
					summonerRepo.save(s);
				});
	}

}
