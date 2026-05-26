package com.projecto.ecomarket.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioRequestDTO {

    @NotNull(message = "la direccion de entrega es obligatoria")
    private String direccionEntrega;

    @NotNull(message = "el transportista es obligatorio")
    private String transportista;

}
