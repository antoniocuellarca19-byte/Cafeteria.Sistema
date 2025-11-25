package com.cafeteria.sistema.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeteria.sistema.entity.DetallePedido;
import com.cafeteria.sistema.entity.Pedido;
import com.cafeteria.sistema.repository.PedidoRepository;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // 1. OBTENER TODOS LOS PEDIDOS
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // 2. CREAR UN NUEVO PEDIDO
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        pedido.setFecha(LocalDateTime.now());
        
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);
            }
        }
        return pedidoRepository.save(pedido);
    }

    // 3. REPORTE DE CIERRE DE CAJA
    @GetMapping("/cierre-dia")
    public Map<String, Object> cierreCaja() {
        List<Pedido> todos = pedidoRepository.findAll();
        
        Double totalEfectivo = 0.0;
        Double totalQR = 0.0;
        int cantidadPedidos = 0;
        LocalDateTime hoy = LocalDateTime.now();

        for (Pedido p : todos) {
            // Verificamos si es de HOY
            if (p.getFecha().toLocalDate().equals(hoy.toLocalDate())) {
                if ("EFECTIVO".equals(p.getMetodoPago())) {
                    totalEfectivo += p.getTotal();
                } else {
                    totalQR += p.getTotal();
                }
                cantidadPedidos++;
            }
        }

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("fecha", hoy.toString());
        reporte.put("pedidos_hoy", cantidadPedidos);
        reporte.put("total_efectivo", totalEfectivo);
        reporte.put("total_qr", totalQR);
        reporte.put("total_general", totalEfectivo + totalQR);

        return reporte;
    }
}