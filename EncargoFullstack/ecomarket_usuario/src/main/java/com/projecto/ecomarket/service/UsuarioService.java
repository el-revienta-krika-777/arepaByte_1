package com.projecto.ecomarket.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.dto.UsuarioRequestDTO;
import com.projecto.ecomarket.dto.UsuarioResponseDTO;
import com.projecto.ecomarket.model.Usuario;
import com.projecto.ecomarket.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toResponse);
    }

    public Optional<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre).map(this::toResponse);
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe un usuario con el nombre: " + dto.getNombre());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setActivo(true);
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado: {} (id={})", guardado.getNombre(), guardado.getId());
        return toResponse(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con id " + id + " no encontrado"));
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado: id {}", id);
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setActivo(u.getActivo());
        return dto;
    }
}
