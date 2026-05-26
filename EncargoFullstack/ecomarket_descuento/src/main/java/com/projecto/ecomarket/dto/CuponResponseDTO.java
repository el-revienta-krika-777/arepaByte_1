package com.projecto.ecomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuponResponseDTO {
    private String codigo;
    private String descripcion; 
    private Boolean valido;
    private Double montoDescuento; 
}
