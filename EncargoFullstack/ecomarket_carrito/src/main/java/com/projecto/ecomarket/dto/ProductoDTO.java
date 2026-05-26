package com.projecto.ecomarket.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String gtin;
    private BigDecimal precio;
    private String categoriaNombre;
}

