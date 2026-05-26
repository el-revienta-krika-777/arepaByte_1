package com.projecto.ecomarket.service;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.dto.*;
import com.projecto.ecomarket.model.Carrito;
import com.projecto.ecomarket.model.ItemCarrito;
import com.projecto.ecomarket.repository.CarritoRepository;
import com.projecto.ecomarket.repository.ItemCarritoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CatalogoClient catalogoClient;

    @Transactional
    public CarritoResponseDTO crear(String usuario) {
        return mapToDTO(carritoRepository.save(new Carrito(usuario)));
    }

    public Optional<CarritoResponseDTO> obtenerPorId(Long id) {
        return carritoRepository.findById(id).map(this::mapToDTO);
    }

    public List<CarritoResponseDTO> obtenerPorUsuario(String usuario) {
        return carritoRepository.findByUsuario(usuario).stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public CarritoResponseDTO agregarItem(Long carritoId, ItemCarritoRequestDTO dto) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));

        // Llamada HTTP a codigoms_catalogo — aqui ocurre la comunicacion entre microservicios
        ProductoDTO producto = consultarProducto(dto.getProductoId());

        Optional<ItemCarrito> existente = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(dto.getProductoId()))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + dto.getCantidad());
        } else {
            carrito.getItems().add(new ItemCarrito(
                    producto.getId(), producto.getNombre(), producto.getPrecio(),
                    dto.getCantidad(), carrito));
        }

        return mapToDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoResponseDTO quitarItem(Long carritoId, Long itemId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));
        if (!item.getCarrito().getId().equals(carritoId))
            throw new RuntimeException("El item " + itemId + " no pertenece al carrito " + carritoId);
        carrito.getItems().remove(item);
        return mapToDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoResponseDTO vaciar(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));
        carrito.getItems().clear();
        return mapToDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public void eliminar(Long carritoId) {
        carritoRepository.deleteById(carritoId);
    }

    private ProductoDTO consultarProducto(Long productoId) {
        try {
            return catalogoClient.obtenerProducto(productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El libro con id " + productoId + " no existe en el catalogo");
        } catch (FeignException e) {
            log.error("Error al contactar codigoms_catalogo: {}", e.getMessage());
            throw new RuntimeException("No se pudo verificar el libro. Verifique que codigoms_catalogo este corriendo.");
        }
    }

    private CarritoResponseDTO mapToDTO(Carrito c) {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setId(c.getId());
        dto.setUsuario(c.getUsuario());
        dto.setFechaCreacion(c.getFechaCreacion());

        List<ItemCarritoResponseDTO> items = c.getItems().stream().map(i -> {
            ItemCarritoResponseDTO iDto = new ItemCarritoResponseDTO();
            iDto.setId(i.getId());
            iDto.setProductoId(i.getProductoId());
            iDto.setNombreProducto(i.getNombreProducto());
            iDto.setCantidad(i.getCantidad());
            iDto.setPrecioUnitario(i.getPrecioUnitario());
            iDto.setSubtotal(i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())));
            return iDto;
        }).toList();

        dto.setItems(items);
        dto.setTotal(items.stream()
                .map(ItemCarritoResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }
}
