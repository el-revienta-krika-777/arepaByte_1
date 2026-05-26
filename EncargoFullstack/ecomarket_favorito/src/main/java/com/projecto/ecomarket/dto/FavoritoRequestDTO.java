package com.projecto.ecomarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoritoRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotBlank(message = "El usuario no puede estar vacío")
    private String usuario;
}

