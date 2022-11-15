package com.alistats.discorki.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckInGame {
    @Scheduled(cron = "* */2 * * * ?")
    public void checkInGame() {
        System.out.println("Checking if in game...");
    }
}
