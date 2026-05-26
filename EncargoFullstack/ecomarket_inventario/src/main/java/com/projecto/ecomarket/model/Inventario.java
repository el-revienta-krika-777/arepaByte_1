package com.projecto.ecomarket.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId;

    @Min(value = 0, message = "El stock no puede ser menor a cero")
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean disponible;

    @Column(nullable = false)
    private Integer stockMinimo;
}
