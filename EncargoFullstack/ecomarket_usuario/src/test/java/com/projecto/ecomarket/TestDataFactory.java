package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.UsuarioRequestDTO;
import com.projecto.ecomarket.model.Usuario;

import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static Usuario unUsuario() {
        return new Usuario(
                faker.number().numberBetween(1L, 999L),
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                true
        );
    }

    public static UsuarioRequestDTO unUsuarioRequest() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre(faker.name().username());
        dto.setEmail(faker.internet().emailAddress());
        dto.setTelefono(faker.phoneNumber().phoneNumber());
        return dto;
    }
}