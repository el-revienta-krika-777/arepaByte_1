package com.projecto.ecomarket.controller;

import com.projecto.ecomarket.dto.CarritoResponseDTO;
import com.projecto.ecomarket.dto.ItemCarritoRequestDTO;
import com.projecto.ecomarket.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // <-- Crear servicios web y APIs REST.
@RequestMapping("/api/carritos") // <-- Conectar (o mapear) las peticiones web. 
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @PostMapping
    public ResponseEntity<CarritoResponseDTO> crear(@RequestParam String usuario) {
        return ResponseEntity.status(201).body(carritoService.crear(usuario)); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        return carritoService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CarritoResponseDTO>> obtenerPorUsuario(@RequestParam String usuario) {
        return ResponseEntity.ok(carritoService.obtenerPorUsuario(usuario));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(@PathVariable Long id,
                                                        @Valid @RequestBody ItemCarritoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.agregarItem(id, dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> quitarItem(@PathVariable Long id,
                                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.quitarItem(id, itemId));
    }

    @DeleteMapping("/{id}/vaciar")
    public ResponseEntity<CarritoResponseDTO> vaciar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.vaciar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (carritoService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
