package com.tournament.lastmanstanding.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "game_id", "user_id" }) })
@Entity
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@JoinColumn(name = "user_id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private User user;

	@JoinColumn(name = "character_id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Character character;

	@JoinColumn(name = "game_id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Game game;

	public Player(User user, Character character, Game game) {
		this.user = user;
		this.character = character;
		this.game = game;
		this.health = character.getMaximumHealth();
	}

	public Player() {
	}

	private Integer health;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public Character getCharacter() {
		return character;
	}

	public Game getGame() {
		return game;
	}

	public Integer getHealth() {
		return health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}
}
