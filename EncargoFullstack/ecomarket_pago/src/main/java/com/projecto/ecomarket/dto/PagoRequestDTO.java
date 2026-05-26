package com.projecto.ecomarket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "el metodo de pago es obligatorio")
    private String metodoPago;

}
