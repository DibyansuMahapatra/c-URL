package com.shorturl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CompactUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompactUrlApplication.class, args);
	}
}