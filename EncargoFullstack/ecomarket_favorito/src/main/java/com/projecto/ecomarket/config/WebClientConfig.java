package com.projecto.ecomarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${catalogo.url}")
    private String catalogoUrl;

    @Value("${usuarios.url}")
    private String usuariosUrl;

    @Bean
    public WebClient catalogoWebClient() {
        return WebClient.builder()
                .baseUrl(catalogoUrl)
                .build();
    }

    @Bean
    public WebClient usuariosWebClient() {
        return WebClient.builder()
                .baseUrl(usuariosUrl)
                .build();
    }
}
