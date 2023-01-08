package com.tournament.lastmanstanding.service;

import com.tournament.lastmanstanding.model.Game;
import com.tournament.lastmanstanding.model.Player;
import com.tournament.lastmanstanding.repository.GameRepository;
import com.tournament.lastmanstanding.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class GameService {
	private GameRepository gameRepository;

	private PlayerRepository playerRepository;

	public GameService (GameRepository gameRepository, PlayerRepository playerRepository) {
		this.gameRepository = gameRepository;
		this.playerRepository = playerRepository;
	}

	public Game updateGameStatus (Game game, Game.Status status) throws ResponseStatusException {
		if (game.getStatus().equals(Game.Status.ENDED)) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Game already ended");
		}

		long aliveCount = playerRepository.countAlivePlayers(game.getId());

		if (game.getStatus().equals(Game.Status.ONGOING)) {
			if (!status.equals(Game.Status.ENDED)) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Wrong status");
			}

			if (aliveCount > 1) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
						"Cannot end the game because more than 1 players are alive");
			}
		}

		if (game.getStatus().equals(Game.Status.READY)) {
			if (!status.equals(Game.Status.ONGOING)) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Wrong status");
			}
		}

		if (game.getStatus().equals(Game.Status.WAITING)) {
			if (!status.equals(Game.Status.READY)) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Wrong status");
			}

			if (aliveCount < game.getMinimumPlayer().intValue()) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
						"Cannot start the game unless minimum players criteria is statisfied");
			}
		}

		game.setStatus(status);
		gameRepository.save(game);

		return game;
	}

	public List <Player> attack (Integer attackerId, Integer victimId) throws ResponseStatusException {
		if (attackerId.equals(victimId)) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Players are same");
		}

		Player attacker = playerRepository.findById(attackerId).get();
		Player victim = playerRepository.findById(victimId).get();

		if (!attacker.getGame().getId().equals(victim.getGame().getId())) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Players are not in same game");
		}

		Game game = attacker.getGame();

		if (!game.getStatus().equals(Game.Status.ONGOING)) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Game not in ongoing state");
		}

		if (attacker.getHealth().intValue() <= 0) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Attacker is dead");
		}
		if (victim.getHealth().intValue() <= 0) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Victim is already dead");
		}

		int diff = Math.max(attacker.getCharacter().getAttack() - victim.getCharacter().getDefense(), 0);

		victim.setHealth(Math.max(victim.getHealth() - diff, 0));

		playerRepository.save(victim);

		if (victim.getHealth().intValue() == 0) {
			long aliveCount = playerRepository.countAlivePlayers(game.getId());
			if (aliveCount <= 1) {
				updateGameStatus(game, Game.Status.ENDED);
			}
		}

		return playerRepository.findAllPlayers(game.getId());
	}

}
