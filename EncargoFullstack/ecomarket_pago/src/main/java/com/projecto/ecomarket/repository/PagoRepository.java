package com.projecto.ecomarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projecto.ecomarket.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByPedidoId(Long pedidoId);
}