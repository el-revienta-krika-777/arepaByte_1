package com.projecto.ecomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long pagoId;
    private Long pedidoId;
    private Double monto;
    private String estado;
    private String metodoPago;
}
