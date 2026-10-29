# Diário de Despesas Essenciais e Tetos por Categoria

Aplicativo Android (Kotlin + Jetpack Compose) para controle de gastos do dia a dia, com teto mensal individual por categoria.
Disciplina: Programação Para Dispositivos Móveis — IFTM Campus Patrocínio — Prova 05.

- **Protótipo no Figma:** <COLE_AQUI_O_LINK_PÚBLICO>
- **Vídeo demonstrativo:** <COLE_AQUI_O_LINK_DO_YOUTUBE_NÃO_LISTADO>

## Funcionalidades
1. Cadastro e login com e-mail/senha (Firebase Authentication).
2. Teto orçamentário mensal por categoria (Supermercado/Feira, Combustível/Transporte, Farmácia/Saúde, Outros).
3. Lançamento rápido de compras: valor, categoria obrigatória, descrição/estabelecimento e forma de pagamento (Pix, Dinheiro, Cartão de Crédito/Débito).
4. Painel em tempo real com barra de progresso por categoria: verde (< 75%), amarelo (75–99%) e vermelho (≥ 100%).
5. Resumo global: total gasto no mês e saldo residual (soma dos tetos − total gasto).
6. Validação numérica (regex: só dígitos e até 2 casas decimais; rejeita vazio, zero, negativo e texto) e persistência local com Room.

## Arquitetura (MVVM)
```
View (Compose)  ──eventos──▶  ViewModel  ──▶  Model (Room DAO / Firebase Auth)
     ▲                            │
     └────── StateFlow ◀──────────┘
```
| Camada | Arquivos | Responsabilidade |
|---|---|---|
| Model | `data/Models.kt`, `data/AppDatabase.kt` | Entidades `Expense` e `Budget`, DAO e banco Room |
| ViewModel | `viewmodel/AuthViewModel.kt`, `viewmodel/MainViewModel.kt` | Estado (`StateFlow`), regras de cálculo e acesso ao DAO |
| View | `ui/*.kt`, `MainActivity.kt` | Telas Compose sem regra de negócio; observam o estado via `collectAsStateWithLifecycle` |
| Util | `util/Utils.kt` | Validação/formatação monetária e intervalo do mês |

Fluxo reativo: o DAO expõe `Flow`; o `MainViewModel` combina despesas do mês e tetos (`combine`) em um `StateFlow<DashboardState>`; qualquer inclusão/exclusão atualiza o painel automaticamente. Os dados são separados por `userId` (uid do Firebase).

## Como executar
1. Clone o repositório e abra no Android Studio (JDK 17).
2. Crie um projeto no [Firebase Console](https://console.firebase.google.com), registre o app com o pacote `br.edu.iftm.diariodespesas`, ative **Authentication → E-mail/senha** e baixe o `google-services.json` para `app/`.
3. Execute em um emulador/dispositivo com Android 7.0+ (API 24).
