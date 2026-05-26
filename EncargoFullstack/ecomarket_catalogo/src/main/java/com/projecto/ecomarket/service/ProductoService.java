package com.projecto.ecomarket.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.projecto.ecomarket.dto.ProductoRequestDTO;
import com.projecto.ecomarket.dto.ProductoResponseDTO;
import com.projecto.ecomarket.model.Categoria;
import com.projecto.ecomarket.model.Producto;
import com.projecto.ecomarket.repository.CategoriaRepository;
import com.projecto.ecomarket.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        log.info("Consulta de producto id={}", id);
        return productoRepository.findById(id).map(this::mapToDTO);
    }

    public ProductoResponseDTO guardar(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: " + dto.getCategoriaId()));
        Producto producto = new Producto(null, dto.getNombre(), dto.getGtin(), dto.getPrecio(), categoria);
        return mapToDTO(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    private ProductoResponseDTO mapToDTO(Producto l) {
        return new ProductoResponseDTO(l.getId(), l.getNombre(), l.getGtin(),
                l.getPrecio(), l.getCategoria().getNombre());
    }
}

