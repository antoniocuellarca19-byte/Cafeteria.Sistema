package com.cafeteria.sistema.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_pedidos")
@Getter @Setter
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad; 
    private Double subtotal; 

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto; // Java traerá el objeto completo (nombre, precio, etc)

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    @JsonBackReference // Detiene el bucle infinito
    private Pedido pedido;
}