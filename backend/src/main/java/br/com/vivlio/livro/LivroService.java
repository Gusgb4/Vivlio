package br.com.vivlio.livro;

import br.com.vivlio.credito.CreditoService;
import br.com.vivlio.shared.NegocioException;
import br.com.vivlio.usuario.Usuario;
import br.com.vivlio.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private static final String DESCRICAO_DOACAO = "Doação de livro";

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final CreditoService creditoService;

    @Transactional(readOnly = true)
    public List<LivroResponse> listarDisponiveis(String busca, String genero) {
        return livroRepository.buscar(StatusLivro.DISPONIVEL, texto(busca), texto(genero))
                .stream()
                .map(LivroResponse::de)
                .toList();
    }

    @Transactional
    public DoacaoResponse doar(Long usuarioId, DoacaoRequest request) {
        Usuario doador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado."));

        Livro livro = livroRepository.save(new Livro(
                request.titulo().trim(),
                request.autor().trim(),
                request.genero().trim(),
                doador
        ));

        creditoService.creditar(doador, DESCRICAO_DOACAO);

        return new DoacaoResponse(LivroResponse.de(livro), doador.getSaldoCreditos());
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
