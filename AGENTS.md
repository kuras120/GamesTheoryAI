# Repository map

Use the documentation under `docs` as the source of repository context and
implementation guidance.

| Area | Source |
| --- | --- |
| Chess domain | [`docs/domain/chess.md`](docs/domain/chess.md) |
| Tic-tac-toe domain | [`docs/domain/tic-tac-toe.md`](docs/domain/tic-tac-toe.md) |
| Local setup and commands | [`docs/guidelines/local-development.md`](docs/guidelines/local-development.md) |
| Repository structure | [`docs/guidelines/repository-structure.md`](docs/guidelines/repository-structure.md) |
| Python/Q-learning integration contract | [`docs/guidelines/ai-integration.md`](docs/guidelines/ai-integration.md) |
| Chess implementation | `src/main/java/com/games/theory/chess` and `src/main/resources/Chess.fxml` |
| Tic-tac-toe implementation | `src/main/java/com/games/theory/tictactoe` and `src/main/resources/TicTacToe.fxml` |
| Python/Q-learning adapter | `src/main/java/com/games/theory/tictactoe/integration` |
| Shared infrastructure | `src/main/java/com/games/theory/utils` |
| Dependency download and packaging | `build.gradle`, `libs.gradle`, and `gradle.properties` |
| Expected behavior | `src/test` |

When behavior, structure, or integration contracts change, update the relevant
document under `docs` together with the implementation.
