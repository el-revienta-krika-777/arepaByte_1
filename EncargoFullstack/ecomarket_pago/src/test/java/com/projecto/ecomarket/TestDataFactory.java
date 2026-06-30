package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.EnvioDTO;
import com.projecto.ecomarket.dto.PagoRequestDTO;
import com.projecto.ecomarket.dto.PagoResponseDTO;
import com.projecto.ecomarket.model.Pago;
import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    // --- 1. GENERACIÓN DE ENTIDADES (MODEL) ---

    public static Pago unPagoParaProcesar() {
        Pago pago = new Pago();
        pago.setPedidoId(faker.number().randomNumber(4, true));
        pago.setMonto(faker.number().randomDouble(2, 10, 2000)); // Monto entre 10 y 2000 con 2 decimales
        pago.setMetodoPago(faker.options().option("TARJETA", "TRANSFERENCIA", "PAYPAL"));
        pago.setEstado("PENDIENTE"); // Estado inicial antes de pasar por el servicio
        return pago;
    }

    public static Pago unPagoCompleto() {
        Pago pago = new Pago();
        pago.setId(faker.number().randomNumber(3, true));
        pago.setPedidoId(faker.number().randomNumber(4, true));
        pago.setMonto(faker.number().randomDouble(2, 10, 2000));
        pago.setMetodoPago(faker.options().option("TARJETA", "TRANSFERENCIA", "PAYPAL"));
        pago.setEstado(faker.options().option("PENDIENTE", "COMPLETADO", "RECHAZADO"));
        pago.setTransaccionId("TXN-" + faker.number().randomNumber(10, true));
        return pago;
    }

    // --- 2. GENERACIÓN DE DTOs PROPIOS ---

    public static PagoRequestDTO unPagoRequestDTO() {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setMetodoPago(faker.options().option("TARJETA", "TRANSFERENCIA", "PAYPAL"));
        return dto;
    }

    public static PagoResponseDTO unPagoResponseDTO() {
        return PagoResponseDTO.builder()
                .pagoId(faker.number().randomNumber(3, true))
                .pedidoId(faker.number().randomNumber(4, true))
                .monto(faker.number().randomDouble(2, 10, 2000))
                .estado("COMPLETADO")
                .metodoPago(faker.options().option("TARJETA", "TRANSFERENCIA", "PAYPAL"))
                .build();
    }

    // --- 3. GENERACIÓN DE DTOs EXTERNOS (MOCKS DE FEIGN/WEBCLIENT) ---

    public static EnvioDTO unEnvioDTO(Long pedidoId) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(faker.number().randomNumber(3, true));
        dto.setPedidoId(pedidoId);
        dto.setDireccionEntrega(faker.address().fullAddress());
        dto.setTransportista(faker.options().option("DHL", "FedEx", "Correos"));
        return dto;
    }
}