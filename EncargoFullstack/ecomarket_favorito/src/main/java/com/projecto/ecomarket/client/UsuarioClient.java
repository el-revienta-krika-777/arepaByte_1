package com.projecto.ecomarket.client;

import com.projecto.ecomarket.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private final WebClient usuariosWebClient;

    public Optional<UsuarioDTO> buscarPorNombre(String nombre) {
        try {
            UsuarioDTO usuario = usuariosWebClient.get()
                    .uri("/api/usuarios/nombre/{nombre}", nombre)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block();
            return Optional.ofNullable(usuario);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al conectar con codigoms_usuarios: {}", e.getMessage());
            throw new RuntimeException("El servicio de usuarios no esta disponible");
        }
    }
}
