package com.cafeteria.sistema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "insumos")
@Data
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Leche Entera"
    private Double stockActual; // Ej: 50.0
    private String unidadMedida; // Ej: "Litros", "Kilos", "Unidades"

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;
}