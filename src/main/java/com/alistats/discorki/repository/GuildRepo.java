package com.alistats.discorki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.Guild;

@Repository
public interface GuildRepo extends JpaRepository<Guild, String> {
    
}
