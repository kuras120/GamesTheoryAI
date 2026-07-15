# Tic-tac-toe AI domain

The tic-tac-toe AI is an optional opponent for player `O`. The human remains
player `X`. Enabling AI changes only who chooses `O` moves; scoring, turn order,
board validation, and reset behavior remain owned by the tic-tac-toe domain.

The opponent uses Q-learning to choose its moves. The game consumes the chosen
move but does not reproduce or modify the learning algorithm.

## Turn flow

After the human places `X`, the game takes a row-major snapshot of the board and
requests the opponent's move without blocking the interface. The board is
temporarily disabled while the move is calculated. A valid result is applied
as an `O` move.

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

The game rejects the result when:

- the process exits with a non-zero status;
- standard output is not the expected JSON object with integer coordinates;
- a coordinate is outside the board;
- the selected cell does not exist or is already occupied.

On a rejected or failed move, the error is logged and the board is made
interactive again.

## Availability and player feedback

AI preparation begins when the game opens. AI remains unselected while it is
prepared and after it becomes available, so the player always chooses whether
to enable it.

When AI cannot be prepared, its option remains disabled and the player receives
a short explanation with installation or restart guidance. Chess and manual
tic-tac-toe remain available. A fatal opponent error during a game disables AI,
returns control to the player, and adds a friendly failure entry to the game
activity feed.
