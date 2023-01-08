package com.tournament.lastmanstanding.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class IndexController {
	@GetMapping
	String index() {
		return "Welcome to last man standing";
	}
}
