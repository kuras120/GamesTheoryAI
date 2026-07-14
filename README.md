# GamesTheoryAI

GamesTheoryAI is a desktop application for playing classic board games and
exploring how artificial intelligence behaves as an opponent.

The project currently provides chess and tic-tac-toe interfaces. Tic-tac-toe
can be played against a Q-learning-based AI, making the application a simple
showcase for connecting game interfaces with independently developed learning
models.

## What Is Inside

- a chess board frontend for exploring positions and pieces;
- a 4 x 4 tic-tac-toe game for local play;
- a Q-learning opponent integrated with tic-tac-toe.

## Quick Start

Make sure JDK 21 and Python 3 are installed, then run:

```shell
./gradlew run                         # chess
./gradlew run --args='tictactoe'     # tic-tac-toe against AI
./gradlew test                        # automated tests
```

## Documentation

- Repository guide: [`docs/guidelines/repository-guide.md`](docs/guidelines/repository-guide.md)
