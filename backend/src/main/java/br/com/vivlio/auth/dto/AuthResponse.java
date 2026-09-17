package br.com.vivlio.auth.dto;

import br.com.vivlio.usuario.UsuarioResponse;

public record AuthResponse(String token, UsuarioResponse usuario) {
}
