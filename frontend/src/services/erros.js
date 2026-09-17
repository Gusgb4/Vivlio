const PADRAO = 'Não foi possível concluir a operação. Tente novamente.';

export function mensagemDeErro(erro, padrao = PADRAO) {
  if (!erro.response) {
    return 'Não foi possível falar com o servidor. Verifique se o backend está no ar.';
  }

  const dados = erro.response.data;

  if (dados?.campos) {
    const primeiro = Object.values(dados.campos)[0];
    if (primeiro) {
      return primeiro;
    }
  }

  return dados?.mensagem || padrao;
}
