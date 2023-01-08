package com.tournament.lastmanstanding.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Character {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(unique = true)
	private String name;

	@Column(name = "maximum_health")
	private Integer maximumHealth;

	private Integer attack;

	private Integer defense;

	public Character(String name, Integer maximumHealth, Integer attack, Integer defense) {
		this.name = name;
		this.maximumHealth = maximumHealth;
		this.attack = attack;
		this.defense = defense;
	}

	public Character() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMaximumHealth() {
		return maximumHealth;
	}

	public void setMaximumHealth(Integer maximumHealth) {
		this.maximumHealth = maximumHealth;
	}

	public Integer getAttack() {
		return attack;
	}

	public void setAttack(Integer attack) {
		this.attack = attack;
	}

	public Integer getDefense() {
		return defense;
	}

	public void setDefense(Integer defense) {
		this.defense = defense;
	}

}