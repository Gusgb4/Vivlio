import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import RotaProtegida from './components/RotaProtegida';
import Acervo from './pages/Acervo';
import Cadastro from './pages/Cadastro';
import Doacao from './pages/Doacao';
import Login from './pages/Login';
import NaoEncontrada from './pages/NaoEncontrada';
import Perfil from './pages/Perfil';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />

      <Route element={<Layout />}>
        <Route path="/" element={<Acervo />} />
        <Route path="/acervo" element={<Acervo />} />

        <Route element={<RotaProtegida />}>
          <Route path="/doar" element={<Doacao />} />
          <Route path="/perfil" element={<Perfil />} />
        </Route>

        <Route path="*" element={<NaoEncontrada />} />
      </Route>
    </Routes>
  );
}

export default App;
