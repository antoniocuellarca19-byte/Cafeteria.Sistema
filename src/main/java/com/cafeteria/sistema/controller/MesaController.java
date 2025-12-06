package com.cafeteria.sistema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    // 1. Listar todas las mesas
    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    // 2. NUEVO: Endpoint para LIBERAR mesa (Cerrar)
    @PutMapping("/{id}/liberar")
    public Mesa liberarMesa(@PathVariable Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        mesa.setEstado("LIBRE");
        return mesaRepository.save(mesa);
    }

    // 3. NUEVO: Endpoint para OCUPAR mesa (Abrir)
    @PutMapping("/{id}/ocupar")
    public Mesa ocuparMesa(@PathVariable Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        mesa.setEstado("OCUPADA");
        return mesaRepository.save(mesa);
    }
}