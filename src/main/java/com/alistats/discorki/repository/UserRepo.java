package com.alistats.discorki.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alistats.discorki.model.User;

public interface UserRepo extends JpaRepository<User, String> {
}
