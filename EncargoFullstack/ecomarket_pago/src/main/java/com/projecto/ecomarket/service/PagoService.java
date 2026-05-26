package com.projecto.ecomarket.service;

import org.springframework.stereotype.Service;
import com.projecto.ecomarket.client.EnvioClient;
import com.projecto.ecomarket.dto.PagoResponseDTO;
import com.projecto.ecomarket.dto.EnvioDTO;
import com.projecto.ecomarket.model.Pago;
import com.projecto.ecomarket.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final EnvioClient envioClient;

    public PagoService(PagoRepository pagoRepository, EnvioClient envioClient) {
        this.pagoRepository = pagoRepository;
        this.envioClient = envioClient;
    }

    private PagoResponseDTO mapToDTO(Pago pago) {
        return PagoResponseDTO.builder()
                .pagoId(pago.getId())
                .pedidoId(pago.getPedidoId())
                .monto(pago.getMonto())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .build();
    }

    public PagoResponseDTO procesarPago(Pago pago) {
        pago.setEstado("COMPLETADO");
        pago.setTransaccionId("TXN-" + System.currentTimeMillis());
        Pago pagoGuardado = pagoRepository.save(pago);

        // Lógica de envío
        EnvioDTO envio = new EnvioDTO();
        
        // Le pasamos el ID del pedido que se acaba de pagar
        envio.setPedidoId(pagoGuardado.getPedidoId()); 
        envio.setDireccionEntrega("Dirección de prueba"); // Idealmente esto vendría en el Pago o Pedido
        envio.setTransportista("prueba");
        
        envioClient.crearEnvio(envio); 

        return mapToDTO(pagoGuardado);
    }

    public PagoResponseDTO obtenerPagoPorPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para este pedido"));
        return mapToDTO(pago);
    }
}