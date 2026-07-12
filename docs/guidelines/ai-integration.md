# AI integration

## Python environment

The Gradle build downloads the latest `games_theory` wheel from the GitHub
releases of [`kuras120/NeuralNetworks`](https://github.com/kuras120/NeuralNetworks).
It creates a virtual environment under `build/resources/main`, installs the
wheel, and invokes `games-theory-init` when the tic-tac-toe UI starts.

## Tic-tac-toe process contract

For every AI turn, the Java application starts the `games-theory` command. The
current scores and board flattened row by row are passed as command-line
arguments; `N` represents an empty cell.

The command must write exactly one JSON object to standard output:

```json
{"x": 2, "y": 3}
```

Both coordinates are zero-based. `x` identifies the column and `y` identifies
the row. Diagnostic messages belong on standard error because standard output
is reserved for the machine-readable move.

The Java adapter validates the process exit code, JSON format, coordinate
range, and whether the selected cell is empty. Process execution happens away
from the JavaFX application thread; board updates are scheduled back on that
thread.
