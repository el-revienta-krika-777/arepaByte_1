package com.projecto.ecomarket.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Boolean activo;
}