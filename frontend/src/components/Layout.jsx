import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

function Layout() {
  const { usuario, sair } = useAuth();
  const navegar = useNavigate();

  function aoSair() {
    sair();
    navegar('/login');
  }

  return (
    <div className="app">
      <header className="cabecalho">
        <Link to="/" className="marca">
          Vivlio
        </Link>

        <nav className="menu">
          <NavLink to="/acervo">Acervo</NavLink>
          {usuario && <NavLink to="/doar">Doar livro</NavLink>}
          {usuario && <NavLink to="/perfil">Meu perfil</NavLink>}
        </nav>

        <div className="area-usuario">
          {usuario ? (
            <>
              <span className="creditos">
                {usuario.saldoCreditos}{' '}
                {usuario.saldoCreditos === 1 ? 'crédito' : 'créditos'}
              </span>
              <span className="nome-usuario">{usuario.nome}</span>
              <button
                type="button"
                className="botao botao-secundario"
                onClick={aoSair}
              >
                Sair
              </button>
            </>
          ) : (
            <Link to="/login" className="botao botao-secundario">
              Entrar
            </Link>
          )}
        </div>
      </header>

      <main className="conteudo">
        <Outlet />
      </main>

      <footer className="rodape">
        Vivlio, plataforma comunitária de troca de livros usados.
      </footer>
    </div>
  );
}

export default Layout;
