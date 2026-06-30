package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.EnvioRequestDTO;
import com.projecto.ecomarket.dto.EnvioResponseDTO;
import com.projecto.ecomarket.dto.PedidoDTO;
import com.projecto.ecomarket.dto.UsuarioDTO;
import com.projecto.ecomarket.model.Envio;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // --- 1. GENERACIÓN DE ENTIDADES (MODEL) ---

    public static Envio unEnvioParaCrear() {
        Envio envio = new Envio();
        envio.setPedidoId(faker.number().randomNumber(4, true));
        envio.setDireccionEntrega(faker.address().fullAddress());
        envio.setTransportista(faker.options().option("DHL", "FedEx", "Correos"));
        return envio;
    }

    public static Envio unEnvioCompleto() {
        Envio envio = new Envio();
        envio.setId(faker.number().randomNumber(3, true));
        envio.setPedidoId(faker.number().randomNumber(4, true));
        envio.setDireccionEntrega(faker.address().fullAddress());
        envio.setTransportista(faker.options().option("DHL", "FedEx", "Correos"));
        envio.setNumeroSeguimiento("TRK-" + faker.regexify("[A-Z0-9]{10}"));
        envio.setEstadoEnvio(faker.options().option("PREPARANDO", "EN_TRANSITO", "ENTREGADO", "CANCELADO"));
        envio.setFechaEnvio(LocalDateTime.now().minusDays(1));
        envio.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));
        return envio;
    }

    // --- 2. GENERACIÓN DE DTOs DEL PROPIO MICROSERVICIO ---

    public static EnvioRequestDTO unEnvioRequestDTO() {
        EnvioRequestDTO dto = new EnvioRequestDTO();
        dto.setDireccionEntrega(faker.address().fullAddress());
        dto.setTransportista(faker.options().option("DHL", "FedEx", "Correos"));
        return dto;
    }

    public static EnvioResponseDTO unEnvioResponseDTO() {
        return EnvioResponseDTO.builder()
                .envioId(faker.number().randomNumber(3, true))
                .pedidoId(faker.number().randomNumber(4, true))
                .estadoEnvio("PREPARANDO")
                .numeroSeguimiento("TRK-" + faker.regexify("[A-Z0-9]{10}"))
                .transportista(faker.options().option("DHL", "FedEx", "Correos"))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3).toString())
                .build();
    }

    // --- 3. GENERACIÓN DE DTOS EXTERNOS (FEIGN CLIENTS / MOCKS) ---

    public static PedidoDTO unPedidoDTO(long id) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(id);
        return dto;
    }

    public static UsuarioDTO unUsuarioDTO(long id) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        return dto;
    }
}