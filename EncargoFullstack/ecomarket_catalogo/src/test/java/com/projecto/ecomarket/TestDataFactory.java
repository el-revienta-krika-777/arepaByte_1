package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.ProductoRequestDTO;
import com.projecto.ecomarket.model.Categoria;
import com.projecto.ecomarket.model.Producto;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // 1. Categoría con Nombre de departamento y una descripción
    public static Categoria unaCategoria() {
        return new Categoria(
                faker.number().randomNumber(2, true),
                faker.commerce().department(), 
                faker.lorem().sentence(5)      
        );
    }

    // 2. Producto adaptado a EcoMarket
    public static Producto unProducto(Categoria categoria) {
        return new Producto(
                faker.number().randomNumber(3, true),
                faker.commerce().productName(), 
                faker.code().ean13(),           
                precio(),
                categoria
        );
    }

    // 3. DTO para las pruebas de tus Controladores/Servicios
    public static ProductoRequestDTO unProductoRequest(Long categoriaId) {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(faker.commerce().productName());
        dto.setGtin(faker.code().ean13());
        dto.setPrecio(precio());
        dto.setCategoriaId(categoriaId);
        return dto;
    }

    // Método auxiliar para el precio (reutilizado del ejemplo)
    private static BigDecimal precio() {
        return BigDecimal.valueOf(faker.number().randomDouble(2, 100, 5000))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
