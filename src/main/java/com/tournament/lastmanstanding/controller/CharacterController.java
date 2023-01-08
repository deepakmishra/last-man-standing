package com.tournament.lastmanstanding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tournament.lastmanstanding.model.Character;
import com.tournament.lastmanstanding.repository.CharacterRepository;

@RestController
@RequestMapping("/characters")
public class CharacterController {

	@Autowired
	private CharacterRepository characterRepository;

	@GetMapping("/{characterId}")
	public Character getCharacter(@PathVariable("characterId") Integer characterId) {
		return characterRepository.findById(characterId).get();
	}

	@GetMapping
	public List<Character> getCharacters() {
		return characterRepository.findAll();
	}

	@PostMapping
	public Character createCharacter(@RequestBody Character character) {
		try {
			return characterRepository.save(character);
		} catch (JpaSystemException jse) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Character name already exists");
		}

	}

	@PutMapping("/{characterId}")
	public Character updateCharacter(@PathVariable("characterId") Integer characterId,
			@RequestBody Character character) {
		if (!character.getId().equals(characterId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
		}
		return characterRepository.save(character);
	}
}
