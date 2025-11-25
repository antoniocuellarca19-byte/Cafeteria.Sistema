package com.cafeteria.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.sistema.entity.Insumo;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
}