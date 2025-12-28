package com.assessai.scoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ScoringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoringServiceApplication.class, args);
	}

}