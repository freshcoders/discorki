package com.alistats.discorki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;

public interface RankRepo extends JpaRepository<Rank, Long> {
    public Optional<Rank> findFirstBySummonerAndQueueTypeOrderByIdDesc(Summoner summoner, String queueType);
}
