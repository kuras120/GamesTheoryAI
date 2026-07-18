# Python Runtime Guide

This guide describes how the optional tic-tac-toe AI dependency is selected,
packaged, installed, and executed. The player-facing behavior and move contract
remain defined by the [tic-tac-toe AI domain](../domain/tic-tac-toe-ai.md).

## Requirements And Version Selection

- Build-time dependency preparation requires Python 3.9 or newer with `pip`.
- Runtime bootstrap requires Python 3.9 or newer with `venv` support.
- `GAME_THEORY_VERSION` in `gradle.properties` selects an explicit GitHub
  release of `kuras120/NeuralNetworks`; the current version is `0.0.3`.
- `PYTHON_COMMAND` may override the Python command used by Gradle when the
  default platform command is unsuitable.
- CI uses Python 3.9 so the minimum supported version is exercised during
  dependency preparation.

## Build-Time Wheelhouse

`libs.gradle` downloads the selected `games_theory` wheel under its published
filename. It then uses `pip download` and
`config/python-wheelhouse-constraints.txt` to collect the complete dependency
graph.

The prepared wheelhouse must contain only platform-independent wheels. The
build generates `wheelhouse-manifest.txt` with the exact installation
requirement, every wheel filename, and its SHA-256 hash. Gradle adds this
generated directory to application resources for regular and Shadow JARs.

The build does not create or package a virtual environment. A dependency that
requires a platform-specific wheel needs a separate packaging design before it
can be accepted.

## Runtime Bootstrap

AI setup runs outside the JavaFX application thread and follows this sequence:

1. Discover a supported interpreter. Windows checks `py -3` before `python3`
   and `python`; other systems check `python3` and `python`.
2. Acquire a bootstrap lock in the application runtime directory.
3. Extract the packaged wheelhouse and verify every manifest hash.
4. Create or reuse a private virtual environment.
5. Install the selected requirement with `pip --no-index --find-links`, so the
   target machine does not need network access.
6. Run `games-theory-init` with the application data root.

A hash marker prevents reinstalling an unchanged wheelhouse. When dependencies
change, the environment is updated without deleting learned data.

## Application Directories

Runtime files and learned data live outside the repository and packaged
application:

- Windows: `%LOCALAPPDATA%/GamesTheoryAI`, with the standard user-local fallback;
- macOS: `~/Library/Application Support/GamesTheoryAI`;
- Linux: `$XDG_DATA_HOME/GamesTheoryAI` or `~/.local/share/GamesTheoryAI`.

The `runtime` child contains the lock, extracted wheelhouse, installation
marker, and virtual environment. The `data` child is reserved for persistent
learning data.

## Failure Handling

Missing or unsupported Python and missing `venv` support are availability
states. They disable AI and provide installation and restart guidance without
blocking other games. Bootstrap, installation, initialization, and move
failures retain detailed diagnostics in logs while exposing concise messages to
the player.

External commands use explicit argument lists and bounded timeouts. Standard
output remains reserved for the machine-readable move contract; standard error
is diagnostic and does not fail an otherwise successful command.
