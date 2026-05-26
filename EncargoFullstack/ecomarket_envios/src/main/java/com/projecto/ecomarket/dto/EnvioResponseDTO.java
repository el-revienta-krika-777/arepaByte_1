package com.projecto.ecomarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDTO {
    private Long envioId;
    private Long pedidoId;
    private String estadoEnvio;
    private String numeroSeguimiento;
    private String transportista;
    private String fechaEntregaEstimada;
}