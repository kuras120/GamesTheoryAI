# GamesTheoryAI

A JavaFX desktop playground for classic board games and AI integration:
a chess board frontend (without chess move validation) and 4 × 4 tic-tac-toe
with local play or an optional Python Q-learning opponent.

## Quick start

Install JDK 21 and Python 3.9+ with `pip` and `venv`, then run from the repository:

```shell
./gradlew run                      # chess
./gradlew run --args='tictactoe'    # tic-tac-toe
./gradlew test                     # tests
```

The build downloads the backend and its dependencies. Playing against AI uses
system Python and a private environment prepared from the bundled wheels.

## Useful links

- [Architecture, build commands, and runtime](docs/architecture.md)
- [Chess](docs/domain/chess.md) · [Tic-tac-toe](docs/domain/tic-tac-toe.md) · [AI contract](docs/domain/tic-tac-toe-ai.md)
- [Research](docs/research/)
- [Ideas and unfinished work](INBOX.md)
- [Agent instructions](AGENTS.md)
