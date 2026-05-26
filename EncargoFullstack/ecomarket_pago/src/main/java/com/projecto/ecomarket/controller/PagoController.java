package com.projecto.ecomarket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecto.ecomarket.dto.PagoResponseDTO;
import com.projecto.ecomarket.model.Pago;
import com.projecto.ecomarket.service.PagoService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<PagoResponseDTO> pagar(@RequestBody Pago pago) {
        return new ResponseEntity<>(pagoService.procesarPago(pago), HttpStatus.CREATED);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> verPago(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(pedidoId));
    }
}