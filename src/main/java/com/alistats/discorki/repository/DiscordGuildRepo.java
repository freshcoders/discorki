package com.alistats.discorki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.DiscordGuild;

@Repository
public interface DiscordGuildRepo extends JpaRepository<DiscordGuild, String> {
    
}
