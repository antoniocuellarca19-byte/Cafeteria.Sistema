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
import com.cafeteria.sistema.entity.Insumo;
import com.cafeteria.sistema.entity.Pedido;
import com.cafeteria.sistema.entity.Receta;
import com.cafeteria.sistema.repository.InsumoRepository;
import com.cafeteria.sistema.repository.PedidoRepository;
import com.cafeteria.sistema.repository.RecetaRepository;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final RecetaRepository recetaRepository;
    private final InsumoRepository insumoRepository;

    public PedidoController(PedidoRepository pedidoRepository, 
                            RecetaRepository recetaRepository, 
                            InsumoRepository insumoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.recetaRepository = recetaRepository;
        this.insumoRepository = insumoRepository;
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        pedido.setFecha(LocalDateTime.now());
        
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);

                Long idProducto = detalle.getProducto().getId();
                
                List<Receta> recetas = recetaRepository.findByProductoId(idProducto);

                for (Receta receta : recetas) {
                    Insumo insumo = receta.getInsumo();
                    
                    Double cantidadAGastar = receta.getCantidadRequerida() * detalle.getCantidad();
                    
                    Double nuevoStock = insumo.getStockActual() - cantidadAGastar;
                    
                    insumo.setStockActual(nuevoStock);
                    
                    insumoRepository.save(insumo);
                }
            }
        }
        return pedidoRepository.save(pedido);
    }

    @GetMapping("/cierre-dia")
    public Map<String, Object> cierreCaja() {
        List<Pedido> todos = pedidoRepository.findAll();
        
        Double totalEfectivo = 0.0;
        Double totalQR = 0.0;
        int cantidadPedidos = 0;
        LocalDateTime hoy = LocalDateTime.now();

        for (Pedido p : todos) {
            if (p.getFecha() == null) continue;

            if (p.getFecha().toLocalDate().equals(hoy.toLocalDate())) {
                String metodo = p.getMetodoPago() != null ? p.getMetodoPago() : "EFECTIVO";

                if ("EFECTIVO".equals(metodo)) {
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