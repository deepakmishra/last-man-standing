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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.lastmanstanding.model.Character;
import com.tournament.lastmanstanding.repository.CharacterRepository;

@TestInstance(value = Lifecycle.PER_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class CharacterControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private CharacterRepository characterRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		characterRepository.deleteAll();
	}

	@Test
	public void testUnique() throws Exception {
		Character character1 = new Character("Fiend", 100, 50, 2);
		Character character2 = new Character("Fiend", 150, 25, 16);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/characters/")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder.content(objectMapper.writeValueAsString(character1)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		this.mvc.perform(builder.content(objectMapper.writeValueAsString(character2)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void testCrud() throws Exception {
		Character character1 = new Character("Fiend", 100, 50, 2);
		Character character2 = new Character("Ryu", 150, 25, 16);
		Character character3 = new Character("Ken", 140, 30, 12);
		Character character4 = new Character("Chun Li", 100, 32, 20);
		Character character5 = new Character("Jin Kazama", 150, 28, 20);

		Character[] characters = { character1, character2, character3, character4, character5 };

		String resultAllInitial = this.mvc
				.perform(MockMvcRequestBuilders.get("/characters/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountInitial = objectMapper.readValue(resultAllInitial, Character[].class).length;

		for (Character character : characters) {
			String resultCreated = this.mvc
					.perform(MockMvcRequestBuilders.post("/characters/").accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(character)))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					.getContentAsString();

			Character characterCreated = objectMapper.readValue(resultCreated, Character.class);
			Assert.notNull(characterCreated.getId(), "Character ID should not be null");
			Assert.isTrue(characterCreated.getName().equals(character.getName()), "Character name should be same");

			String resultRead = this.mvc
					.perform(MockMvcRequestBuilders.get("/characters/" + characterCreated.getId())
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					.getContentAsString();
			Character characterRead = objectMapper.readValue(resultRead, Character.class);
			Assert.isTrue(characterRead.getId().equals(characterCreated.getId()),
					"Character id returned should be same");
			Assert.isTrue(characterRead.getName().equals(characterCreated.getName()),
					"Character name returned should be same");

			characterRead.setMaximumHealth(2 * character.getMaximumHealth());
			String resultUpdated = this.mvc
					.perform(MockMvcRequestBuilders.put("/characters/" + characterRead.getId())
							.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(characterRead)))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					.getContentAsString();
			Character characterUpdated = objectMapper.readValue(resultUpdated, Character.class);
			Assert.isTrue(characterUpdated.getId().equals(characterRead.getId()),
					"Character id returned should be same");
			Assert.isTrue(characterUpdated.getName().equals(characterRead.getName()),
					"Character name returned should be same");
			Assert.isTrue(characterUpdated.getMaximumHealth().equals(2 * characterCreated.getMaximumHealth()),
					"Character Max Health not updated");

			this.mvc.perform(MockMvcRequestBuilders.delete("/characters/" + characterCreated.getId()))
					.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}

		String resultAllFinal = this.mvc
				.perform(MockMvcRequestBuilders.get("/characters/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountFinal = objectMapper.readValue(resultAllFinal, Character[].class).length;
		Assert.isTrue(totalCountInitial + characters.length == totalCountFinal, "All characters not made");

	}
}
