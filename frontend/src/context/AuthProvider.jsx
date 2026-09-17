import { useCallback, useEffect, useMemo, useState } from 'react';
import { AuthContext } from './AuthContext';
import api from '../services/api';

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [carregando, setCarregando] = useState(() =>
    Boolean(localStorage.getItem('vivlio_token'))
  );

  useEffect(() => {
    const token = localStorage.getItem('vivlio_token');

    if (!token) {
      return;
    }

    api
      .get('/api/usuarios/me')
      .then((resposta) => setUsuario(resposta.data))
      .catch(() => localStorage.removeItem('vivlio_token'))
      .finally(() => setCarregando(false));
  }, []);

  const entrar = useCallback(async (email, senha) => {
    const { data } = await api.post('/api/auth/login', { email, senha });
    localStorage.setItem('vivlio_token', data.token);
    setUsuario(data.usuario);
  }, []);

  const registrar = useCallback(async (nome, email, senha) => {
    const { data } = await api.post('/api/auth/registrar', {
      nome,
      email,
      senha,
    });
    localStorage.setItem('vivlio_token', data.token);
    setUsuario(data.usuario);
  }, []);

  const sair = useCallback(() => {
    localStorage.removeItem('vivlio_token');
    setUsuario(null);
  }, []);

  const atualizarSaldo = useCallback((saldoCreditos) => {
    setUsuario((atual) => (atual ? { ...atual, saldoCreditos } : atual));
  }, []);

  const valor = useMemo(
    () => ({ usuario, carregando, entrar, registrar, sair, atualizarSaldo }),
    [usuario, carregando, entrar, registrar, sair, atualizarSaldo]
  );

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}
