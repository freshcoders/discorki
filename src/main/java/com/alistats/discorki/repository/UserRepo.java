package com.alistats.discorki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alistats.discorki.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
}
