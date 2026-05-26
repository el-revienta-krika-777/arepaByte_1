package com.projecto.ecomarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PagoApplication  {

	public static void main(String[] args) {
		SpringApplication.run(PagoApplication.class, args);
	}

	@Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8088") 
                .build();
    }

}
