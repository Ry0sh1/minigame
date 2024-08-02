package com.dt.minigame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MinigameApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinigameApplication.class, args);
	}

}
