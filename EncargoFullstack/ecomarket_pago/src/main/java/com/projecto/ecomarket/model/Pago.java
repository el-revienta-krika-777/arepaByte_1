package com.projecto.ecomarket.model;

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
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId; 

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodoPago; // "TARJETA", "TRANSFERENCIA", "PAYPAL"

    @Column(nullable = false)
    private String estado; // "PENDIENTE", "COMPLETADO", "RECHAZADO"

    private String transaccionId; // ID que devuelve la pasarela de pago (Stripe, PayPal, etc.)
}