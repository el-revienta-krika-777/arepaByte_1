package com.projecto.ecomarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EcomarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomarketApplication.class, args);
	}

}
