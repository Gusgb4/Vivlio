package br.com.vivlio.livro;

import br.com.vivlio.usuario.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @GetMapping
    public List<LivroResponse> listar(@RequestParam(required = false) String busca,
                                      @RequestParam(required = false) String genero) {
        return livroService.listarDisponiveis(busca, genero);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoacaoResponse doar(@AuthenticationPrincipal Usuario usuarioLogado,
                               @Valid @RequestBody DoacaoRequest request) {
        return livroService.doar(usuarioLogado.getId(), request);
    }
}
