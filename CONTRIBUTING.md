# Guia de Contribuição — Vivlio

Este documento define as regras de versionamento, commits e qualidade de código que todo integrante da equipe (e qualquer pessoa que entrar depois) deve seguir.

## Estratégia de Branches

- **main** — código estável, pronto para produção/entrega. Protegida: nenhum push direto é permitido.
- **develop** — branch de integração, onde o código de todas as funcionalidades é reunido antes de ir para a main.
- **feature/nome-da-funcionalidade** — criada a partir da develop para desenvolver uma nova funcionalidade.
- **fix/descricao-do-bug** — criada a partir da develop para corrigir um bug.

Fluxo: `feature/*` ou `fix/*` → Pull Request → `develop` → (após testes) → Pull Request → `main`.

## Padrão de Commits

Commits devem seguir o formato `tipo: descrição breve`, usando os seguintes tipos:

- `feat:` — nova funcionalidade
- `fix:` — correção de bug
- `refactor:` — alteração de código que não muda o comportamento externo (manutenção preventiva)
- `docs:` — alterações em documentação

Exemplo: `feat: adiciona validação de crédito no resgate de livros`

## Regras de Pull Request

- Todo PR precisa de pelo menos **1 aprovação** de outro desenvolvedor antes do merge.
- Nenhum PR será aprovado se a verificação do linter (ESLint/Prettier) acusar erros críticos.
- Conversas/comentários de revisão devem ser resolvidos antes do merge.

## Ferramentas de Verificação

- **ESLint** e **Prettier** — padronização de código no frontend.
