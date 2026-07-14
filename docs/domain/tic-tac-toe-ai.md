# Tic-tac-toe AI domain

The tic-tac-toe AI is an optional opponent for player `O`. The human remains
player `X`. Enabling AI changes only who chooses `O` moves; scoring, turn order,
board validation, and reset behavior remain owned by the tic-tac-toe domain.

The opponent is implemented by the external Python package `games_theory`,
which contains the Q-learning behavior. Java owns the JavaFX interaction and
process adapter; it does not reproduce or modify the learning algorithm.

## Turn flow

After the human places `X`, Java takes a row-major snapshot of the board and
starts the Python command outside the JavaFX application thread. The board is
temporarily disabled while the move is calculated. A valid result is applied
on the JavaFX thread as an `O` move.

A reset invalidates an outstanding request so a result calculated for an older
board is not applied to the new game.

## Process input

The command receives these positional arguments:

1. player `X` score;
2. player `O` score;
3. every board cell flattened row by row.

Marks are passed as `X` and `O`; an empty cell is passed as `N`.

For a 4 x 4 board, the command therefore receives two score arguments followed
by sixteen cell arguments.

## Process output

Standard output is reserved for exactly one JSON move:

```json
{"x": 2, "y": 3}
```

Coordinates are zero-based. `x` is the column and `y` is the row. Standard
error is reserved for diagnostic logs and does not invalidate a successful
command by itself.

Java rejects the result when:

- the process exits with a non-zero status;
- standard output is not the expected JSON object with integer coordinates;
- a coordinate is outside the board;
- the selected cell does not exist or is already occupied.

On a rejected or failed move, the error is logged and the board is made
interactive again.

## Current runtime setup

The Gradle build downloads the latest `games_theory` wheel from the GitHub
releases of `kuras120/NeuralNetworks` and creates a build-local Python virtual
environment. When the tic-tac-toe view initializes, Java installs the wheel in
that environment and invokes `games-theory-init`.

This setup is platform-specific and is being reconsidered in the active project
under `docs/projects`. Project documents describe proposals, not current
runtime guarantees.

## Source map

- `src/main/java/com/games/theory/tictactoe/integration` — process orchestration,
  output parsing, and move validation.
- `src/main/java/com/games/theory/tictactoe/exception/AiException.java` — adapter
  failure type.
- `src/main/java/com/games/theory/utils/DataReaderUtils.java` — current command
  and resource lookup.
- `libs.gradle` — current wheel download and build-local environment tasks.
- `src/test/java/com/games/theory/tictactoe/integration` — process-output tests.
