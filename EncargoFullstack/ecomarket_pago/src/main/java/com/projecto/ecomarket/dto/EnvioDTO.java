package com.projecto.ecomarket.dto;
import lombok.Data;

@Data
public class EnvioDTO {
    private Long id;
    private String direccionEntrega;
    private Long pedidoId;
    private String transportista; // Ejem: "DHL", "FedEx", "Correos"
}