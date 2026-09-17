import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

function Perfil() {
  const { usuario } = useAuth();

  return (
    <section>
      <h1>Meu perfil</h1>

      <div className="cartao">
        <p className="linha-perfil">
          <span>Nome</span>
          {usuario.nome}
        </p>
        <p className="linha-perfil">
          <span>E-mail</span>
          {usuario.email}
        </p>
        <p className="linha-perfil">
          <span>Saldo de créditos</span>
          {usuario.saldoCreditos}
        </p>
      </div>

      {usuario.saldoCreditos < 1 ? (
        <p className="dica">
          Sem créditos no momento. <Link to="/doar">Doe um livro</Link> para
          ganhar 1 crédito.
        </p>
      ) : (
        <p className="dica">
          Você pode resgatar {usuario.saldoCreditos}{' '}
          {usuario.saldoCreditos === 1 ? 'livro' : 'livros'} no{' '}
          <Link to="/acervo">acervo</Link>.
        </p>
      )}
    </section>
  );
}

export default Perfil;
