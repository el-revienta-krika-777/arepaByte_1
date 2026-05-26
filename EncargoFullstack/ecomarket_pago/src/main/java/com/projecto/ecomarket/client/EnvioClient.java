package com.projecto.ecomarket.client;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.projecto.ecomarket.dto.EnvioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioClient {

    private final WebClient webClient;

    public Optional<EnvioDTO> buscarEnvio(Long envioId) {
        try {
            EnvioDTO envio = webClient.get()
                    .uri("/api/envios/{id}", envioId) 
                    .retrieve()
                    .bodyToMono(EnvioDTO.class)
                    .block();
            return Optional.ofNullable(envio);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al conectar con envios: {}", e.getMessage());
            throw new RuntimeException("El servicio de envios no está disponible");
        }
    }

    public EnvioDTO crearEnvio(EnvioDTO envioDto) {
        try {
            return webClient.post()
                    .uri("/api/envios/crear") 
                    .bodyValue(envioDto)
                    .retrieve()
                    .bodyToMono(EnvioDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al crear el envío: {}", e.getMessage());
            throw new RuntimeException("No se pudo registrar el envío");
        }
    }
}