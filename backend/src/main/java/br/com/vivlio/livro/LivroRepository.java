package br.com.vivlio.livro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query("""
            select l from Livro l
            where l.status = :status
              and (lower(l.titulo) like lower(concat('%', :busca, '%'))
                   or lower(l.autor) like lower(concat('%', :busca, '%')))
              and lower(l.genero) like lower(concat('%', :genero, '%'))
            order by l.id desc
            """)
    List<Livro> buscar(@Param("status") StatusLivro status,
                       @Param("busca") String busca,
                       @Param("genero") String genero);
}
