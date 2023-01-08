package com.tournament.lastmanstanding.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Game {

	public enum Status {
		WAITING, READY, ONGOING, ENDED;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "maximum_player")
	private Integer maximumPlayer;

	@Column(name = "minimum_player")
	private Integer minimumPlayer;

	@Enumerated(EnumType.ORDINAL)
	private Status status = Status.WAITING;

	public Game(Integer minimumPlayer, Integer maximumPlayer) {
		this.minimumPlayer = minimumPlayer;
		this.maximumPlayer = maximumPlayer;
	}

	public Game() {
	}

	public Integer getId() {
		return id;
	}

	public Integer getMaximumPlayer() {
		return maximumPlayer;
	}

	public Integer getMinimumPlayer() {
		return minimumPlayer;
	}

	public Status getStatus() {
		return status;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMaximumPlayer(Integer maximumPlayer) {
		this.maximumPlayer = maximumPlayer;
	}

	public void setMinimumPlayer(Integer minimumPlayer) {
		this.minimumPlayer = minimumPlayer;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
