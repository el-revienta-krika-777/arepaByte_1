package com.projecto.ecomarket.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecto.ecomarket.dto.CuponRequestDTO;
import com.projecto.ecomarket.dto.CuponResponseDTO;
import com.projecto.ecomarket.model.Cupon;
import com.projecto.ecomarket.repository.CuponRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuponService {

    private final CuponRepository cuponRepository;

    public List<CuponResponseDTO> obtenerTodos() {
        return cuponRepository.findAll().stream()
                .map(this::mapToDTOConEstadisticas)
                .toList();
    }

    public CuponResponseDTO obtenerPorCodigo(String codigo) {
        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        return mapToDTOConEstadisticas(cupon);
    }

    public CuponResponseDTO procesarValidacion(CuponRequestDTO request) {
        try {
            Cupon cupon = obtenerYValidarCupon(request.getCodigo());
            Double descuento = calcularDescuento(request.getCodigo(), request.getMontoCarrito());

            return CuponResponseDTO.builder()
                    .codigo(cupon.getCodigo())
                    .descripcion("Cupón válido: " + cupon.getTipo() + " de " + cupon.getValor() + (cupon.getTipo().equals("PORCENTAJE") ? "%" : ""))
                    .valido(true)
                    .montoDescuento(descuento)
                    .build();

        } catch (RuntimeException e) {
            log.warn("Fallo al validar cupón {}: {}", request.getCodigo(), e.getMessage());
            return CuponResponseDTO.builder()
                    .codigo(request.getCodigo())
                    .descripcion(e.getMessage())
                    .valido(false)
                    .montoDescuento(0.0)
                    .build();
        }
    }

    private Cupon obtenerYValidarCupon(String codigo) {
        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Cupón no válido o no existe"));

        if (!cupon.getActivo()) throw new RuntimeException("El cupón está inactivo");

        if (cupon.getFechaExpiracion() != null && cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El cupón ha expirado");
        }

        if (cupon.getUsoMaximo() != null && cupon.getUsosActuales() >= cupon.getUsoMaximo()) {
            throw new RuntimeException("El cupón ha alcanzado su límite de usos");
        }

        return cupon;
    }

    public Double calcularDescuento(String codigo, Double totalPedido) {
        Cupon cupon = obtenerYValidarCupon(codigo);
        double descuento = "PORCENTAJE".equalsIgnoreCase(cupon.getTipo()) 
                           ? totalPedido * (cupon.getValor() / 100) 
                        : cupon.getValor();

        return Math.min(descuento, totalPedido);
    }

    @Transactional
    public CuponResponseDTO crearCupon(CuponRequestDTO dto) {
        if (cuponRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código de cupón '" + dto.getCodigo() + "' ya existe.");
        }
        Cupon cupon = mapToEntity(dto);
        cupon.setUsosActuales(0);
        cupon.setActivo(true);
        
        return mapToDTO(cuponRepository.save(cupon));
    }

    @Transactional
    public void aplicarCupon(String codigo) {
        Cupon cupon = obtenerYValidarCupon(codigo);
        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        cuponRepository.save(cupon);
        log.info("Cupón {} aplicado. Usos actuales: {}", codigo, cupon.getUsosActuales());
    }

    private Cupon mapToEntity(CuponRequestDTO dto) {
        return Cupon.builder()
                .codigo(dto.getCodigo())
                .tipo(dto.getTipo())
                .valor(dto.getValor())
                .usoMaximo(dto.getUsoMaximo())
                .fechaExpiracion(dto.getFechaExpiracion())
                .build();
    }

    private CuponResponseDTO mapToDTO(Cupon cupon) {
        return CuponResponseDTO.builder()
                .codigo(cupon.getCodigo())
                .descripcion("Cupón: " + cupon.getTipo() + " - Valor: " + cupon.getValor())
                .valido(cupon.getActivo())
                .montoDescuento(0.0)
                .build();
    }

    private CuponResponseDTO mapToDTOConEstadisticas(Cupon cupon) {
        return CuponResponseDTO.builder()
                .codigo(cupon.getCodigo())
                .valido(cupon.getActivo())
                .descripcion(String.format("Uso: %d/%d | Expira: %s", 
                    cupon.getUsosActuales(), 
                    cupon.getUsoMaximo(),
                    cupon.getFechaExpiracion() != null ? cupon.getFechaExpiracion().toLocalDate() : "Nunca"))
                .montoDescuento(cupon.getValor()) 
                .build();
    }
}