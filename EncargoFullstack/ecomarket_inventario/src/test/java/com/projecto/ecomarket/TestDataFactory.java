package com.projecto.ecomarket;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.datafaker.Faker;

import com.projecto.ecomarket.dto.InventarioRequestDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Inventario;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static Inventario unInventario(Long productoId) {
        int stock = faker.number().numberBetween(10, 100);
        return Inventario.builder()
                .id(faker.number().numberBetween(1L, 999L))
                .productoId(productoId)
                .stock(stock)
                .disponible(stock > 0)
                .stockMinimo(faker.number().numberBetween(1, 5))
                .build();
    }

    public static InventarioRequestDTO unInventarioRequest(Long productoId) {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setProductoId(productoId);
        dto.setStock(faker.number().numberBetween(10, 50));
        dto.setStockMinimo(faker.number().numberBetween(1, 5));
        return dto;
    }

    public static ProductoDTO unProductoDTO(Long productoId) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(productoId);
        dto.setNombre(faker.commerce().productName());
        dto.setGtin(faker.code().ean13());
        dto.setPrecio(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 10000)).setScale(2, RoundingMode.HALF_UP));
        dto.setCategoriaNombre(faker.commerce().department());
        return dto;
    }
}
