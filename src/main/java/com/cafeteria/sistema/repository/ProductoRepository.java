package com.cafeteria.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.sistema.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}