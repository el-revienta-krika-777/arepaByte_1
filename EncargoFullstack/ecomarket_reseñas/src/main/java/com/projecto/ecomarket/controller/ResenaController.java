package com.projecto.ecomarket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecto.ecomarket.dto.ResenaRequestDTO; // <--- Importamos el RequestDTO
import com.projecto.ecomarket.dto.ResenaResponseDTO;
import com.projecto.ecomarket.service.ResenaService;

import jakarta.validation.Valid; // Para que las anotaciones @Min, @Max funcionen

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<List<ResenaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(resenaService.obtenerTodas());
    }

    // Ajustado para recibir ResenaRequestDTO
    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO resenaDto) {
        return new ResponseEntity<>(resenaService.crearResena(resenaDto), HttpStatus.CREATED);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPromedio(productoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }
}
