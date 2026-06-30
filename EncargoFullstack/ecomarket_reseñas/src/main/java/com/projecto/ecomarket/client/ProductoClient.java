package com.projecto.ecomarket.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "producto-service", url = "http://localhost:8081")
public interface ProductoClient {
    @GetMapping("/api/productos/{id}")
    Object obtenerProducto(@PathVariable("id") Long id);
}
