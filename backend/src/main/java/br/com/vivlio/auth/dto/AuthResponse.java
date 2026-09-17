package br.com.vivlio.auth.dto;

import br.com.vivlio.usuario.dto.UsuarioResponse;

public record AuthResponse(String token, UsuarioResponse usuario) {
}
