package com.assessai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale du microservice OCR pour AssessAI
 */
@SpringBootApplication
public class OcrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcrServiceApplication.class, args);
	}

}
