# Application Presentation

Chess and tic-tac-toe share a consistent light and dark appearance. Every
application launch starts in light mode and provides a `Dark mode` toggle for
the running game.

Theme selection lasts only for the current application process. It is not
stored between launches, and each game starts independently in light mode.

Changing the theme updates presentation only. It preserves the current board,
selected squares, turn, score, activity history, AI selection and availability,
and any other active game state.

Both themes must keep text, board marks, focus indicators, selected states, and
disabled controls readable. Theme and AI toggles remain keyboard-operable and
visually distinct.
