package com.cafeteria.sistema.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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

    // 1. OBTENER TODOS LOS PEDIDOS (Para el cierre de caja después)
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // 2. CREAR UN NUEVO PEDIDO (Esta es la magia)
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        // Asignamos la fecha y hora actual automáticamente
        pedido.setFecha(LocalDateTime.now());
        
        // TRUCO IMPORTANTE:
        // Como los detalles vienen "dentro" del pedido, a veces Java no sabe
        // que pertenecen a ESTE pedido específico. Hacemos este bucle para decírselo.
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido); // "Tú perteneces a este pedido"
            }
        }

        // Al guardar el 'padre' (pedido), se guardan solos los 'hijos' (detalles)
        return pedidoRepository.save(pedido);
    }
    // 3. REPORTE DE CIERRE DE CAJA (Ventas de HOY)
    @GetMapping("/cierre-dia")
    public Map<String, Object> cierreCaja() {
        // Traemos todos los pedidos
        List<Pedido> todos = pedidoRepository.findAll();
        
        Double totalEfectivo = 0.0;
        Double totalQR = 0.0;
        int cantidadPedidos = 0;
        LocalDateTime hoy = LocalDateTime.now();

        for (Pedido p : todos) {
            // Verificamos si el pedido es de HOY (mismo año, mes y día)
            if (p.getFecha().toLocalDate().equals(hoy.toLocalDate())) {
                if ("EFECTIVO".equals(p.getMetodoPago())) {
                    totalEfectivo += p.getTotal();
                } else {
                    totalQR += p.getTotal();
                }
                cantidadPedidos++;
            }
        }

        // Creamos una respuesta ordenada tipo "Mapa" (Diccionario)
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("fecha", hoy.toString());
        reporte.put("pedidos_hoy", cantidadPedidos);
        reporte.put("total_efectivo", totalEfectivo);
        reporte.put("total_qr", totalQR);
        reporte.put("total_general", totalEfectivo + totalQR);

        return reporte;
    }
}