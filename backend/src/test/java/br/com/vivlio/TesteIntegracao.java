package br.com.vivlio;

import br.com.vivlio.credito.repository.TransacaoCreditoRepository;
import br.com.vivlio.livro.repository.LivroRepository;
import br.com.vivlio.usuario.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class TesteIntegracao {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected LivroRepository livroRepository;

    @Autowired
    protected TransacaoCreditoRepository transacaoCreditoRepository;

    @BeforeEach
    void limparBase() {
        transacaoCreditoRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();
    }
}
