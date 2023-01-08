package com.tournament.lastmanstanding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tournament.lastmanstanding.model.Character;

public interface CharacterRepository extends JpaRepository<Character, Integer> {
	Character findByName(String name);
}