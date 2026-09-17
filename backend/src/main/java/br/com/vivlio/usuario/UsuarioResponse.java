package br.com.vivlio.usuario;

public record UsuarioResponse(Long id, String nome, String email, Integer saldoCreditos) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSaldoCreditos());
    }
}
