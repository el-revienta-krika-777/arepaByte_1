package com.projecto.ecomarket.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cupones")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ejem: "VERANO2026"

    @Column(nullable = false)
    private Double valor; 

    @Column(nullable = false)
    private String tipo; // "PORCENTAJE" o "FIJO"

    private LocalDateTime fechaExpiracion;
    
    @Column(nullable = false)
    private Boolean activo;

    private Integer usoMaximo;
    private Integer usosActuales;
}
