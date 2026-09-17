import { Link } from 'react-router-dom';

function NaoEncontrada() {
  return (
    <section>
      <h1>Página não encontrada</h1>
      <Link to="/acervo">Voltar para o acervo</Link>
    </section>
  );
}

export default NaoEncontrada;
