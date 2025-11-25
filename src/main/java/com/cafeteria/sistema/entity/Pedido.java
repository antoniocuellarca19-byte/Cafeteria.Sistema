package com.cafeteria.sistema.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha; // Fecha y hora exacta de la venta

    private Double total; // Cuánto dinero fue en total

    private String metodoPago; // "EFECTIVO" o "QR"

    private String estado; // "PENDIENTE" o "PAGADO"

    @ManyToOne // Muchas ventas pueden ser de una misma mesa
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;
    
    // Esta parte es clave: Un pedido tiene una lista de detalles
    // cascade = CascadeType.ALL significa: Si guardo el Pedido, guarda sus detalles automáticamente.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;
}