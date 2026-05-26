package com.projecto.ecomarket.controller;

import com.projecto.ecomarket.dto.FavoritoRequestDTO;
import com.projecto.ecomarket.dto.FavoritoResponseDTO;
import com.projecto.ecomarket.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favoritos", description = "Lista de productos favoritos por usuario.")
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @Operation(
        summary = "Agregar favorito",
        description = " "
    )
    @PostMapping
    public ResponseEntity<FavoritoResponseDTO> agregar(@Valid @RequestBody FavoritoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritoService.agregar(dto));
    }

    @Operation(summary = "Listar todos los favoritos")
    @GetMapping
    public ResponseEntity<List<FavoritoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(favoritoService.listarTodos());
    }

    @Operation(summary = "Listar favoritos de un usuario")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<FavoritoResponseDTO>> listarPorUsuario(
            @Parameter(example = "maria") @PathVariable String usuario) {
        return ResponseEntity.ok(favoritoService.listarPorUsuario(usuario));
    }

    @Operation(summary = "Ver que usuarios tienen un producto en favoritos")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<FavoritoResponseDTO>> listarPorProducto(
            @Parameter(example = "1") @PathVariable Long productoId) {
        return ResponseEntity.ok(favoritoService.listarPorProducto(productoId));
    }

    @Operation(summary = "Eliminar favorito")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
