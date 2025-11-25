package com.cafeteria.sistema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeteria.sistema.entity.Mesa;
import com.cafeteria.sistema.repository.MesaRepository;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173")
public class MesaController {

    private final MesaRepository mesaRepository;

    public MesaController(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }
}