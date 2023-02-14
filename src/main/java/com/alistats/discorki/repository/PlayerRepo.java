package com.alistats.discorki.repository;

import com.alistats.discorki.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo extends JpaRepository<Player, String> {
}
