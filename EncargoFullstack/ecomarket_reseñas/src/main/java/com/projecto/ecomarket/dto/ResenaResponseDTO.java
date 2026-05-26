package com.projecto.ecomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponseDTO {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String nombreUsuario; 
    private int calificacion;
    private String comentario;
    private String fecha;
}
