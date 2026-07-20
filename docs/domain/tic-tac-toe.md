# Tic-tac-toe domain

The tic-tac-toe domain owns the 4 x 4 board, player turns, marks, reset
behavior, and score calculation. Players receive points for completed rows,
columns, and diagonals.

When AI mode is enabled, the human plays `X` and the external Q-learning player
plays `O`. Enabling the opponent does not change the board, turn, scoring, or
reset rules.

The opponent behavior and move contract are documented in the
[tic-tac-toe AI domain](tic-tac-toe-ai.md).

## Scoring And Winning Lines

The game evaluates three-cell rows, columns, and diagonals. Each newly scored
sequence awards one point and is checked only once. Four equal marks in one
direction contain two overlapping three-cell sequences and therefore award two
points.

Each awarded sequence is shown as one continuous line between the centers of
its first and last cells. Winning lines remain above marks and cell hover
states, do not consume pointer input, stay aligned when the board is resized,
and remain visible until reset.

## Game activity

The game presents a chronological activity feed containing player and AI
moves, awarded points, and the current score after a scoring event. Board
coordinates shown to players use one-based row and column numbers.

Reset starts a new game, clears the previous activity and score, and preserves
AI availability and previously learned opponent data.
