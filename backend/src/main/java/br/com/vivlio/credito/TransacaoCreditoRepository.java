package br.com.vivlio.credito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoCreditoRepository extends JpaRepository<TransacaoCredito, Long> {

    List<TransacaoCredito> findByUsuarioIdOrderByIdDesc(Long usuarioId);
}
