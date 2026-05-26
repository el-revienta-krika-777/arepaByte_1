package com.projecto.ecomarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequestDTO {
    @NotNull
    private Long productoId;
    @Min(0)
    private Integer stock;
    @Min(0)
    private Integer stockMinimo;
}
