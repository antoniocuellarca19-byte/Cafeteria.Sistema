import { useState, useEffect } from 'react'
import axios from 'axios'

function App() {
  const [mesas, setMesas] = useState([])
  const [productos, setProductos] = useState([])
  const [mesaSeleccionada, setMesaSeleccionada] = useState(null)
  const [carrito, setCarrito] = useState([])
  const [total, setTotal] = useState(0)

  // Cargar mesas al inicio
  useEffect(() => {
    cargarMesas();
  }, [])

  const cargarMesas = async () => {
    const res = await axios.get('http://localhost:8080/api/mesas');
    setMesas(res.data);
  }

  const cargarProductos = async () => {
    const res = await axios.get('http://localhost:8080/api/productos');
    setProductos(res.data);
  }

  // Al hacer clic en "Abrir Mesa"
  const abrirMesa = async (mesa) => {
    await cargarProductos(); // Traemos el menú
    setMesaSeleccionada(mesa);
    setCarrito([]); // Limpiamos el carrito
    setTotal(0);
  }

  // Función para consultar el cierre (AHORA ESTÁ FUERA, EN SU LUGAR CORRECTO)
  const verCierreCaja = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/pedidos/cierre-dia');
      const data = res.data;
      
      alert(`
        📅 REPORTE DE HOY
        ---------------------------
        📝 Pedidos atendidos: ${data.pedidos_hoy}
        
        💵 Efectivo en Caja: $${data.total_efectivo}
        📱 Cobrado por QR:   $${data.total_qr}
        
        💰 VENTA TOTAL:      $${data.total_general}
      `);
    } catch (error) {
      console.error(error);
      alert("Error al calcular el cierre");
    }
  }

  // Agregar producto al carrito
  const agregarProducto = (producto) => {
    setCarrito([...carrito, producto]);
    setTotal(total + producto.precio);
  }

  // Guardar el pedido en Java
  const confirmarPedido = async (metodoPago) => {
    const pedido = {
      total: total,
      metodoPago: metodoPago,
      estado: "PAGADO",
      mesa: mesaSeleccionada,
      detalles: carrito.map(prod => ({
        cantidad: 1,
        subtotal: prod.precio,
        producto: prod
      }))
    }

    try {
      await axios.post('http://localhost:8080/api/pedidos', pedido);
      alert("¡Venta registrada con éxito! 💰");
      setMesaSeleccionada(null); // Volver a las mesas
      cargarMesas(); // Actualizar estados
    } catch (error) {
      alert("Error al guardar venta");
      console.error(error);
    }
  }

  // --- VISTA 1: LISTA DE MESAS ---
  if (!mesaSeleccionada) {
    return (
      <div className="container mt-5">
        {/* HEADER CON BOTÓN DE CIERRE */}
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h1>☕ Control de Mesas</h1>
          <button className="btn btn-dark" onClick={verCierreCaja}>
            📊 Cierre de Caja
          </button>
        </div>

        <div className="row">
          {mesas.map((mesa) => (
            <div className="col-md-3 mb-4" key={mesa.id}>
              <div className={`card text-center text-white ${mesa.estado === 'LIBRE' ? 'bg-success' : 'bg-danger'}`}>
                <div className="card-body">
                  <h3>Mesa {mesa.numero}</h3>
                  <p>{mesa.estado}</p>
                  <button className="btn btn-light" onClick={() => abrirMesa(mesa)}>
                    {mesa.estado === 'LIBRE' ? 'Abrir Mesa' : 'Ver Pedido'}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  // --- VISTA 2: TOMAR ORDEN (MENÚ) ---
  return (
    <div className="container mt-4">
      <button className="btn btn-secondary mb-3" onClick={() => setMesaSeleccionada(null)}>
        ⬅ Volver
      </button>
      
      <h2 className="mb-3">Orden Mesa {mesaSeleccionada.numero}</h2>
      
      <div className="row">
        {/* COLUMNA IZQUIERDA: PRODUCTOS */}
        <div className="col-md-8">
          <div className="row">
            {productos.map((prod) => (
              <div className="col-md-4 mb-3" key={prod.id}>
                <div className="card h-100" onClick={() => agregarProducto(prod)} style={{cursor: 'pointer'}}>
                  {/* Si tienes imágenes reales, cambia el src. Por ahora usamos un placeholder */}
                  <div className="card-body text-center">
                    <h6>{prod.nombre}</h6>
                    <p className="text-primary fw-bold">${prod.precio}</p>
                    <span className="badge bg-secondary">{prod.categoria}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* COLUMNA DERECHA: TICKET/CUENTA */}
        <div className="col-md-4">
          <div className="card">
            <div className="card-header bg-dark text-white">
              Ticket Actual
            </div>
            <ul className="list-group list-group-flush">
              {carrito.map((item, index) => (
                <li key={index} className="list-group-item d-flex justify-content-between">
                  <span>{item.nombre}</span>
                  <span>${item.precio}</span>
                </li>
              ))}
            </ul>
            <div className="card-footer">
              <h4>Total: ${total}</h4>
              <div className="d-grid gap-2 mt-3">
                <button className="btn btn-primary" onClick={() => confirmarPedido("EFECTIVO")}>
                  💵 Pagar Efectivo
                </button>
                <button className="btn btn-info text-white" onClick={() => confirmarPedido("QR")}>
                  📱 Pagar QR
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App