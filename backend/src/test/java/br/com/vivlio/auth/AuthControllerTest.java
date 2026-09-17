package br.com.vivlio.auth;

import br.com.vivlio.auth.dto.LoginRequest;
import br.com.vivlio.auth.dto.RegistroRequest;
import br.com.vivlio.usuario.Usuario;
import br.com.vivlio.usuario.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBase() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("TC01 - cadastro com dados validos cria a conta com saldo zerado")
    void deveCadastrarUsuarioComSaldoZero() throws Exception {
        mockMvc.perform(registro(new RegistroRequest("Leonardo Ferreira", "Leonardo@Vivlio.com", "senha123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.email").value("leonardo@vivlio.com"))
                .andExpect(jsonPath("$.usuario.saldoCreditos").value(0));

        assertThat(usuarioRepository.findByEmail("leonardo@vivlio.com")).isPresent();
    }

    @Test
    @DisplayName("TC02 - cadastro com e-mail duplicado retorna 409 e nao duplica a conta")
    void naoDeveCadastrarEmailDuplicado() throws Exception {
        cadastrar(new RegistroRequest("Ana Souza", "ana@vivlio.com", "senha123"));

        mockMvc.perform(registro(new RegistroRequest("Outra Ana", "ana@vivlio.com", "senha456")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("E-mail já cadastrado. Por favor, faça login."));

        assertThat(usuarioRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("TC03 - cadastro sem campos obrigatorios retorna 400 com os campos sinalizados")
    void naoDeveCadastrarSemCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"\",\"senha\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").isNotEmpty())
                .andExpect(jsonPath("$.campos.email").isNotEmpty())
                .andExpect(jsonPath("$.campos.senha").isNotEmpty());

        assertThat(usuarioRepository.count()).isZero();
    }

    @Test
    @DisplayName("TC04 - senha e persistida com hash BCrypt (RNF03)")
    void deveSalvarSenhaComHashBCrypt() throws Exception {
        cadastrar(new RegistroRequest("Daniela QA", "daniela@vivlio.com", "senha123"));

        Usuario salvo = usuarioRepository.findByEmail("daniela@vivlio.com").orElseThrow();
        assertThat(salvo.getSenhaHash()).isNotEqualTo("senha123");
        assertThat(salvo.getSenhaHash()).startsWith("$2");
    }

    @Test
    @DisplayName("Login com credenciais validas devolve token JWT")
    void deveAutenticarComCredenciaisValidas() throws Exception {
        cadastrar(new RegistroRequest("Marcelo Dev", "marcelo@vivlio.com", "senha123"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("marcelo@vivlio.com", "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.nome").value("Marcelo Dev"));
    }

    @Test
    @DisplayName("Login com senha incorreta retorna 401")
    void naoDeveAutenticarComSenhaIncorreta() throws Exception {
        cadastrar(new RegistroRequest("Joao Dev", "joao@vivlio.com", "senha123"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("joao@vivlio.com", "senhaErrada"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    @Test
    @DisplayName("Login de e-mail inexistente retorna 401")
    void naoDeveAutenticarEmailInexistente() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("ninguem@vivlio.com", "senha123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RNF02 - rota protegida sem token retorna 401")
    void deveBloquearRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RNF02 - rota protegida com token invalido retorna 401")
    void deveBloquearRotaProtegidaComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer token.invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Rota protegida com token valido devolve o perfil do usuario logado")
    void deveRetornarPerfilComTokenValido() throws Exception {
        String token = cadastrar(new RegistroRequest("Gustavo DevOps", "gustavo@vivlio.com", "senha123"));

        mockMvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("gustavo@vivlio.com"))
                .andExpect(jsonPath("$.saldoCreditos").value(0));
    }

    private String cadastrar(RegistroRequest request) throws Exception {
        String corpo = mockMvc.perform(registro(request))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpo).get("token").asText();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder registro(RegistroRequest request)
            throws Exception {
        return post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON).content(json(request));
    }

    private String json(Object valor) throws Exception {
        return objectMapper.writeValueAsString(valor);
    }
}
