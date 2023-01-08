package com.tournament.lastmanstanding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tournament.lastmanstanding.model.User;
import com.tournament.lastmanstanding.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/{userId}")
	public User getUser(@PathVariable("userId") Integer userId) {
		return userRepository.findById(userId).get();
	}

	@GetMapping
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@PostMapping
	public User createUser(@RequestBody User user) {
		try {
			return userRepository.save(user);
		} catch (JpaSystemException jse) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
		}
	}
}
