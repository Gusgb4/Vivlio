package br.com.vivlio.resgate.service;

import br.com.vivlio.TesteIntegracao;
import br.com.vivlio.livro.entity.Livro;
import br.com.vivlio.livro.entity.StatusLivro;
import br.com.vivlio.resgate.dto.ResgateResponse;
import br.com.vivlio.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ResgateConcorrenteTest extends TesteIntegracao {

    @Autowired
    private ResgateService resgateService;

    @Test
    @DisplayName("TC14 - RNF01: dois resgates simultaneos com saldo 1 nao geram saldo negativo")
    void naoDevePermitirSaldoNegativoEmResgatesSimultaneos() throws Exception {
        Usuario doador = usuarioRepository.save(new Usuario("Ana Souza", "ana@vivlio.com", "hash"));

        Usuario leitor = new Usuario("João Dev", "joao@vivlio.com", "hash");
        leitor.setSaldoCreditos(1);
        Usuario salvo = usuarioRepository.save(leitor);

        Livro primeiro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "Romance", doador));
        Livro segundo = livroRepository.save(new Livro("O Cortiço", "Aluísio Azevedo", "Naturalismo", doador));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Callable<ResgateResponse>> tarefas = List.of(
                () -> resgateService.resgatar(salvo.getId(), primeiro.getId()),
                () -> resgateService.resgatar(salvo.getId(), segundo.getId())
        );

        List<Future<ResgateResponse>> resultados = executor.invokeAll(tarefas);
        executor.shutdown();

        long sucessos = resultados.stream().filter(this::concluiuComSucesso).count();

        assertThat(sucessos).isEqualTo(1);
        assertThat(usuarioRepository.findById(salvo.getId()).orElseThrow().getSaldoCreditos()).isZero();
        assertThat(livroRepository.findAll().stream()
                .filter(l -> l.getStatus() == StatusLivro.RESGATADO)
                .count()).isEqualTo(1);
        assertThat(transacaoCreditoRepository.count()).isEqualTo(1);
    }

    private boolean concluiuComSucesso(Future<ResgateResponse> resultado) {
        try {
            resultado.get();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
