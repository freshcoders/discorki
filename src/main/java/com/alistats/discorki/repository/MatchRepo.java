package com.alistats.discorki.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
public interface MatchRepo extends JpaRepository<Match, Long> {
    Optional<Set<Match>> findByStatus(Status status);
}
