package br.com.vivlio.usuario;

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
