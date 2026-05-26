package com.projecto.ecomarket.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "envios")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId; // Relación con el pedido pagado

    @Column(nullable = false)
    private String direccionEntrega;

    private String transportista; // Por ejem: "DHL", "FedEx", "Correos"
    
    private String numeroSeguimiento;

    @Column(nullable = false)
    private String estadoEnvio; // "PREPARANDO", "EN_TRANSITO", "ENTREGADO", "CANCELADO"

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntregaEstimada;
}