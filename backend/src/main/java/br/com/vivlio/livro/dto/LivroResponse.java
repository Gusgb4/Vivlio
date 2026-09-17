package br.com.vivlio.livro.dto;

import br.com.vivlio.livro.entity.Livro;

public record LivroResponse(Long id, String titulo, String autor, String genero, String status, String doador) {
    public static LivroResponse de(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getGenero(),
                livro.getStatus().name(),
                livro.getDoador().getNome()
        );
    }
}
