package com.cafeteria.sistema.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedidos")
@Getter @Setter // Usamos Getter y Setter en lugar de @Data para evitar errores de listas
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Double total;
    private String metodoPago; 
    private String estado;
    private String observacion; 

    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;
    
    // @JsonManagedReference ayuda a que Java serialice la lista completa sin errores
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference 
    private List<DetallePedido> detalles = new ArrayList<>();
}