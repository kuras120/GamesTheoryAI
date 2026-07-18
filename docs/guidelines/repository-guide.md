# Repository Guide

This guide contains the operational information needed to build, run, test,
package, and release GamesTheoryAI.

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

## Testing

JUnit tests live under `src/test`. `./gradlew test` runs the test suite and
produces JaCoCo coverage; `./gradlew clean build` is the local equivalent of the
main CI build step.

## Packaging

`GAME_THEORY_VERSION` in `gradle.properties` selects the exact
`games_theory` release. The build packages that wheel and its pinned universal
dependencies; it does not package a virtual environment. See the
[Python runtime guide](python-runtime.md) for the complete build and runtime
flow.
