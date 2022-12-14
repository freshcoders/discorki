package com.alistats.discorki.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
public interface MatchRepo extends JpaRepository<Match, Long> {
    public Optional<ArrayList<Match>> findByStatus(Status status);
}
