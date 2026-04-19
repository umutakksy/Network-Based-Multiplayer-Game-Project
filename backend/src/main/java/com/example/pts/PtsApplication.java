package com.example.pts;

import com.example.pts.repository.LobbyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PtsApplication.class, args);
	}

	@Bean
	public CommandLineRunner cleanupLobbies(LobbyRepository lobbyRepository) {
		return args -> {
			System.out.println("Oturum başlatıldı: Veritabanındaki eski lobiler temizleniyor...");
			lobbyRepository.deleteAll();
		};
	}
}
