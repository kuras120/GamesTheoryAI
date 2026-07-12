# GamesTheoryAI

GamesTheoryAI is a desktop application for playing classic board games and
exploring how artificial intelligence behaves as an opponent.

The project currently provides chess and tic-tac-toe interfaces. Tic-tac-toe
can be played against a Q-learning-based AI, making the application a simple
showcase for connecting game interfaces with independently developed learning
models.

## Quick Start

Make sure JDK 21 and Python 3 are installed, then run:

```shell
./gradlew run                         # chess
./gradlew run --args='tictactoe'     # tic-tac-toe against AI
```

More information is available in the [`docs`](docs) directory:

- [game domains](docs/domain/),
- [development and integration guidelines](docs/guidelines/).
