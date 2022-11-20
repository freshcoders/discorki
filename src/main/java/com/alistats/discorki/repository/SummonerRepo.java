package com.alistats.discorki.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.Summoner;

@Repository
public interface SummonerRepo extends JpaRepository<Summoner, String> {
    public Optional<Summoner> findByName(String name);
    public Optional<ArrayList<Summoner>> findByIsTracked(Boolean isTracked);
    public Optional<ArrayList<Summoner>> findByPuuidIn(List<String> puuids);
}
