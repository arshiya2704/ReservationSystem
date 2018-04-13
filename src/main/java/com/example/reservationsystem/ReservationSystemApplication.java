package com.example.reservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.example.reservationsystem.repository")
@SpringBootApplication
public class ReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationSystemApplication.class, args);
	}
}
