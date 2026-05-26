package com.projecto.ecomarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projecto.ecomarket.model.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(String cliente);
}
