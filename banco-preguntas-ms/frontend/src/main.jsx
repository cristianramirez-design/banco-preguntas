import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import Asignaciones from './pages/Asignaciones.jsx';
import FormularioPregunta from './pages/FormularioPregunta.jsx';
import ListaPreguntas from './pages/ListaPreguntas.jsx';
import Login from './pages/Login.jsx';
import MisRevisiones from './pages/MisRevisiones.jsx';
import Notificaciones from './pages/Notificaciones.jsx';
import { ProveedorSesion, inicioDeRol, useSesion } from './sesion.jsx';
import './estilos.css';

function Protegida({ roles, children }) {
  const { usuario, rol } = useSesion();
  if (!usuario) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(rol)) return <Navigate to={inicioDeRol(rol)} replace />;
  return children;
}

function Inicio() {
  const { usuario, rol } = useSesion();
  return <Navigate to={usuario ? inicioDeRol(rol) : '/login'} replace />;
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<Protegida><Layout /></Protegida>}>
        <Route path="/mis-preguntas" element={<Protegida roles={['AUTOR']}><ListaPreguntas modo="autor" /></Protegida>} />
        <Route path="/preguntas/nueva" element={<Protegida roles={['AUTOR']}><FormularioPregunta /></Protegida>} />
        <Route path="/preguntas/:id/editar" element={<Protegida roles={['AUTOR']}><FormularioPregunta /></Protegida>} />
        <Route path="/mis-revisiones" element={<Protegida roles={['REVISOR']}><MisRevisiones /></Protegida>} />
        <Route path="/asignaciones" element={<Protegida roles={['ADMINISTRADOR']}><Asignaciones /></Protegida>} />
        <Route path="/banco" element={<Protegida roles={['ADMINISTRADOR']}><ListaPreguntas modo="banco" /></Protegida>} />
        <Route path="/notificaciones" element={<Protegida roles={['ADMINISTRADOR']}><Notificaciones todas /></Protegida>} />
        <Route path="/mis-notificaciones" element={<Notificaciones />} />
      </Route>
      <Route path="*" element={<Inicio />} />
    </Routes>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ProveedorSesion>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ProveedorSesion>
  </React.StrictMode>,
);
