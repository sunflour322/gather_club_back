package com.gather_club_back.gather_club_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.gather_club_back.gather_club_back.entity") // Укажите правильный пакет
@EnableJpaRepositories("com.gather_club_back.gather_club_back.repository") // Укажите правильный
@SpringBootApplication
public class GatherClubBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatherClubBackApplication.class, args);
	}

}
