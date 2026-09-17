import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { mensagemDeErro } from '../services/erros';

function Cadastro() {
  const { registrar } = useAuth();
  const navegar = useNavigate();

  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function aoEnviar(evento) {
    evento.preventDefault();
    setErro('');
    setEnviando(true);

    try {
      await registrar(nome, email, senha);
      navegar('/acervo', { replace: true });
    } catch (falha) {
      setErro(mensagemDeErro(falha));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="tela-auth">
      <h1>Criar conta</h1>

      <form className="formulario" onSubmit={aoEnviar}>
        <label className="campo">
          Nome
          <input
            type="text"
            value={nome}
            onChange={(evento) => setNome(evento.target.value)}
            required
          />
        </label>

        <label className="campo">
          E-mail
          <input
            type="email"
            value={email}
            onChange={(evento) => setEmail(evento.target.value)}
            required
          />
        </label>

        <label className="campo">
          Senha
          <input
            type="password"
            value={senha}
            onChange={(evento) => setSenha(evento.target.value)}
            minLength={6}
            required
          />
          <small>Mínimo de 6 caracteres.</small>
        </label>

        {erro && <p className="erro">{erro}</p>}

        <button type="submit" className="botao" disabled={enviando}>
          {enviando ? 'Criando...' : 'Criar conta'}
        </button>
      </form>

      <p className="rodape-form">
        Já tem conta? <Link to="/login">Entrar</Link>
      </p>
    </section>
  );
}

export default Cadastro;
