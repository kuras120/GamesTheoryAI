# Chess domain

The chess domain renders a board from bundled state and piece-name resources.
Its controller creates squares and pieces, selects a piece on the first click,
and swaps board occupants on the second click.

It is currently a board frontend: it does not enforce chess move rules and is
not connected to an AI engine.

## Source map

- `src/main/java/com/games/theory/chess` — controller and domain model.
- `src/main/resources/Chess.fxml` — JavaFX view.
- `src/main/resources/data/DefaultState.data` — initial piece positions.
- `src/main/resources/data/Names.data` — piece display names.
