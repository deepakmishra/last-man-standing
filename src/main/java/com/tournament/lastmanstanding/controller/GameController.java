package com.tournament.lastmanstanding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tournament.lastmanstanding.model.Game;
import com.tournament.lastmanstanding.repository.GameRepository;
import com.tournament.lastmanstanding.repository.PlayerRepository;
import com.tournament.lastmanstanding.service.GameService;

@RestController
@RequestMapping("/games")
public class GameController {

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private PlayerRepository playerRepository;

	@GetMapping("/{gameId}")
	public Game getGame(@PathVariable("gameId") Integer gameId) {
		return gameRepository.findById(gameId).get();
	}

	@GetMapping
	public List<Game> getGames(@RequestParam(required = false) Game.Status status) {
		if (status == null) {
			return gameRepository.findAll();
		}
		return gameRepository.findByStatus(status);
	}

	@PostMapping
	public Game createGame(@RequestBody Game game) {
		game.setStatus(Game.Status.WAITING);
		return gameRepository.save(game);
	}

	@PutMapping("/{gameId}")
	public Game updateGame(@PathVariable("gameId") Integer gameId, @RequestBody Game game) {
		if (!game.getId().equals(gameId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
		}
		return gameRepository.save(game);
	}

	@PutMapping("/{gameId}/{status}")
	public Game updateGameStatus(@PathVariable("gameId") Integer gameId, @PathVariable("status") Game.Status status)
			throws ResponseStatusException {
		Game game = gameRepository.findById(gameId).get();
		return new GameService(gameRepository, playerRepository).updateGameStatus(game, status);
	}
}
