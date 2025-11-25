package com.cafeteria.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.sistema.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}