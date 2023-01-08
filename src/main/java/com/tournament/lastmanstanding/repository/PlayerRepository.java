package com.tournament.lastmanstanding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tournament.lastmanstanding.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
	@Query("select p from Player p where p.game.id = :gameId and p.health > 0")
	List<Player> findAlivePlayers(@Param("gameId") Integer gameId);

	@Query("select p from Player p where p.game.id = :gameId and p.health <= 0")
	List<Player> findDeadPlayers(@Param("gameId") Integer gameId);

	@Query("select p from Player p where p.game.id = :gameId")
	List<Player> findAllPlayers(Integer gameId);

	@Query("select count(p) from Player p where p.game.id = :gameId and p.health > 0")
	long countAlivePlayers(@Param("gameId") Integer gameId);

	@Query("select count(p) from Player p where p.game.id = :gameId and p.health <= 0")
	long countDeadPlayers(@Param("gameId") Integer gameId);

	@Query("select count(p) from Player p where p.game.id = :gameId")
	long countAllPlayers(Integer gameId);
}