package com.projecto.ecomarket.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private String cliente;
    private Integer cantidad;
    private BigDecimal total;
    private LocalDateTime fechaPedido;
}
