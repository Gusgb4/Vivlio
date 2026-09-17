package br.com.vivlio.livro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public List<LivroResponse> listarDisponiveis(String busca, String genero) {
        return livroRepository.buscar(StatusLivro.DISPONIVEL, texto(busca), texto(genero))
                .stream()
                .map(LivroResponse::de)
                .toList();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
