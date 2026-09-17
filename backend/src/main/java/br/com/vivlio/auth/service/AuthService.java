package br.com.vivlio.auth.service;

import br.com.vivlio.auth.dto.AuthResponse;
import br.com.vivlio.auth.dto.LoginRequest;
import br.com.vivlio.auth.dto.RegistroRequest;
import br.com.vivlio.shared.exception.NegocioException;
import br.com.vivlio.usuario.dto.UsuarioResponse;
import br.com.vivlio.usuario.entity.Usuario;
import br.com.vivlio.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new NegocioException(HttpStatus.CONFLICT, "E-mail já cadastrado. Por favor, faça login.");
        }

        Usuario usuario = usuarioRepository.save(
                new Usuario(request.nome().trim(), email, passwordEncoder.encode(request.senha()))
        );

        return montarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.senha(), u.getSenhaHash()))
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        return montarResposta(usuario);
    }

    private AuthResponse montarResposta(Usuario usuario) {
        return new AuthResponse(jwtService.gerarToken(usuario.getEmail()), UsuarioResponse.de(usuario));
    }
}
