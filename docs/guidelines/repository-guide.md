# Repository guide

This guide contains operational repository information for maintainers and
agents. The root `README.md` is intentionally short and visitor-oriented; use
this file for setup, module maps, workflows, testing, and release details.

## Requirements and commands

Local development requires JDK 21 and Python 3.9 or newer. Python is used to
prepare packaged AI dependencies during the build and to create the local AI
runtime when the application starts.

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
- `src/main/java/com/games/theory/utils` — shared resource, application-path,
  and process helpers.
- `src/main/resources` — FXML views, bundled board data, and build-provided
  resources.
- `src/test` — unit tests for tic-tac-toe rules and integration parsing.
- `build.gradle` and `libs.gradle` — Java build, dependency download, packaging,
  tests, and coverage.

## Documentation map

- [Chess domain](../domain/chess.md) — current chess behavior.
- [Tic-tac-toe domain](../domain/tic-tac-toe.md) — board, turns, scoring, and
  player-facing activity.
- [Tic-tac-toe AI domain](../domain/tic-tac-toe-ai.md) — opponent behavior,
  move contract, validation, and availability behavior.
- [Python runtime guide](python-runtime.md) — dependency packaging, local
  environment bootstrap, paths, and failure handling.
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

`GAME_THEORY_VERSION` in `gradle.properties` selects the exact
`games_theory` release. The build packages that wheel and its pinned universal
dependencies; it does not package a virtual environment. See the
[Python runtime guide](python-runtime.md) for the complete build and runtime
flow.
