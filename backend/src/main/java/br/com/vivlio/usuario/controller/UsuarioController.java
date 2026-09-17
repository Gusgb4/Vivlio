package br.com.vivlio.usuario.controller;

import br.com.vivlio.usuario.dto.UsuarioResponse;
import br.com.vivlio.usuario.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @GetMapping("/me")
    public UsuarioResponse perfil(@AuthenticationPrincipal Usuario usuarioLogado) {
        return UsuarioResponse.de(usuarioLogado);
    }
}
