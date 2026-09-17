package br.com.vivlio.livro;

import br.com.vivlio.credito.TransacaoCreditoRepository;
import br.com.vivlio.usuario.Usuario;
import br.com.vivlio.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TransacaoCreditoRepository transacaoCreditoRepository;

    private Usuario doador;

    @BeforeEach
    void prepararAcervo() {
        transacaoCreditoRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        doador = usuarioRepository.save(new Usuario("Ana Souza", "ana@vivlio.com", "hash"));

        livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "Romance", doador));
        livroRepository.save(new Livro("Memórias Póstumas", "Machado de Assis", "Romance", doador));
        livroRepository.save(new Livro("O Cortiço", "Aluísio Azevedo", "Naturalismo", doador));

        Livro resgatado = new Livro("Capitães da Areia", "Jorge Amado", "Romance", doador);
        resgatado.setStatus(StatusLivro.RESGATADO);
        livroRepository.save(resgatado);
    }

    @Test
    @DisplayName("TC15 - catalogo e publico e lista apenas os livros disponiveis")
    void deveListarApenasLivrosDisponiveis() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.titulo == 'Capitães da Areia')]").isEmpty())
                .andExpect(jsonPath("$[0].status").value("DISPONIVEL"))
                .andExpect(jsonPath("$[0].doador").value("Ana Souza"));
    }

    @Test
    @DisplayName("Busca por titulo ignorando maiusculas e minusculas")
    void deveBuscarPorTitulo() throws Exception {
        mockMvc.perform(get("/api/livros").param("busca", "dom casmurro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    @Test
    @DisplayName("Busca por autor traz todos os livros do autor")
    void deveBuscarPorAutor() throws Exception {
        mockMvc.perform(get("/api/livros").param("busca", "machado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Filtro por genero")
    void deveFiltrarPorGenero() throws Exception {
        mockMvc.perform(get("/api/livros").param("genero", "naturalismo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("O Cortiço"));
    }

    @Test
    @DisplayName("Busca sem resultado devolve lista vazia")
    void deveDevolverListaVaziaQuandoNaoEncontra() throws Exception {
        mockMvc.perform(get("/api/livros").param("busca", "livro inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
