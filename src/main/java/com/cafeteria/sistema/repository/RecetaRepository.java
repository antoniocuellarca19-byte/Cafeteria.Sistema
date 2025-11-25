package com.cafeteria.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.sistema.entity.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByProductoId(Long productoId);
}