package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.CuponRequestDTO;
import com.projecto.ecomarket.dto.CuponResponseDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Cupon;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // --- 1. GENERACIÓN DE ENTIDADES (MODEL) ---

    public static Cupon unCuponPorcentajeValido() {
        return Cupon.builder()
                .id(faker.number().randomNumber(3, true))
                .codigo("DESC" + faker.number().digits(4))
                .valor(faker.number().randomDouble(2, 5, 50)) // Descuento entre 5% y 50%
                .tipo("PORCENTAJE")
                .fechaExpiracion(LocalDateTime.now().plusDays(15)) // Expira en el futuro
                .activo(true)
                .usoMaximo(100)
                .usosActuales(faker.number().numberBetween(0, 50))
                .build();
    }

    public static Cupon unCuponFijoValido() {
        return Cupon.builder()
                .id(faker.number().randomNumber(3, true))
                .codigo("FIJO" + faker.number().digits(4))
                .valor(faker.number().randomDouble(2, 10, 100)) // Descuento entre $10 y $100
                .tipo("FIJO")
                .fechaExpiracion(LocalDateTime.now().plusMonths(1))
                .activo(true)
                .usoMaximo(50)
                .usosActuales(10)
                .build();
    }

    public static Cupon unCuponExpirado() {
        Cupon cupon = unCuponPorcentajeValido();
        cupon.setFechaExpiracion(LocalDateTime.now().minusDays(5)); // Expiró hace 5 días
        return cupon;
    }

    public static Cupon unCuponAgotado() {
        Cupon cupon = unCuponFijoValido();
        cupon.setUsoMaximo(10);
        cupon.setUsosActuales(10); // Límite alcanzado
        return cupon;
    }

    public static Cupon unCuponInactivo() {
        Cupon cupon = unCuponPorcentajeValido();
        cupon.setActivo(false);
        return cupon;
    }

    // --- 2. GENERACIÓN DE DTOs DE ENTRADA (REQUEST) ---

    public static CuponRequestDTO unCuponRequestDTO(String codigo, Double montoCarrito) {
        return CuponRequestDTO.builder()
                .codigo(codigo)
                .tipo(faker.options().option("PORCENTAJE", "FIJO"))
                .valor(faker.number().randomDouble(2, 10, 50))
                .usoMaximo(100)
                .fechaExpiracion(LocalDateTime.now().plusDays(30))
                .montoCarrito(montoCarrito)
                .build();
    }

    // --- 3. GENERACIÓN DE DTOs EXTERNOS / AUXILIARES ---

    public static ProductoDTO unProductoDTO() {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(faker.number().randomNumber(4, true));
        dto.setNombre(faker.commerce().productName());
        dto.setPrecio(faker.number().randomDouble(2, 50, 1500));
        return dto;
    }
}