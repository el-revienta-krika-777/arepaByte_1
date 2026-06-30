package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.ItemCarritoRequestDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Carrito;
import com.projecto.ecomarket.model.ItemCarrito;
import net.datafaker.Faker;

import java.math.BigDecimal;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // --- 1. GENERACIÓN DE ENTIDADES (MODEL) ---

    public static Carrito unCarritoVacio(String usuario) {
        Carrito carrito = new Carrito(usuario);
        carrito.setId(faker.number().randomNumber(3, true));
        return carrito;
    }

    public static Carrito unCarritoConItems(String usuario) {
        Carrito carrito = unCarritoVacio(usuario);
        
        // Agregamos un par de items por defecto
        ItemCarrito item1 = unItemCarrito(carrito, 101L);
        item1.setId(1L);
        
        ItemCarrito item2 = unItemCarrito(carrito, 102L);
        item2.setId(2L);
        
        carrito.getItems().add(item1);
        carrito.getItems().add(item2);
        
        return carrito;
    }

    public static ItemCarrito unItemCarrito(Carrito carrito, Long productoId) {
        BigDecimal precio = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100));
        return new ItemCarrito(
                productoId,
                faker.book().title(), // Como en tu código dice "El libro con id...", usamos títulos de libros
                precio,
                faker.number().numberBetween(1, 5),
                carrito
        );
    }

    // --- 2. GENERACIÓN DE DTOs DE ENTRADA (REQUEST) ---

    public static ItemCarritoRequestDTO unItemCarritoRequestDTO(Long productoId, Integer cantidad) {
        ItemCarritoRequestDTO dto = new ItemCarritoRequestDTO();
        dto.setProductoId(productoId);
        dto.setCantidad(cantidad);
        return dto;
    }

    // --- 3. GENERACIÓN DE DTOs EXTERNOS (FEIGN CLIENTS) ---

    public static ProductoDTO unProductoDTO(Long id) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre(faker.book().title());
        dto.setGtin(faker.code().ean13());
        dto.setPrecio(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100)));
        dto.setCategoriaNombre("Libros");
        return dto;
    }
}