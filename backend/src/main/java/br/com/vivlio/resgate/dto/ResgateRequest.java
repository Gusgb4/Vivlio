package br.com.vivlio.resgate.dto;

import jakarta.validation.constraints.NotNull;

public record ResgateRequest(

        @NotNull(message = "Informe o livro que deseja resgatar.")
        Long livroId
) {
}
