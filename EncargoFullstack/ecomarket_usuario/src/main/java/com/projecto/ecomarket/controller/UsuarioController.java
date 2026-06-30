package com.projecto.ecomarket.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecto.ecomarket.dto.UsuarioRequestDTO;
import com.projecto.ecomarket.dto.UsuarioResponseDTO;
import com.projecto.ecomarket.service.UsuarioService;

import java.util.List;


@Tag(name = "Usuarios", description = "Registro de usuarios del sistema. Endpoint GET /nombre/{nombre} es consumido por carrito, pedidos y favoritos para validar que el usuario existe.")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioService.obtenerTodos();
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Buscar usuario por nombre",
        description = "Endpoint que consumen carrito (FeignClient), pedidos (FeignClient) y favoritos (WebClient) para validar que el usuario existe antes de registrar una operacion. Devuelve 404 si el nombre no existe."
    )
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorNombre(
            @Parameter(description = "Nombre exacto del usuario", example = "ana") @PathVariable String nombre) {
        return usuarioService.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.crear(dto);
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}

