package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.ResenaRequestDTO;
import com.projecto.ecomarket.dto.UsuarioDTO;
import com.projecto.ecomarket.model.Resena;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static Resena unaResena() {
        return Resena.builder()
                .id(faker.number().numberBetween(1L, 999L))
                .productoId(faker.number().numberBetween(1L, 999L))
                .usuarioId(faker.number().numberBetween(1L, 999L))
                .calificacion(faker.number().numberBetween(1, 5))
                .comentario(faker.lorem().sentence())
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    public static ResenaRequestDTO unaResenaRequest() {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setProductoId(faker.number().numberBetween(1L, 999L));
        dto.setUsuarioId(faker.number().numberBetween(1L, 999L));
        dto.setCalificacion(faker.number().numberBetween(1, 5));
        dto.setComentario(faker.lorem().sentence());
        return dto;
    }

    public static UsuarioDTO unUsuarioDTO() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(faker.number().numberBetween(1L, 999L));
        dto.setNombre(faker.name().fullName());
        dto.setEmail(faker.internet().emailAddress());
        dto.setTelefono(faker.phoneNumber().phoneNumber());
        dto.setActivo(true);
        return dto;
    }
}