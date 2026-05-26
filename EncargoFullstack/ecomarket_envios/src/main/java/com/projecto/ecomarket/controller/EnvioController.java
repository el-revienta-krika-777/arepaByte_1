package com.projecto.ecomarket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projecto.ecomarket.dto.EnvioResponseDTO;
import com.projecto.ecomarket.model.Envio;
import com.projecto.ecomarket.service.EnvioService;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(envioService.obtenerTodosLosEnvios());
}

    @PostMapping("/crear")
    public ResponseEntity<EnvioResponseDTO> generarEnvio(@RequestBody Envio envio) {
        return new ResponseEntity<>(envioService.crearEnvio(envio), HttpStatus.CREATED);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponseDTO> rastrear(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.consultarPorPedido(pedidoId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> cambiarEstado(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String seguimiento) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado, seguimiento));
    }
}
