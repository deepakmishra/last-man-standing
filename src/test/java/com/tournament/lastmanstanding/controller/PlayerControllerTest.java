package com.tournament.lastmanstanding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.lastmanstanding.model.Character;
import com.tournament.lastmanstanding.model.Game;
import com.tournament.lastmanstanding.model.Player;
import com.tournament.lastmanstanding.model.User;
import com.tournament.lastmanstanding.repository.CharacterRepository;
import com.tournament.lastmanstanding.repository.GameRepository;
import com.tournament.lastmanstanding.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

@TestInstance(value = Lifecycle.PER_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CharacterRepository characterRepository;
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private GameRepository playerRepository;
	private Game game, game2;
	private User[] users;
	private Character[] characters;

	@BeforeEach
	public void setUp () throws Exception {
		userRepository.deleteAll();
		characterRepository.deleteAll();
		gameRepository.deleteAll();
		playerRepository.deleteAll();

		game = new Game(2, 5);
		String resultCreated = this.mvc
				                       .perform(MockMvcRequestBuilders.post("/games/")
						                                .accept(MediaType.APPLICATION_JSON)
						                                .contentType(MediaType.APPLICATION_JSON)
						                                .content(objectMapper.writeValueAsString(game)))
				                       .andReturn().getResponse()
				                       .getContentAsString();
		game = objectMapper.readValue(resultCreated, Game.class);

		game2 = new Game(2, 5);
		resultCreated = this.mvc
				                .perform(MockMvcRequestBuilders.post("/games/")
						                         .accept(MediaType.APPLICATION_JSON)
						                         .contentType(MediaType.APPLICATION_JSON)
						                         .content(objectMapper.writeValueAsString(game2)))
				                .andReturn().getResponse()
				                .getContentAsString();
		game2 = objectMapper.readValue(resultCreated, Game.class);

		User user1 = new User("Deepak");
		User user2 = new User("Shreyas");
		User user3 = new User("Ashutosh");
		users = new User[]{user1, user2, user3};
		for (int i = 0; i < users.length; i++) {
			User user = users[i];
			resultCreated = this.mvc
					                .perform(MockMvcRequestBuilders.post("/users/")
							                         .accept(MediaType.APPLICATION_JSON)
							                         .contentType(MediaType.APPLICATION_JSON)
							                         .content(objectMapper.writeValueAsString(user)))
					                .andReturn().getResponse()
					                .getContentAsString();

			user = objectMapper.readValue(resultCreated, User.class);
			users[i] = user;
		}

		Character character1 = new Character("Ken", 100, 50, 2);
		Character character2 = new Character("Ryu", 150, 25, 16);
		characters = new Character[]{character1, character2};
		for (int i = 0; i < characters.length; i++) {
			Character character = characters[i];
			resultCreated = this.mvc
					                .perform(MockMvcRequestBuilders.post("/characters/")
							                         .accept(MediaType.APPLICATION_JSON)
							                         .contentType(MediaType.APPLICATION_JSON)
							                         .content(objectMapper.writeValueAsString(character)))
					                .andReturn().getResponse()
					                .getContentAsString();

			character = objectMapper.readValue(resultCreated, Character.class);
			characters[i] = character;
		}
	}

	@Test
	public void testCrud () throws Exception {

		Player player1 = new Player(users[0], characters[0], game);
		Player player2 = new Player(users[1], characters[0], game);
		Player player3 = new Player(users[2], characters[1], game);

		Player[] players = new Player[]{player1, player2, player3};

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/READY")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		for (Player player : players) {
			String resultCreated = this.mvc
					                       .perform(MockMvcRequestBuilders.post("/players/").accept(MediaType.APPLICATION_JSON)
							                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(player)))
					                       .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					                       .getContentAsString();

			Player playerCreated = objectMapper.readValue(resultCreated, Player.class);
			Assert.notNull(playerCreated.getId(), "Player ID should not be null");
		}

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/READY")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}

	@Test
	public void testFightSetup () throws Exception {
		String attackUrlFormat = "/players/attack/?attackerId=%d&victimId=%d";
		String attackUrl;

		Player player1 = new Player(users[0], characters[0], game);
		Player player2 = new Player(users[1], characters[1], game);
		Player player3 = new Player(users[2], characters[0], game);
		Player player4 = new Player(users[2], characters[1], game2);

		Player[] players = new Player[]{player1, player2, player3, player4};

		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			String resultCreated = this.mvc
					                       .perform(MockMvcRequestBuilders.post("/players/")
							                                .accept(MediaType.APPLICATION_JSON)
							                                .contentType(MediaType.APPLICATION_JSON)
							                                .content(objectMapper.writeValueAsString(player)))
					                       .andReturn().getResponse().getContentAsString();

			player = objectMapper.readValue(resultCreated, Player.class);
			players[i] = player;
		}

		// Self attack
		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		// Not ongoing
		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/READY")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

		// Not ongoing
		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/ONGOING")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

		// Not in same game
		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[3].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		// Accepted
		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}

	@Test
	public void testFight () throws Exception {
		String attackUrlFormat = "/players/attack/?attackerId=%d&victimId=%d";
		String attackUrl;

		Player player1 = new Player(users[0], characters[0], game);
		Player player2 = new Player(users[1], characters[1], game);
		Player player3 = new Player(users[2], characters[0], game);

		Player[] players = new Player[]{player1, player2, player3};

		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			String resultCreated = this.mvc
					                       .perform(MockMvcRequestBuilders.post("/players/")
							                                .accept(MediaType.APPLICATION_JSON)
							                                .contentType(MediaType.APPLICATION_JSON)
							                                .content(objectMapper.writeValueAsString(player)))
					                       .andReturn().getResponse().getContentAsString();

			player = objectMapper.readValue(resultCreated, Player.class);
			players[i] = player;
		}

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/READY")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + game.getId() + "/ONGOING")
				                 .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

		// 100, 50, 2
		// 150, 25,16
		// 100, 50, 2

		// 100,150,100

		attackUrl = String.format(attackUrlFormat, players[0].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 100,116,100

		attackUrl = String.format(attackUrlFormat, players[1].getId(), players[2].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 100,116,77

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 52,116,77

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 4,116,77

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,116,77

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		// 0,116,77

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[0].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		// 0,116,77

		attackUrl = String.format(attackUrlFormat, players[1].getId(), players[2].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,116,54

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,82,54

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,48,54

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,14,54

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		// 0,0,54

		attackUrl = String.format(attackUrlFormat, players[2].getId(), players[1].getId());
		this.mvc.perform(MockMvcRequestBuilders.post(attackUrl))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		// 0,0,54

		String result = this.mvc.perform(MockMvcRequestBuilders.get("/players/?gameId=" + game.getId()))
				                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				                .andReturn().getResponse().getContentAsString();

		Player[] finalPlayers = objectMapper.readValue(result, Player[].class);

		int countOfWinner = 0;
		Player winner = null;

		for (Player player : finalPlayers) {
			if (player.getHealth().intValue() > 0) {
				countOfWinner++;
				winner = player;
			}
		}

		Assert.isTrue(countOfWinner == 1, "There can be only one winner");
		Assert.isTrue(players[2].getId().equals(winner.getId()), "Wrong winner");
	}
}
