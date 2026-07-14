# Tic-tac-toe domain

The tic-tac-toe domain owns the 4 x 4 board, player turns, marks, reset
behavior, and score calculation. Row, column, and diagonal processors inspect
the JavaFX nodes and award points for completed lines.

When AI mode is enabled, the human plays `X` and the external Q-learning player
plays `O`. The board is flattened row by row before being passed to the AI; `N`
represents an empty cell. The returned coordinate uses `x` as the zero-based
column and `y` as the zero-based row.

The opponent behavior, process contract, and runtime setup are documented in
the [tic-tac-toe AI domain](tic-tac-toe-ai.md).

## Source map

- `src/main/java/com/games/theory/tictactoe` — controller and domain model.
- `src/main/java/com/games/theory/tictactoe/processor` — scoring rules.
- `src/main/java/com/games/theory/tictactoe/integration` — AI adapter.
- `src/main/resources/TicTacToe.fxml` — JavaFX view.
