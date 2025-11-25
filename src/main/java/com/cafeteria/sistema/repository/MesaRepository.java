package com.cafeteria.sistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteria.sistema.entity.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
}