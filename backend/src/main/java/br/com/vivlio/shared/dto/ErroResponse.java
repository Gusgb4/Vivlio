package br.com.vivlio.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(String mensagem, Map<String, String> campos) {
    public ErroResponse(String mensagem) {
        this(mensagem, null);
    }
}
