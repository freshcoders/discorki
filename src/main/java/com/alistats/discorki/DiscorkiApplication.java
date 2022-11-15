package com.alistats.discorki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiscorkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscorkiApplication.class, args);
	}

}
