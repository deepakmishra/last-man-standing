package com.tournament.lastmanstanding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tournament.lastmanstanding.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsername(String username);
}