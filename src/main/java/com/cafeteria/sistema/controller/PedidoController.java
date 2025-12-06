package com.cafeteria.sistema.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional; // Importante
import org.springframework.web.bind.annotation.*;

import com.cafeteria.sistema.entity.*;
import com.cafeteria.sistema.repository.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final RecetaRepository recetaRepository;
    private final InsumoRepository insumoRepository;
    private final CierreDiarioRepository cierreRepository;

    public PedidoController(PedidoRepository pedidoRepository, 
                            RecetaRepository recetaRepository, 
                            InsumoRepository insumoRepository,
                            CierreDiarioRepository cierreRepository) {
        this.pedidoRepository = pedidoRepository;
        this.recetaRepository = recetaRepository;
        this.insumoRepository = insumoRepository;
        this.cierreRepository = cierreRepository;
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @PostMapping
    @Transactional // Asegura que todo ocurra en una sola transacción
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        pedido.setFecha(LocalDateTime.now());
        
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido); // Vincula al padre

                // Lógica de Stock (Insumos)
                if(detalle.getProducto() != null && detalle.getProducto().getId() != null) {
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
        }
        
        // 1. Guardamos el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // 2. TRUCO: Refrescamos desde la base de datos para asegurar que 
        // devuelva todos los detalles con sus nombres y IDs completos.
        return pedidoRepository.findById(pedidoGuardado.getId()).orElse(pedidoGuardado);
    }

    // Endpoint de Cierre de Caja (Incluido para mantener la funcionalidad anterior)
    @PostMapping("/cierre-dia")
    public CierreDiario realizarCierreCaja() {
        List<Pedido> todos = pedidoRepository.findAll();
        
        Double totalEfectivo = 0.0;
        Double totalQR = 0.0;
        int cantidadPedidos = 0;
        LocalDateTime hoy = LocalDateTime.now();

        for (Pedido p : todos) {
            if (p.getFecha() != null && p.getFecha().toLocalDate().equals(hoy.toLocalDate())) {
                String metodo = p.getMetodoPago() != null ? p.getMetodoPago() : "EFECTIVO";
                if ("EFECTIVO".equalsIgnoreCase(metodo)) {
                    totalEfectivo += p.getTotal();
                } else {
                    totalQR += p.getTotal();
                }
                cantidadPedidos++;
            }
        }

        CierreDiario cierre = new CierreDiario();
        cierre.setFecha(hoy);
        cierre.setCantidadPedidos(cantidadPedidos);
        cierre.setTotalEfectivo(totalEfectivo);
        cierre.setTotalQr(totalQR);
        cierre.setTotalGeneral(totalEfectivo + totalQR);

        return cierreRepository.save(cierre);
    }
}