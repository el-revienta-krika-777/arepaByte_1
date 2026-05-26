package com.projecto.ecomarket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.client.ProductoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.ResenaRequestDTO;
import com.projecto.ecomarket.dto.ResenaResponseDTO;
import com.projecto.ecomarket.model.Resena;
import com.projecto.ecomarket.repository.ResenaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Agregado para poder ver errores en consola si falla la comunicación
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    @Transactional
    public ResenaResponseDTO crearResena(ResenaRequestDTO dto) {
        
        // 1. VALIDACIÓN: Verificamos que existan en los otros microservicios
        try {
            productoClient.obtenerProducto(dto.getProductoId());
            usuarioClient.obtenerUsuario(dto.getUsuarioId());
        } catch (Exception e) {
            throw new RuntimeException("No se puede crear la reseña: El Producto o Usuario no existen en sus respectivos servicios.");
        }

        // 2. Mapeo manual y guardado
        Resena resena = Resena.builder()
                .productoId(dto.getProductoId())
                .usuarioId(dto.getUsuarioId())
                .calificacion(dto.getCalificacion())
                .comentario(dto.getComentario())
                .fechaCreacion(LocalDateTime.now())
                .build();

        return mapToDTO(resenaRepository.save(resena));
    }

    /**
     * Este método ahora "enriquece" el DTO llamando al microservicio de Usuarios
     */
    private ResenaResponseDTO mapToDTO(Resena resena) {
        String nombreEncontrado = "Usuario no disponible";
        
        try {
            // Llamada al microservicio de usuarios para obtener el nombre
            var usuario = usuarioClient.obtenerUsuario(resena.getUsuarioId());
            if (usuario != null && usuario.getNombre() != null) {
                nombreEncontrado = usuario.getNombre();
            }
        } catch (Exception e) {
            // Si el microservicio de usuarios falla, registramos el error pero no detenemos la app
            log.error("Error al obtener nombre del usuario {} : {}", resena.getUsuarioId(), e.getMessage());
        }

        return ResenaResponseDTO.builder()
                .id(resena.getId())
                .productoId(resena.getProductoId())
                .usuarioId(resena.getUsuarioId())
                .nombreUsuario(nombreEncontrado) // <--- Aquí asignamos el nombre obtenido
                .calificacion(resena.getCalificacion())
                .comentario(resena.getComentario())
                .fecha(resena.getFechaCreacion() != null ? resena.getFechaCreacion().toString() : LocalDateTime.now().toString())
                .build();
    }

    public List<ResenaResponseDTO> obtenerTodas() {
        return resenaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ResenaResponseDTO> obtenerPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ResenaResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Double obtenerPromedio(Long productoId) {
        Double promedio = resenaRepository.getPromedioCalificacion(productoId);
        return (promedio != null) ? promedio : 0.0;
    }

    @Transactional
    public void eliminarResena(Long id) {
        if (!resenaRepository.existsById(id)) {
            throw new RuntimeException("La reseña no existe");
        }
        resenaRepository.deleteById(id);
    }
}