package com.projecto.ecomarket.client;

import com.projecto.ecomarket.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "codigoms-catalogo", url = "${catalogo.service.url}")
public interface CatalogoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable Long id);
}

