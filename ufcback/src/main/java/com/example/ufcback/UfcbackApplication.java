package com.example.ufcback;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@EnableCaching
@SpringBootApplication
public class UfcbackApplication {

	static {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
	}

	@PostConstruct
	public void init() {
		// 애플리케이션 전역 타임존을 한국 시간(KST)으로 고정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(UfcbackApplication.class, args);
	}
}
