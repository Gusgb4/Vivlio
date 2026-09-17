package br.com.vivlio.resgate.dto;

import br.com.vivlio.livro.dto.LivroResponse;

public record ResgateResponse(LivroResponse livro, Integer saldoCreditos) {
}
