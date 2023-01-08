package com.tournament.lastmanstanding.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.lastmanstanding.model.Game;
import com.tournament.lastmanstanding.repository.GameRepository;

@TestInstance(value = Lifecycle.PER_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private GameRepository gameRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		gameRepository.deleteAll();
	}

	@Test
	public void testCrud() throws Exception {
		Game game = new Game(2, 5);

		String resultAllInitial = this.mvc
				.perform(MockMvcRequestBuilders.get("/games/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountInitial = objectMapper.readValue(resultAllInitial, Game[].class).length;

		String resultCreated = this.mvc
				.perform(MockMvcRequestBuilders.post("/games/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(game)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();

		Game gameCreated = objectMapper.readValue(resultCreated, Game.class);
		Assert.notNull(gameCreated.getId(), "Game ID should not be null");

		String resultRead = this.mvc
				.perform(MockMvcRequestBuilders.get("/games/" + gameCreated.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		Game gameRead = objectMapper.readValue(resultRead, Game.class);
		Assert.isTrue(gameRead.getStatus().equals(Game.Status.WAITING), "Game status should be waiting when created");

		gameRead.setMaximumPlayer(2 * game.getMaximumPlayer());
		String resultUpdated = this.mvc
				.perform(MockMvcRequestBuilders.put("/games/" + gameRead.getId()).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(gameRead)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		Game gameUpdated = objectMapper.readValue(resultUpdated, Game.class);
		Assert.isTrue(gameUpdated.getId().equals(gameRead.getId()), "Game id returned should be same");
		Assert.isTrue(gameUpdated.getMaximumPlayer().equals(2 * gameCreated.getMaximumPlayer()),
				"Game Max Health not updated");

		this.mvc.perform(MockMvcRequestBuilders.delete("/games/" + gameCreated.getId()))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

		String resultAllFinal = this.mvc
				.perform(MockMvcRequestBuilders.get("/games/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountFinal = objectMapper.readValue(resultAllFinal, Game[].class).length;
		Assert.isTrue(totalCountInitial + 1 == totalCountFinal, "Game not made");
	}

	@Test
	public void testStatusChangeWithoutPlayer() throws Exception {
		Game game = new Game(2, 5);

		String resultCreated = this.mvc
				.perform(MockMvcRequestBuilders.post("/games/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(game)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();

		Game gameCreated = objectMapper.readValue(resultCreated, Game.class);

		this.mvc.perform(MockMvcRequestBuilders.put("/games/" + gameCreated.getId() + "/READY")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

	}
}
