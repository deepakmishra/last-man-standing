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
import com.tournament.lastmanstanding.model.User;
import com.tournament.lastmanstanding.repository.UserRepository;

@TestInstance(value = Lifecycle.PER_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserRepository userRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		userRepository.deleteAll();
	}

	@Test
	public void testUnique() throws Exception {
		User user1 = new User("Deepak Mishra");
		User user2 = new User("Deepak Mishra");

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users/")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(builder.content(objectMapper.writeValueAsString(user1)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		this.mvc.perform(builder.content(objectMapper.writeValueAsString(user2)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void testCrud() throws Exception {
		User user1 = new User("Deepak");
		User user2 = new User("Shreyas");
		User user3 = new User("Ashutosh");
		User user4 = new User("Raunak");
		User user5 = new User("Satheesh");
		User user6 = new User("Faizan");
		User user7 = new User("Ashu");

		User[] users = { user1, user2, user3, user4, user5, user6, user7 };

		String resultAllInitial = this.mvc
				.perform(MockMvcRequestBuilders.get("/users/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountInitial = objectMapper.readValue(resultAllInitial, User[].class).length;

		for (User user : users) {
			String resultCreated = this.mvc
					.perform(MockMvcRequestBuilders.post("/users/").accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					.getContentAsString();

			User userCreated = objectMapper.readValue(resultCreated, User.class);
			Assert.notNull(userCreated.getId(), "User ID should not be null");
			Assert.isTrue(userCreated.getUsername().equals(user.getUsername()), "User name should be same");

			String resultRead = this.mvc
					.perform(MockMvcRequestBuilders.get("/users/" + userCreated.getId())
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
					.getContentAsString();
			User userRead = objectMapper.readValue(resultRead, User.class);
			Assert.isTrue(userRead.getUsername().equals(user.getUsername()), "User name returned should be same");

			this.mvc.perform(
					MockMvcRequestBuilders.put("/users/" + userCreated.getId()).accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRead)))
					.andExpect(MockMvcResultMatchers.status().is4xxClientError());

			this.mvc.perform(MockMvcRequestBuilders.delete("/users/" + userCreated.getId()))
					.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}

		String resultAllFinal = this.mvc
				.perform(MockMvcRequestBuilders.get("/users/").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse()
				.getContentAsString();
		int totalCountFinal = objectMapper.readValue(resultAllFinal, User[].class).length;
		Assert.isTrue(totalCountInitial + users.length == totalCountFinal, "All users not made");

	}
}
