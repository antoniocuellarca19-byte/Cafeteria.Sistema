import { useState, useEffect } from 'react'
import axios from 'axios'

// Configura tu backend
const API_URL = 'http://localhost:8080/api';

function App() {
  const [mesas, setMesas] = useState([]);
  const [productos, setProductos] = useState([]);
  const [productosFiltrados, setProductosFiltrados] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [mesaSeleccionada, setMesaSeleccionada] = useState(null);
  const [carrito, setCarrito] = useState([]);
  const [total, setTotal] = useState(0);
  const [observacion, setObservacion] = useState("");
  const [categoriaActual, setCategoriaActual] = useState("TODOS");

  // --- CARGA INICIAL ---
  useEffect(() => {
    cargarMesas();
    cargarProductos();
  }, [])

  const cargarMesas = async () => {
    try {
      const res = await axios.get(`${API_URL}/mesas`);
      setMesas(res.data);
    } catch (error) {
      console.error("Error cargando mesas:", error);
    }
  }

  const cargarProductos = async () => {
    try {
      const res = await axios.get(`${API_URL}/productos`);
      const listaProductos = res.data;
      setProductos(listaProductos);
      setProductosFiltrados(listaProductos);
      const categoriasUnicas = ["TODOS", ...new Set(listaProductos.map(p => p.categoria))];
      setCategorias(categoriasUnicas);
    } catch (error) {
      console.error("Error cargando productos:", error);
    }
  }

  // --- ACCIONES DE MESA (DESDE LA PANTALLA PRINCIPAL) ---

  const ocuparMesa = async (id) => {
    try {
      await axios.put(`${API_URL}/mesas/${id}/ocupar`);
      cargarMesas(); // Recargar para ver el cambio a ROJO
    } catch (error) {
      console.error("Error al ocupar mesa:", error);
    }
  }

  const liberarMesa = async (id) => {
    if(!window.confirm("¿La mesa ya está vacía y lista para liberarse?")) return;
    try {
      await axios.put(`${API_URL}/mesas/${id}/liberar`);
      cargarMesas(); // Recargar para ver el cambio a VERDE
    } catch (error) {
      console.error("Error al liberar mesa:", error);
    }
  }

  const irAPedido = (mesa) => {
    setMesaSeleccionada(mesa);
    setCarrito([]);
    setTotal(0);
    setObservacion("");
    setCategoriaActual("TODOS");
    setProductosFiltrados(productos);
  }

  // --- LÓGICA DE PEDIDO Y CARRITO ---

  const agregarProducto = (producto) => {
    setCarrito(prev => {
      const existe = prev.find(item => item.producto.id === producto.id);
      let nuevoCarrito;
      if (existe) {
        nuevoCarrito = prev.map(item =>
          item.producto.id === producto.id
            ? { ...item, cantidad: item.cantidad + 1, subtotal: item.subtotal + producto.precio }
            : item
        );
      } else {
        nuevoCarrito = [...prev, { producto: producto, cantidad: 1, subtotal: producto.precio }];
      }
      calcularTotal(nuevoCarrito);
      return nuevoCarrito;
    });
  }

  const quitarProducto = (idProducto) => {
    const nuevoCarrito = carrito.filter(item => item.producto.id !== idProducto);
    setCarrito(nuevoCarrito);
    calcularTotal(nuevoCarrito);
  }

  const calcularTotal = (listaCarrito) => {
    const suma = listaCarrito.reduce((acc, item) => acc + item.subtotal, 0);
    setTotal(suma);
  }

  const confirmarPedido = async (metodoPago) => {
    if (carrito.length === 0) return alert("El carrito está vacío");
    const pedido = {
      total: total,
      metodoPago: metodoPago,
      estado: "PAGADO",
      observacion: observacion,
      mesa: mesaSeleccionada,
      detalles: carrito.map(item => ({
        cantidad: item.cantidad,
        subtotal: item.subtotal,
        producto: item.producto
      }))
    }
    try {
      const res = await axios.post(`${API_URL}/pedidos`, pedido);
      alert(`¡Venta registrada! ID: ${res.data.id}`);
      setMesaSeleccionada(null);
      cargarMesas();
    } catch (error) {
      console.error(error);
      alert("Error al procesar la venta.");
    }
  }

  const filtrarPorCategoria = (cat) => {
    setCategoriaActual(cat);
    setProductosFiltrados(cat === "TODOS" ? productos : productos.filter(p => p.categoria === cat));
  }

  const realizarCierreCaja = async () => {
    if(!window.confirm("¿Realizar cierre de caja?")) return;
    try {
      const res = await axios.post(`${API_URL}/pedidos/cierre-dia`);
      alert(`✅ Cierre Guardado. Total: $${res.data.totalGeneral}`);
    } catch (error) {
      console.error(error);
      alert("Error al cerrar caja");
    }
  }

  // ================= VISTA 1: LISTA DE MESAS (CON CONTROLES) =================
  if (!mesaSeleccionada) {
    return (
      <div className="container mt-5">
        <div className="d-flex justify-content-between align-items-center mb-5">
          <h1 className="fw-bold">☕ Control de Mesas</h1>
          <button className="btn btn-dark" onClick={realizarCierreCaja}>📊 Cierre de Caja</button>
        </div>

        <div className="row g-4">
          {mesas.map((mesa) => {
            const estaLibre = mesa.estado === 'LIBRE';
            return (
              <div className="col-12 col-md-6 col-lg-3" key={mesa.id}>
                <div className={`card text-center h-100 shadow-sm ${estaLibre ? 'border-success' : 'border-danger'}`} style={{borderWidth: '2px'}}>
                  
                  {/* CABECERA: NUMERO Y ESTADO */}
                  <div className={`card-header fw-bold text-white ${estaLibre ? 'bg-success' : 'bg-danger'}`}>
                    Mesa {mesa.numero} - {mesa.estado}
                  </div>

                  <div className="card-body d-flex flex-column gap-2 justify-content-center py-4">
                    
                    {/* BOTÓN 1: CONTROL DE ESTADO (OCUPAR / LIBERAR) */}
                    {estaLibre ? (
                      <button 
                        className="btn btn-outline-danger fw-bold w-100" 
                        onClick={() => ocuparMesa(mesa.id)}
                      >
                        🔴 CERRAR MESA 
                      </button>
                    ) : (
                      <button 
                        className="btn btn-outline-success fw-bold w-100" 
                        onClick={() => liberarMesa(mesa.id)}
                      >
                        🟢 ABRIR MESA 
                      </button>
                    )}

                    <hr className="my-1"/>

                    {/* BOTÓN 2: ENTRAR A LA ORDEN */}
                    <button 
                      className="btn btn-primary fw-bold w-100" 
                      onClick={() => irAPedido(mesa)}
                    >
                      📝 TOMAR PEDIDO / COBRAR
                    </button>

                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    )
  }

  // ================= VISTA 2: DENTRO DE LA ORDEN =================
  return (
    <div className="container-fluid vh-100 d-flex flex-column overflow-hidden">
      <div className="row bg-white shadow-sm py-3 px-4 align-items-center z-1">
        <div className="col-auto">
          <button className="btn btn-outline-secondary" onClick={() => setMesaSeleccionada(null)}>⬅ Volver</button>
        </div>
        <div className="col">
          <h3 className="m-0 fw-bold">Mesa {mesaSeleccionada.numero}</h3>
        </div>
      </div>

      <div className="row flex-grow-1 overflow-hidden">
        {/* MENÚ */}
        <div className="col-md-8 d-flex flex-column h-100 p-4 bg-light">
          <div className="mb-3 d-flex gap-2 overflow-auto">
            {categorias.map(cat => (
              <button key={cat} className={`btn ${categoriaActual===cat ? 'btn-primary':'btn-outline-primary'} rounded-pill`} onClick={() => filtrarPorCategoria(cat)}>{cat}</button>
            ))}
          </div>
          <div className="row row-cols-2 row-cols-lg-4 g-3 overflow-y-auto pb-5">
            {productosFiltrados.map(prod => (
              <div className="col" key={prod.id}>
                <div className="card h-100 shadow-sm" onClick={() => agregarProducto(prod)} style={{cursor:'pointer'}}>
                  <div className="card-body text-center">
                    <h6 className="fw-bold">{prod.nombre}</h6>
                    <span className="text-primary fw-bold">${prod.precio}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* TICKET */}
        <div className="col-md-4 h-100 bg-white border-start d-flex flex-column p-0 shadow-lg">
          <div className="p-3 bg-dark text-white"><h5>🛒 Ticket</h5></div>
          <div className="flex-grow-1 overflow-y-auto p-3">
            {carrito.length===0 && <p className="text-center text-muted">Carrito vacío</p>}
            <ul className="list-group list-group-flush">
              {carrito.map((item, i) => (
                <li key={i} className="list-group-item d-flex justify-content-between">
                  <div><span className="badge bg-primary rounded-pill me-2">{item.cantidad}</span>{item.producto.nombre}</div>
                  <div><span className="fw-bold me-2">${item.subtotal}</span><button className="btn btn-sm btn-danger" onClick={()=>quitarProducto(item.producto.id)}>x</button></div>
                </li>
              ))}
            </ul>
          </div>
          <div className="p-4 bg-light border-top">
            <textarea className="form-control mb-3" rows="2" placeholder="Observaciones..." value={observacion} onChange={e=>setObservacion(e.target.value)}></textarea>
            <div className="d-flex justify-content-between mb-3"><h4>Total:</h4><h2 className="text-success">${total}</h2></div>
            <div className="d-grid gap-2">
              <button className="btn btn-success" onClick={()=>confirmarPedido("EFECTIVO")} disabled={total===0}>💵 EFECTIVO</button>
              <button className="btn btn-primary" onClick={()=>confirmarPedido("QR")} disabled={total===0}>📱 QR</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App