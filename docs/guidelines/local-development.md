# Local development

## Requirements

The project requires JDK 21 and Python 3.

## Commands

```shell
./gradlew run                         # chess
./gradlew run --args='tictactoe'     # tic-tac-toe
./gradlew test                        # unit tests
```

The Gradle build prepares the Python environment required by the tic-tac-toe
AI. Generated runtime data is stored under `data/` and is not committed.
