package br.com.vivlio.credito;

import br.com.vivlio.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditoService {

    private final TransacaoCreditoRepository transacaoCreditoRepository;

    @Transactional
    public void creditar(Usuario usuario, String descricao) {
        usuario.setSaldoCreditos(usuario.getSaldoCreditos() + 1);
        transacaoCreditoRepository.save(new TransacaoCredito(usuario, TipoTransacao.ENTRADA, descricao));
    }
}
