package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.PedidoRequestDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Pedido;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // --- 1. GENERACIÓN DE ENTIDADES (MODEL) ---

    public static Pedido unPedido(String cliente) {
        Pedido pedido = new Pedido();
        pedido.setId(faker.number().randomNumber(3, true));
        pedido.setProductoId(faker.number().randomNumber(3, true));
        pedido.setNombreProducto(faker.book().title());
        pedido.setPrecioUnitario(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100)));
        pedido.setCliente(cliente != null ? cliente : faker.name().fullName());
        pedido.setCantidad(faker.number().numberBetween(1, 10));
        pedido.setFechaPedido(LocalDateTime.now());
        return pedido;
    }

    // --- 2. GENERACIÓN DE DTOs DE ENTRADA (REQUEST) ---

    public static PedidoRequestDTO unPedidoRequestDTO(Long productoId, String cliente, Integer cantidad) {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setProductoId(productoId);
        dto.setCliente(cliente);
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