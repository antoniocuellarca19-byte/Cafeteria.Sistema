package com.cafeteria.sistema.entity; // <--- Importante: El paquete

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 
import lombok.Data;

@Entity 
@Table(name = "mesas") 
@Data
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;
    
    private String estado;
}