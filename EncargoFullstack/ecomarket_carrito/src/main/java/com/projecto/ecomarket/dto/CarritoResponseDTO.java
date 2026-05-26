package com.projecto.ecomarket.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarritoResponseDTO {
    private Long id;
    private String usuario;
    private LocalDateTime fechaCreacion;
    private List<ItemCarritoResponseDTO> items;
    private BigDecimal total;
}
