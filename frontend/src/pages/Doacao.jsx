import { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import { mensagemDeErro } from '../services/erros';
import { useAuth } from '../hooks/useAuth';
import { GENEROS } from '../constants/generos';

function Doacao() {
  const { atualizarSaldo } = useAuth();

  const [titulo, setTitulo] = useState('');
  const [autor, setAutor] = useState('');
  const [genero, setGenero] = useState('');
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [enviando, setEnviando] = useState(false);

  async function aoEnviar(evento) {
    evento.preventDefault();
    setErro('');
    setSucesso('');
    setEnviando(true);

    try {
      const { data } = await api.post('/api/livros', { titulo, autor, genero });
      atualizarSaldo(data.saldoCreditos);
      setSucesso(
        `"${data.livro.titulo}" entrou no acervo. Seu saldo agora é de ${data.saldoCreditos}.`
      );
      setTitulo('');
      setAutor('');
      setGenero('');
    } catch (falha) {
      setErro(mensagemDeErro(falha));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="tela-form">
      <h1>Doar livro</h1>
      <p className="subtitulo">
        Cada livro doado entra no acervo da comunidade e te dá 1 crédito.
      </p>

      <form className="formulario" onSubmit={aoEnviar}>
        <label className="campo">
          Título
          <input
            type="text"
            value={titulo}
            onChange={(evento) => setTitulo(evento.target.value)}
            required
          />
        </label>

        <label className="campo">
          Autor
          <input
            type="text"
            value={autor}
            onChange={(evento) => setAutor(evento.target.value)}
            required
          />
        </label>

        <label className="campo">
          Gênero
          <select
            value={genero}
            onChange={(evento) => setGenero(evento.target.value)}
            required
          >
            <option value="">Selecione</option>
            {GENEROS.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
        </label>

        {erro && <p className="erro">{erro}</p>}
        {sucesso && (
          <p className="sucesso">
            {sucesso} <Link to="/acervo">Ver no acervo</Link>
          </p>
        )}

        <button type="submit" className="botao" disabled={enviando}>
          {enviando ? 'Cadastrando...' : 'Cadastrar doação'}
        </button>
      </form>
    </section>
  );
}

export default Doacao;
