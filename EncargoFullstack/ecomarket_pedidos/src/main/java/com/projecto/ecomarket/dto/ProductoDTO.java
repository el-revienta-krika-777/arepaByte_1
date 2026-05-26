package com.projecto.ecomarket.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String gtin;
    private BigDecimal precio;
    private String categoriaNombre;
}
