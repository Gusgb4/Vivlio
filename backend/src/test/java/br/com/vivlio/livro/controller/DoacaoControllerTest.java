package br.com.vivlio.livro.controller;

import br.com.vivlio.TesteIntegracao;
import br.com.vivlio.auth.service.JwtService;
import br.com.vivlio.credito.entity.TipoTransacao;
import br.com.vivlio.credito.entity.TransacaoCredito;
import br.com.vivlio.livro.dto.DoacaoRequest;
import br.com.vivlio.livro.entity.Livro;
import br.com.vivlio.usuario.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DoacaoControllerTest extends TesteIntegracao {

    @Autowired
    private JwtService jwtService;

    private Usuario doador;
    private String token;

    @BeforeEach
    void prepararUsuario() {
        doador = usuarioRepository.save(new Usuario("Ana Souza", "ana@vivlio.com", "hash"));
        token = jwtService.gerarToken(doador.getEmail());
    }

    @Test
    @DisplayName("TC05 - doacao com dados completos salva o livro como DISPONIVEL e credita +1")
    void deveCadastrarDoacaoECreditarUmCredito() throws Exception {
        mockMvc.perform(doacao(new DoacaoRequest("Dom Casmurro", "Machado de Assis", "Romance")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.livro.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.livro.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.livro.doador").value("Ana Souza"))
                .andExpect(jsonPath("$.saldoCreditos").value(1));

        assertThat(usuarioRepository.findById(doador.getId()).orElseThrow().getSaldoCreditos()).isEqualTo(1);
        assertThat(livroRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("TC06 - doacao sem campo obrigatorio retorna 400 e nao gera credito")
    void naoDeveCadastrarDoacaoIncompleta() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"autor\":\"\",\"genero\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").isNotEmpty())
                .andExpect(jsonPath("$.campos.autor").isNotEmpty())
                .andExpect(jsonPath("$.campos.genero").isNotEmpty());

        assertThat(livroRepository.count()).isZero();
        assertThat(usuarioRepository.findById(doador.getId()).orElseThrow().getSaldoCreditos()).isZero();
        assertThat(transacaoCreditoRepository.count()).isZero();
    }

    @Test
    @DisplayName("TC07 - doacao registra transacao de ENTRADA com a descricao oficial")
    void deveRegistrarTransacaoDeEntrada() throws Exception {
        mockMvc.perform(doacao(new DoacaoRequest("O Cortiço", "Aluísio Azevedo", "Naturalismo")))
                .andExpect(status().isCreated());

        List<TransacaoCredito> transacoes = transacaoCreditoRepository.findByUsuarioIdOrderByIdDesc(doador.getId());
        assertThat(transacoes).hasSize(1);
        assertThat(transacoes.get(0).getTipo()).isEqualTo(TipoTransacao.ENTRADA);
        assertThat(transacoes.get(0).getDescricao()).isEqualTo("Doação de livro");
    }

    @Test
    @DisplayName("TC08 - proporcao estrita 1:1 - tres doacoes geram exatamente 3 creditos")
    void deveManterProporcaoUmParaUm() throws Exception {
        mockMvc.perform(doacao(new DoacaoRequest("Livro 1", "Autor 1", "Romance"))).andExpect(status().isCreated());
        mockMvc.perform(doacao(new DoacaoRequest("Livro 2", "Autor 2", "Romance"))).andExpect(status().isCreated());
        mockMvc.perform(doacao(new DoacaoRequest("Livro 3", "Autor 3", "Romance")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldoCreditos").value(3));

        assertThat(usuarioRepository.findById(doador.getId()).orElseThrow().getSaldoCreditos()).isEqualTo(3);
        assertThat(transacaoCreditoRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("RNF02 - doacao sem token retorna 401 e nao salva nada")
    void deveBloquearDoacaoSemToken() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DoacaoRequest("Dom Casmurro", "Machado de Assis", "Romance"))))
                .andExpect(status().isUnauthorized());

        assertThat(livroRepository.count()).isZero();
    }

    @Test
    @DisplayName("Livro doado passa a aparecer no acervo publico")
    void livroDoadoApareceNoAcervo() throws Exception {
        mockMvc.perform(doacao(new DoacaoRequest("Dom Casmurro", "Machado de Assis", "Romance")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    private MockHttpServletRequestBuilder doacao(DoacaoRequest request) throws Exception {
        return post("/api/livros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }
}
