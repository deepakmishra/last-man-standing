package com.tournament.lastmanstanding.controller;

import com.tournament.lastmanstanding.model.Game;
import com.tournament.lastmanstanding.model.Player;
import com.tournament.lastmanstanding.repository.GameRepository;
import com.tournament.lastmanstanding.repository.PlayerRepository;
import com.tournament.lastmanstanding.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.OperationNotSupportedException;
import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private GameRepository gameRepository;

	@GetMapping("/{playerId}")
	public Player getPlayer (@PathVariable("playerId") Integer playerId) {
		return playerRepository.findById(playerId).get();
	}

	@GetMapping
	public List <Player> getPlayers (@RequestParam("gameId") Integer gameId) {
		if (!gameRepository.existsById(gameId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
		}
		return playerRepository.findAllPlayers(gameId);
	}

	@PostMapping
	public Player createPlayer (@RequestBody Player player) {
		Game game = player.getGame();
		long totalPlayers = playerRepository.countAllPlayers(game.getId());
		if (!game.getStatus().equals(Game.Status.WAITING) || game.getMaximumPlayer().longValue() == totalPlayers) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Game cannot be joined");
		}
		player.setHealth(player.getCharacter().getMaximumHealth());
		return playerRepository.save(player);
	}

	@PutMapping("/{playerId}")
	public Player updatePlayer (@PathVariable("playerId") Integer playerId, @RequestBody Player player) {
		return playerRepository.save(player);
	}

	@PostMapping("/attack")
	public List <Player> attackPlayers (@RequestParam("attackerId") Integer attackerId,
	                                    @RequestParam("victimId") Integer victimId) throws OperationNotSupportedException {
		return new GameService(gameRepository, playerRepository).attack(attackerId, victimId);
	}
}
