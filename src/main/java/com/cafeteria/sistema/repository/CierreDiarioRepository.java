package com.cafeteria.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafeteria.sistema.entity.CierreDiario;

public interface CierreDiarioRepository extends JpaRepository<CierreDiario, Long> {
}