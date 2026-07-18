# Chess domain

The chess game presents a board with an initial piece arrangement and readable
piece names. Selecting an occupied square chooses its piece. Selecting another
square moves the chosen piece by swapping the occupants of both squares.

It is currently a board frontend: it does not enforce chess move rules and is
not connected to an AI engine.

The shared light and dark theme behavior is defined by the
[application presentation contract](application-presentation.md).
