# Tic-tac-toe domain

The tic-tac-toe domain owns the 4 x 4 board, player turns, marks, reset
behavior, and score calculation. Players receive points for completed rows,
columns, and diagonals.

When AI mode is enabled, the human plays `X` and the external Q-learning player
plays `O`. The board is flattened row by row before being passed to the AI; `N`
represents an empty cell. The returned coordinate uses `x` as the zero-based
column and `y` as the zero-based row.

The opponent behavior and move contract are documented in the
[tic-tac-toe AI domain](tic-tac-toe-ai.md).

## Game activity

The game presents a chronological activity feed containing player and AI
moves, awarded points, and the current score after a scoring event. Board
coordinates shown to players use one-based row and column numbers.

Reset starts a new game, clears the previous activity and score, and preserves
AI availability and previously learned opponent data.
