# Repository guide

This guide contains operational repository information for maintainers and
agents. The root `README.md` is intentionally short and visitor-oriented; use
this file for setup, module maps, workflows, testing, and release details.

## Requirements and commands

Local development currently requires JDK 21 and Python 3.

```shell
./gradlew run                         # chess
./gradlew run --args='tictactoe'     # tic-tac-toe
./gradlew test                        # unit tests
./gradlew clean build                 # full build and verification
./gradlew shadowJar                   # runnable application JAR
```

## Module map

- `src/main/java/com/games/theory/chess` — chess JavaFX controller and model.
- `src/main/java/com/games/theory/tictactoe` — tic-tac-toe UI, rules, storage,
  and Python integration.
- `src/main/java/com/games/theory/utils` — shared resource, logging, and process
  helpers.
- `src/main/resources` — FXML views, bundled board data, and build-provided
  resources.
- `src/test` — unit tests for tic-tac-toe rules and integration parsing.
- `build.gradle` and `libs.gradle` — Java build, dependency download, packaging,
  tests, and coverage.

## Documentation map

- [Chess domain](../domain/chess.md) — current chess behavior and source map.
- [Tic-tac-toe domain](../domain/tic-tac-toe.md) — board, turns, scoring, and
  source map.
- [Tic-tac-toe AI domain](../domain/tic-tac-toe-ai.md) — opponent behavior,
  process contract, validation, and current runtime setup.
- [Engineering guide](engineering-guide.md) — project-wide design, testing,
  and review rules.
- [Project lifecycle](project-lifecycle.md) — staged approval workflow for
  larger changes.
- [`docs/projects`](../projects) — temporary, reviewable implementation
  proposals.
- [`docs/research`](../research) — technical options and supporting
  investigations.

## Testing

JUnit tests live under `src/test`. `./gradlew test` runs the test suite and
produces JaCoCo coverage; `./gradlew clean build` is the local equivalent of the
main CI build step.

Tests that invoke external processes should isolate command execution behind a
fakeable boundary. JavaFX behavior that cannot be automated must be listed as a
manual verification item in the project review.

## Python dependency and release resources

The current build downloads the latest `games_theory` wheel from the GitHub
releases of `kuras120/NeuralNetworks`. The current runtime setup is described in
the [tic-tac-toe AI domain](../domain/tic-tac-toe-ai.md).

Planned delivery changes are documented under `docs/projects` and must not be
treated as implemented behavior until their proposal and implementation have
both been accepted.
