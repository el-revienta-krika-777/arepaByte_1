package com.projecto.ecomarket.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projecto.ecomarket.dto.ProductoDTO;

@FeignClient(name = "ecomarket-catalogo", url = "http://localhost:8081") 
public interface ProductoClient {
    
    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProductoPorId(@PathVariable("id") Long id);
}