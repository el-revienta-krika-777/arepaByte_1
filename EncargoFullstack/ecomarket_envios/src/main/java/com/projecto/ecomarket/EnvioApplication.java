package com.projecto.ecomarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class EnvioApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvioApplication.class, args);
	}
	
	@Bean
public WebClient webClient() {
    return WebClient.builder()
            .baseUrl("http://localhost:8085") 
            .build();
}
}
