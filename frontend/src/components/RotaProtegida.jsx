import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

function RotaProtegida() {
  const { usuario, carregando } = useAuth();
  const local = useLocation();

  if (carregando) {
    return <p className="aviso">Carregando...</p>;
  }

  if (!usuario) {
    return <Navigate to="/login" replace state={{ de: local.pathname }} />;
  }

  return <Outlet />;
}

export default RotaProtegida;
