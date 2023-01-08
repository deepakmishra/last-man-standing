package com.tournament.lastmanstanding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tournament.lastmanstanding.model.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {
	List<Game> findByStatus(Game.Status status);
}