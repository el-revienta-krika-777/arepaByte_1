package com.projecto.ecomarket.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projecto.ecomarket.dto.ProductoDTO;

/**
 * CatalogoClient
 *
 * Autor: Prof. Sting Parra Silva
 *
 * Mismo patron que en codigoms_carrito.
 * Tanto carrito como pedidos necesitan consultar libros →
 * ambos tienen su propio CatalogoClient apuntando al mismo servicio.
 * Cada microservicio es independiente y no comparte clientes con otros.
 */
@FeignClient(name = "ecomarket-catalogo", url = "${catalogo.service.url}")
public interface CatalogoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable Long id);
}
