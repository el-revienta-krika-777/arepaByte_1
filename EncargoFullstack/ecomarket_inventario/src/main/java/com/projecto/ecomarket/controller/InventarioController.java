package com.projecto.ecomarket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecto.ecomarket.dto.InventarioRequestDTO;
import com.projecto.ecomarket.dto.InventarioResponseDTO;
import com.projecto.ecomarket.service.InventarioService;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // 1. Obtener todos
    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodoStock());
    }

    // 2. CORREGIDO: El nombre en @PathVariable debe coincidir con {productoId}
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponseDTO> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    // 3. Crear nuevo inventario
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crear(@RequestBody InventarioRequestDTO dto) { 
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.crearInventario(dto));
    }

    // 4. CORREGIDO: Se cambió 'id' por 'productoId' para que coincida con la URL
    @PatchMapping("/aumentar/{productoId}")
    public ResponseEntity<InventarioResponseDTO> aumentar(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.aumentarStock(productoId, cantidad));
    }

    // 5. CORREGIDO: Se cambió 'id' por 'productoId'
    @PatchMapping("/disminuir/{productoId}")
    public ResponseEntity<InventarioResponseDTO> disminuir(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.disminuirStock(productoId, cantidad));
    }

    // 6. Eliminar (Este estaba bien, pero asegúrate de usarlo como /api/inventario/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}