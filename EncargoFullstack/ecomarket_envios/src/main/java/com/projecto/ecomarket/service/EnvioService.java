package com.projecto.ecomarket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projecto.ecomarket.client.PedidoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.EnvioResponseDTO;
import com.projecto.ecomarket.model.Envio;
import com.projecto.ecomarket.repository.EnvioRepository;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final PedidoClient pedidoClient;
    private final UsuarioClient usuarioClient;

    public EnvioService(EnvioRepository envioRepository, PedidoClient pedidoClient, UsuarioClient usuarioClient) {
        this.envioRepository = envioRepository;
        this.pedidoClient = pedidoClient;
        this.usuarioClient = usuarioClient;
    }

    private EnvioResponseDTO mapToDTO(Envio envio) {
        return EnvioResponseDTO.builder()
                .envioId(envio.getId())
                .pedidoId(envio.getPedidoId())
                .estadoEnvio(envio.getEstadoEnvio())
                .numeroSeguimiento(envio.getNumeroSeguimiento())
                .transportista(envio.getTransportista())
                .fechaEntregaEstimada(envio.getFechaEntregaEstimada() != null ? 
                        envio.getFechaEntregaEstimada().toString() : "Pendiente")
                .build();
    }

    // 1. Crear envío (Logística inicial)
    public EnvioResponseDTO crearEnvio(Envio envio) {
        // Validar que el pedido exista
        var pedido = pedidoClient.buscarPedido(envio.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Error: El pedido no existe"));

        // Validar que el usuario exista (asumiendo que tu PedidoDTO tiene el método getUsuarioId)
        // usuarioClient.buscarProducto(pedido.getUsuarioId())
        //         .orElseThrow(() -> new RuntimeException("Error: El usuario no existe"));

        envio.setEstadoEnvio("PREPARANDO");
        envio.setFechaEnvio(LocalDateTime.now());
        envio.setFechaEntregaEstimada(LocalDateTime.now().plusDays(3));

        return mapToDTO(envioRepository.save(envio));
    }

    public List<EnvioResponseDTO> obtenerTodosLosEnvios() {
    return envioRepository.findAll().stream()
            .map(this::mapToDTO)
            .toList();
}

    // 2. Actualizar estado (Cuando sale de bodega o se entrega)
    public EnvioResponseDTO actualizarEstado(Long envioId, String nuevoEstado, String seguimiento) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        envio.setEstadoEnvio(nuevoEstado);
        if (seguimiento != null) envio.setNumeroSeguimiento(seguimiento);

        return mapToDTO(envioRepository.save(envio));
    }

    // 3. Consultar por Pedido
    public EnvioResponseDTO consultarPorPedido(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("No hay envío registrado para este pedido"));
        return mapToDTO(envio);
    }
}