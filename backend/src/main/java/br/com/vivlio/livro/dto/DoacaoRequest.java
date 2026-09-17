package br.com.vivlio.livro.dto;

import jakarta.validation.constraints.NotBlank;

public record DoacaoRequest(

        @NotBlank(message = "O título é obrigatório.")
        String titulo,

        @NotBlank(message = "O autor é obrigatório.")
        String autor,

        @NotBlank(message = "O gênero é obrigatório.")
        String genero
) {
}
