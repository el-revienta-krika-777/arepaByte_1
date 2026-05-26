package com.projecto.ecomarket.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemCarritoResponseDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}