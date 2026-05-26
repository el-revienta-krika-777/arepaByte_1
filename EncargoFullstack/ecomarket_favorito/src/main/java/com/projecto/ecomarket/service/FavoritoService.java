package com.projecto.ecomarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.FavoritoRequestDTO;
import com.projecto.ecomarket.dto.FavoritoResponseDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Favorito;
import com.projecto.ecomarket.repository.FavoritoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final CatalogoClient catalogoClient;
    private final UsuarioClient usuariosClient;

    @Transactional
    public FavoritoResponseDTO agregar(FavoritoRequestDTO dto) {
        usuariosClient.buscarPorNombre(dto.getUsuario())
                .orElseThrow(() -> new RuntimeException(
                        "El usuario '" + dto.getUsuario() + "' no existe en el sistema"));

        ProductoDTO producto = catalogoClient.buscarProducto(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException(
                        "El producto con id " + dto.getProductoId() + " no existe en el catálogo"));

        if (favoritoRepository.existsByProductoIdAndUsuario(dto.getProductoId(), dto.getUsuario())) {
            throw new RuntimeException(
                    "El usuario " + dto.getUsuario() + " ya tiene ese libro en favoritos");
        }

        Favorito favorito = new Favorito();
        favorito.setProductoId(dto.getProductoId());
        favorito.setNombreProducto(producto.getNombre());
        favorito.setUsuario(dto.getUsuario());
        favorito.setFechaAgregado(LocalDateTime.now());

        Favorito guardado = favoritoRepository.save(favorito);
        log.info("Favorito agregado: '{}' para usuario {}", producto.getNombre(), dto.getUsuario());
        return toResponse(guardado);
    }

    public List<FavoritoResponseDTO> listarTodos() {
        return favoritoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FavoritoResponseDTO> listarPorUsuario(String usuario) {
        return favoritoRepository.findByUsuario(usuario).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FavoritoResponseDTO> listarPorProducto(Long productoId) {
        return favoritoRepository.findByProductoId(productoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void eliminar(Long id) {
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorito con id " + id + " no encontrado"));
        favoritoRepository.delete(favorito);
        log.info("Favorito eliminado: id {}", id);
    }

    private FavoritoResponseDTO toResponse(Favorito favorito) {
        FavoritoResponseDTO dto = new FavoritoResponseDTO();
        dto.setId(favorito.getId());
        dto.setProductoId(favorito.getProductoId());
        dto.setNombreProducto(favorito.getNombreProducto());
        dto.setUsuario(favorito.getUsuario());
        dto.setFechaAgregado(favorito.getFechaAgregado());
        return dto;
    }
}

