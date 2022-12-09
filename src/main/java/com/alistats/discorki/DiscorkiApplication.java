package com.alistats.discorki;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DiscorkiApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(DiscorkiApplication.class, args);
	}

	@Override
	public void run(String... args) {
	}
}
