package com.projecto.ecomarket.client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.projecto.ecomarket.dto.PedidoDTO;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoClient {

    private final WebClient pedidoWebClient;

    public Optional<PedidoDTO> buscarPedido(Long pedidoId) {
        try {
            PedidoDTO pedido = pedidoWebClient.get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(PedidoDTO.class)
                    .block();
            return Optional.ofNullable(pedido);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al conectar con pedidos: {}", e.getMessage());
            throw new RuntimeException("El servicio de pedidos no está disponible");
        }
    }
}