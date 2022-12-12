package com.alistats.discorki.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.Guild;

public interface GuildRepo extends JpaRepository<Guild, String> {
    // Find active guilds
    Set<Guild> findByActiveTrue();
}
