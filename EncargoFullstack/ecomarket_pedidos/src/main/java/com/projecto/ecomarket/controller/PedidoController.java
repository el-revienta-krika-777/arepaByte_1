package com.projecto.ecomarket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecto.ecomarket.dto.PedidoRequestDTO;
import com.projecto.ecomarket.dto.PedidoResponseDTO;
import com.projecto.ecomarket.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(201).body(pedidoService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{cliente}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorCliente(@PathVariable String cliente) {
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (pedidoService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
