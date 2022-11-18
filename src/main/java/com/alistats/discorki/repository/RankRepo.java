package com.alistats.discorki.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.Rank;

@Repository
public interface RankRepo extends JpaRepository<Rank, Long> {
    @Query(value = "SELECT * FROM rank WHERE summoner_id = ?1 AND queue_type = ?2 ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    public Optional<Rank> findLatestBySummonerId(String summonerId, String queueType);

}
