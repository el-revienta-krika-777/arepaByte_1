package com.projecto.ecomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {
    private long id;
    private long productoId;
    private Integer stock;
    private Boolean disponible;
    private Integer stockMinimo;
}
