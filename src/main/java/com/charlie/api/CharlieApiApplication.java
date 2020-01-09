package com.charlie.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CharlieApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharlieApiApplication.class, args);
	}
 
}
