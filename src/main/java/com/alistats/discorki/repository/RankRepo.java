package com.alistats.discorki.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.Rank;

@Repository
public interface RankRepo extends JpaRepository<Rank, Long> {
}
