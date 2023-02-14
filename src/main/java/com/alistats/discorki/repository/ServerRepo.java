package com.alistats.discorki.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.Server;

public interface ServerRepo extends JpaRepository<Server, String> {
    // Find active guilds
    Set<Server> findByActiveTrue();
}
