package com.projecto.ecomarket.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.client.ProductoClient; // <-- Importante: Debes crear esta interfaz
import com.projecto.ecomarket.dto.InventarioRequestDTO;
import com.projecto.ecomarket.dto.InventarioResponseDTO;
import com.projecto.ecomarket.model.Inventario;
import com.projecto.ecomarket.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Agregamos logs para monitorear las llamadas entre microservicios
public class InventarioService {

    private final InventarioRepository inventariorepository;
    private final ProductoClient productoClient; // <-- Inyectamos el cliente de Catálogo

    // --- MÉTODOS DE LECTURA ---

    public List<InventarioResponseDTO> obtenerTodoStock() { 
        return inventariorepository.findAll().stream()
                .map(this::mapToDTO)
                .toList(); 
    }

    public InventarioResponseDTO obtenerPorProductoId(Long productoId) {
        return inventariorepository.findByProductoId(productoId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para el producto: " + productoId));
    }

    public boolean hayStock(Long productoId, Integer cantidad) {
        Inventario inventario = inventariorepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        return inventario.getStock() >= cantidad; 
    }

    // --- MÉTODOS DE ESCRITURA ---

    @Transactional
    public InventarioResponseDTO crearInventario(InventarioRequestDTO dto) {
        
        // 1. VALIDACIÓN EXTERNA: Consultar al microservicio de Catálogo
        try {
            log.info("Verificando existencia del producto ID: {} en el Catálogo...", dto.getProductoId());
            productoClient.obtenerProductoPorId(dto.getProductoId());
        } catch (Exception e) {
            log.error("Producto {} no encontrado o Catálogo fuera de línea", dto.getProductoId());
            throw new RuntimeException("No se puede crear inventario: El producto no existe en el Catálogo.");
        }

        // 2. VALIDACIÓN INTERNA: Evitar duplicados en la tabla de inventario
        if (inventariorepository.existsByProductoId(dto.getProductoId())) {
            throw new RuntimeException("El producto con ID " + dto.getProductoId() + " ya tiene un registro de inventario.");
        }

        // 3. MAPEO Y GUARDADO
        Inventario inventario = mapToEntity(dto);
        return mapToDTO(inventariorepository.save(inventario));
    }

    @Transactional
    public InventarioResponseDTO disminuirStock(Long productoId, Integer cantidad) {
        Inventario inventario = inventariorepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        if (inventario.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + inventario.getStock());
        }
        
        inventario.setStock(inventario.getStock() - cantidad);
        inventario.setDisponible(inventario.getStock() > 0);
        
        return mapToDTO(inventariorepository.save(inventario));
    }

    @Transactional
    public InventarioResponseDTO aumentarStock(Long productoId, Integer cantidad) {
        Inventario inventario = inventariorepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
            
        inventario.setStock(inventario.getStock() + cantidad);
        inventario.setDisponible(true);
        
        return mapToDTO(inventariorepository.save(inventario));
    }

    @Transactional
    public void eliminar(Long id) { 
        if (!inventariorepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: El inventario con ID " + id + " no existe.");
        }
        inventariorepository.deleteById(id); 
    }

    // --- MAPPERS ---

    private InventarioResponseDTO mapToDTO(Inventario inventario) {
        return InventarioResponseDTO.builder()
                .id(inventario.getId())
                .productoId(inventario.getProductoId())
                .stock(inventario.getStock())
                .disponible(inventario.getDisponible())
                .stockMinimo(inventario.getStockMinimo())
                .build();
    }

    private Inventario mapToEntity(InventarioRequestDTO dto) {
        return Inventario.builder()
                .productoId(dto.getProductoId())
                .stock(dto.getStock())
                .stockMinimo(dto.getStockMinimo())
                .disponible(dto.getStock() > 0)
                .build();
    }
}