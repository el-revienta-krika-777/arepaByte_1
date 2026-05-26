package com.projecto.ecomarket.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoritoResponseDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String usuario;
    private LocalDateTime fechaAgregado;
}