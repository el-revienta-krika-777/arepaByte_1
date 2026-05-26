package com.projecto.ecomarket.client;

import com.projecto.ecomarket.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogoClient {

    private final WebClient catalogoWebClient;

    public Optional<ProductoDTO> buscarProducto(Long productoId) {
        try {
            ProductoDTO producto = catalogoWebClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block();
            return Optional.ofNullable(producto);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al conectar con codigoms_catalogo: {}", e.getMessage());
            throw new RuntimeException("El servicio de catálogo no está disponible");
        }
    }
}
