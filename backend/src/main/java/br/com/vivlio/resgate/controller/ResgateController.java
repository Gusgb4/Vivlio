package br.com.vivlio.resgate.controller;

import br.com.vivlio.resgate.dto.ResgateRequest;
import br.com.vivlio.resgate.dto.ResgateResponse;
import br.com.vivlio.resgate.service.ResgateService;
import br.com.vivlio.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resgates")
@RequiredArgsConstructor
public class ResgateController {

    private final ResgateService resgateService;

    @PostMapping
    public ResgateResponse resgatar(@AuthenticationPrincipal Usuario usuarioLogado,
                                    @Valid @RequestBody ResgateRequest request) {
        return resgateService.resgatar(usuarioLogado.getId(), request.livroId());
    }
}
