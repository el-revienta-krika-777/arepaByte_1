package com.projecto.ecomarket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecto.ecomarket.dto.CuponRequestDTO;
import com.projecto.ecomarket.dto.CuponResponseDTO;
import com.projecto.ecomarket.service.CuponService;

import java.util.List;

@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor 
public class CuponController {

    private final CuponService cuponService;

    // 1. Ver todos los cupones existentes y su estado de uso
    @GetMapping
    public ResponseEntity<List<CuponResponseDTO>> listarTodos() {
        return ResponseEntity.ok(cuponService.obtenerTodos());
    }

    // 2. Ver el detalle de un solo cupón por su código
    @GetMapping("/{codigo}")
    public ResponseEntity<CuponResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(cuponService.obtenerPorCodigo(codigo));
    }

    // 3. Crear el cupón (Admin)
    @PostMapping
    public ResponseEntity<CuponResponseDTO> crear(@Valid @RequestBody CuponRequestDTO request) {
        return new ResponseEntity<>(cuponService.crearCupon(request), HttpStatus.CREATED);
    }

    // 4. Validar el cupón (Usado por el micro de Carrito)
    @PostMapping("/validar")
    public ResponseEntity<CuponResponseDTO> validar(@Valid @RequestBody CuponRequestDTO request) {
        return ResponseEntity.ok(cuponService.procesarValidacion(request));
    }

    // 5. Aplicar / Consumir el cupón (Confirmación de compra)
    @PatchMapping("/{codigo}/aplicar") 
    public ResponseEntity<Void> aplicar(@PathVariable String codigo) {
        cuponService.aplicarCupon(codigo);
        return ResponseEntity.noContent().build();
    }
}