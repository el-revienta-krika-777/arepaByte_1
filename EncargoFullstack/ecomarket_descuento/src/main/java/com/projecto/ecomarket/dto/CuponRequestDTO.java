package com.projecto.ecomarket.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuponRequestDTO {
    private String codigo;
    private String tipo;           // Los tipos son "PORCENTAJE" o "FIJO"
    private Double valor;          
    private Integer usoMaximo;
    private LocalDateTime fechaExpiracion;
    private Double montoCarrito;   
}