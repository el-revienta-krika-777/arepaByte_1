package com.projecto.ecomarket.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El name debe ser el mismo que definiste en el application.properties de Catalogo
@FeignClient(name = "ecomarket-catalogo", url = "http://localhost:8081") 
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    Object obtenerProductoPorId(@PathVariable("id") Long id);
}