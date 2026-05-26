package com.projecto.ecomarket.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.dto.PedidoRequestDTO;
import com.projecto.ecomarket.dto.PedidoResponseDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Pedido;
import com.projecto.ecomarket.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CatalogoClient catalogoClient;

    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        ProductoDTO producto = consultarProducto(dto.getProductoId());

        Pedido pedido = new Pedido();
        pedido.setProductoId(producto.getId());
        pedido.setNombreProducto(producto.getNombre());
        pedido.setPrecioUnitario(producto.getPrecio());
        pedido.setCliente(dto.getCliente());
        pedido.setCantidad(dto.getCantidad());
        pedido.setFechaPedido(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado id={} para producto '{}'", guardado.getId(), producto.getNombre());
        return mapToDTO(guardado);
    }

    public List<PedidoResponseDTO> obtenerTodos() {
        return pedidoRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public Optional<PedidoResponseDTO> obtenerPorId(Long id) {
        return pedidoRepository.findById(id).map(this::mapToDTO);
    }

    public List<PedidoResponseDTO> obtenerPorCliente(String cliente) {
        return pedidoRepository.findByCliente(cliente).stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    private ProductoDTO consultarProducto(Long productoId) {
        try {
            return catalogoClient.obtenerProducto(productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El producto con id " + productoId + " no existe en el catalogo");
        } catch (FeignException e) {
            log.error("Error al contactar codigoms_catalogo: {}", e.getMessage());
            throw new RuntimeException("No se pudo verificar el libro. Verifique que codigoms_catalogo este corriendo.");
        }
    }

    private PedidoResponseDTO mapToDTO(Pedido p) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(p.getId());
        dto.setProductoId(p.getProductoId());
        dto.setNombreProducto(p.getNombreProducto());
        dto.setPrecioUnitario(p.getPrecioUnitario());
        dto.setCliente(p.getCliente());
        dto.setCantidad(p.getCantidad());
        dto.setFechaPedido(p.getFechaPedido());
        dto.setTotal(p.getPrecioUnitario().multiply(BigDecimal.valueOf(p.getCantidad())));
        return dto;
    }
}
