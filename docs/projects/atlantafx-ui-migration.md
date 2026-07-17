# AtlantaFX UI migration

## Status

- Phase: IMPLEMENTED

## Problem and intended outcome

The application declares MaterialFX 11.17.0, whose stable line has not had a
release since 2023. The application does not use MaterialFX controls or themes;
its only API reference is `StringUtils.EMPTY` in tic-tac-toe line prediction.
Keeping the dependency therefore adds maintenance and licensing exposure
without providing UI value.

Replace MaterialFX with AtlantaFX 2.1.0 and give the chess and tic-tac-toe
windows a consistent, modern presentation with selectable light and dark
themes. Preserve the game behavior, window constraints, board geometry, and
interaction flow. Refresh only the presentation layer: use toggle switches for
AI and theme selection, AtlantaFX style classes, and small layout refinements
without redesigning the screens or pushing theme concerns into game services.

## Scope

In scope:

- replace `io.github.palexdev:materialfx` with
  `io.github.mkpaz:atlantafx-base:2.1.0`;
- replace the `MATERIALFX_VERSION` property with an `ATLANTAFX_VERSION`
  property;
- replace `StringUtils.EMPTY` with the Java empty string and remove the
  MaterialFX import;
- apply AtlantaFX `PrimerLight` by default and allow the player to switch each
  running application between `PrimerLight` and `PrimerDark`;
- add an AtlantaFX `ToggleSwitch` labeled `Dark mode` to both game screens;
- replace the tic-tac-toe AI `CheckBox` with
  `atlantafx.base.controls.ToggleSwitch` in FXML and Java code;
- preserve the current chess and tic-tac-toe layouts, board sizes, labels,
  activity feed, score display, reset action, and AI status behavior;
- add a small shared presentation utility that owns theme selection so theme
  API calls are not duplicated across controllers;
- add a shared application stylesheet built on AtlantaFX looked-up colors and
  style classes for board surfaces, side panels, headings, coordinate labels,
  score display, selected squares, and the reset action;
- move purely visual inline chess styles to CSS/style classes where practical,
  without changing square lookup, selection, or piece-moving behavior;
- make narrow spacing, sizing, grouping, and style-class adjustments that give
  the existing interface a more polished hierarchy without changing its
  information architecture;
- update durable repository documentation after implementation acceptance if
  setup or UI implementation guidance changes.

Out of scope:

- persistence of theme selection between application launches;
- redesigning either game screen or changing window dimensions;
- replacing data-bearing standard JavaFX controls such as `ListView`,
  `TextArea`, or board panes when styling them is sufficient;
- adding ControlsFX, GemsFX, icons, animations, or other UI libraries;
- changes to game rules, scoring, AI preparation, Python integration, or
  failure messages;
- adopting a MaterialFX alpha, early-access release, or rewrite module.

## Confirmed decisions and assumptions

- AtlantaFX 2.1.0 is the selected replacement. It supports JavaFX 17+, which
  includes the repository's JavaFX 21.0.8 baseline.
- `PrimerLight` is the initial theme on every launch. Selecting `Dark mode`
  switches the running application to `PrimerDark`; clearing it returns to
  `PrimerLight`.
- Theme selection is runtime-only. The two games are separate application
  launches, so each starts in light mode and owns its current selection.
- Both games expose the theme control in a compact top-right presentation area
  and use the same theme-switching implementation.
- "Preserve the design" means retaining information architecture, control
  placement, board geometry, and behavior. AtlantaFX is expected to change
  colors, control chrome, typography details, focus indicators, and spacing
  intrinsic to its theme.
- The AI toggle keeps the current label `AI` and the adjacent status label.
- The AI and theme toggles are visually distinct controls. Changing the theme
  cannot enable, disable, or select AI.
- A disabled toggle remains visibly disabled while AI is preparing or
  unavailable. It remains unselected when preparation finishes, so enabling AI
  is always an explicit player action.
- The repository owner accepted this proposal on 2026-07-17.

## Affected components and boundaries

### Build configuration

- `build.gradle` changes the UI dependency coordinate.
- `gradle.properties` changes the corresponding version property.
- No repository or packaging changes are expected because AtlantaFX is
  available from Maven Central and is a regular Java dependency.

### Application startup

- Chess and tic-tac-toe `Main` classes install `PrimerLight` before loading
  their views and attach the shared application stylesheet to their scenes.
- A small shared theme utility maps a boolean dark-mode choice to
  `PrimerLight` or `PrimerDark` and updates JavaFX's user-agent stylesheet.
- Each view controller delegates its theme toggle action to that utility.
  Theme selection remains a presentation concern; domain and integration
  services do not select themes.

### Views and presentation stylesheet

- Both FXML roots gain a compact appearance control containing the `Dark mode`
  toggle without changing their existing content ordering.
- Existing controls receive semantic style classes instead of being replaced
  solely for appearance. Examples include game board, game cell, side panel,
  section heading, score display, coordinates, and primary/reset action.
- The shared stylesheet uses AtlantaFX looked-up colors instead of fixed light
  backgrounds so borders, panels, focus states, and text remain legible in
  both themes.
- Chess keeps its recognizable alternating brown board. Theme-aware CSS owns
  the square colors, piece contrast, borders, and selected-square highlight
  previously expressed through inline styles.
- Tic-tac-toe keeps the current 4 x 4 grid and right-side activity panel. Theme
  styling may add surface separation and refined spacing, but does not move or
  replace the feed, score area, reset action, or AI status.

### Tic-tac-toe view and controller

- `TicTacToe.fxml` imports and instantiates `ToggleSwitch` instead of
  `CheckBox` while keeping the existing `fx:id`, label, disabled initial state,
  and position.
- The controller field type changes to `ToggleSwitch`.
- `IntegrationService` accepts `ToggleSwitch` and continues using only
  selected and disabled state. AI behavior does not depend on visual styling.
- The theme toggle is handled by the controller/shared theme utility and is
  never passed to `IntegrationService`.

### Shared and domain behavior

- `LinePredictor` returns `""` for missing nodes without importing MaterialFX.
- No domain contracts or game state transitions change.

## Proposed runtime flow

1. The selected game application starts.
2. The application installs AtlantaFX `PrimerLight` as the global user-agent
   stylesheet and attaches the application presentation stylesheet.
3. FXML loads the existing controls, the theme toggle on both screens, and the
   AI toggle on tic-tac-toe.
4. Selecting `Dark mode` asks the shared theme utility to install
   `PrimerDark`; clearing it restores `PrimerLight`. The scene remains live and
   game state is unchanged.
5. The tic-tac-toe controller passes only the AI toggle to
   `IntegrationService`.
6. AI preparation sets the AI toggle to unselected and disabled and displays
   the existing preparation message.
7. A successful preparation enables the still-unselected AI toggle. An
   unavailable result leaves it disabled and shows the existing explanation.
8. During play, selected-state checks continue to decide whether player `O` is
   controlled by AI.
9. Chess and all other tic-tac-toe interactions continue through their current
   controllers and services.

## Failure handling and user-visible behavior

- A missing or incompatible AtlantaFX artifact is a build-time failure; the
  application must not silently fall back to a different dependency version.
- An FXML import or controller type mismatch is an application-start failure
  and must be caught by build/manual verification before acceptance.
- AI preparation and runtime failures keep their current status text, toggle
  disabling, activity-feed entry, and recovery behavior.
- Theme application must not introduce an alternate runtime path. Both games
  either start with the accepted light theme or fail visibly during
  development.
- Switching theme must preserve scene contents, focusability, current game
  state, score, activity history, AI state, and board selection.
- Focus indication, disabled state, text contrast, board marks, and controls
  must remain readable. Narrow CSS overrides are allowed only where AtlantaFX
  defaults obscure existing content or alter necessary board geometry. Shared
  application CSS must use theme-aware looked-up colors except for the
  intentionally brown chessboard palette.
- Both toggles must remain keyboard-operable and expose visible selected,
  unselected, focused, and, for AI, disabled states.

## Implementation plan

1. [done] Replace the MaterialFX dependency and version property with
   AtlantaFX 2.1.0.
2. [done] Remove `StringUtils` from `LinePredictor`, use `""`, and confirm no
   MaterialFX references remain.
3. [done] Add the shared light/dark theme utility, install `PrimerLight` at
   startup, and attach the application stylesheet in both games.
4. [done] Add a `Dark mode` `ToggleSwitch` to both views and connect it only
   to the shared presentation utility.
5. [done] Replace the AI checkbox with `ToggleSwitch` across FXML,
   `Controller`, and `IntegrationService` without changing AI state behavior.
6. [done] Apply restrained theme-aware style classes and CSS to polish both
   views and move purely visual chess inline styles out of controller logic.
7. [done] Run automated verification and inspect dependency resolution.
8. [done] Launch both game windows in the available macOS environment and
   record the remaining interactive and cross-platform visual checks.
9. [done] Record implementation results, deviations, limitations, and manual
   evidence in this project file and set the phase to `IMPLEMENTED`.

## Verification

Automated:

- `./gradlew test`
- `./gradlew clean build`
- `./gradlew dependencyInsight --dependency atlantafx-base --configuration runtimeClasspath`
- `rg -n "MaterialFX|materialfx|StringUtils" build.gradle gradle.properties src`

Manual:

- run chess with `./gradlew run` and verify the board, coordinate labels, window
  size, focus treatment, and both themes;
- run tic-tac-toe with `./gradlew run --args='tictactoe'` and verify the 4 x 4
  board, reset button, activity feed, score area, status label, and both themes;
- switch light to dark and back during an active game in each application and
  confirm that board state, selection, score, activity, AI state, and focus are
  preserved;
- verify the theme toggle with mouse and keyboard and confirm that every launch
  begins in light mode;
- verify AI toggle states while preparing, available, selected, unselected,
  unavailable, and disabled after a runtime failure;
- verify the AI toggle with mouse and keyboard and confirm visible focus;
- verify manual play, AI play, scoring, activity scrolling, and reset retain
  current behavior;
- inspect tic-tac-toe at its minimum 1280 x 720 and maximum 1920 x 1080 bounds
  and confirm content is not clipped or overlapped;
- verify chess and tic-tac-toe on each supported desktop platform not covered
  by the implementation environment, or record the outstanding platform checks
  before implementation review.

## Acceptance criteria

- MaterialFX is absent from source imports, declared dependencies, resolved
  runtime dependencies, and version properties.
- `LinePredictor` returns the same empty value for missing input without a
  third-party utility.
- AtlantaFX 2.1.0 resolves from Maven Central; both applications start with
  `PrimerLight` and can switch live to `PrimerDark` and back.
- Both existing FXML screens load and retain their current structure and
  content.
- Both screens expose a keyboard-operable `Dark mode` toggle, and theme changes
  do not alter application or game state.
- The AI control is an AtlantaFX `ToggleSwitch`, labeled `AI`, with behavior
  equivalent to the previous checkbox.
- AI remains opt-in after preparation and unavailable/error states prevent
  selection.
- Game rules, AI execution, score calculation, activity entries, and reset
  behavior are unchanged.
- Presentation changes remain confined to FXML, CSS, theme/application startup,
  and narrow controller wiring; domain and integration behavior receive no
  theme dependency.
- Automated checks pass, and remaining cross-platform visual checks are
  explicitly recorded.

## Open questions

- None required before implementation. Light/dark behavior, non-persistent
  default, toggle usage, dependency version, and presentation boundaries are
  defined above.

## Result

- Replaced MaterialFX 11.17.0 with AtlantaFX 2.1.0. AtlantaFX's transitive
  OpenJFX dependency is excluded because it otherwise upgraded the resolved
  runtime from the repository baseline JavaFX 21.0.8 to JavaFX 23.
- Removed the only MaterialFX API use and added a regression test confirming
  that a missing line endpoint still returns `""`.
- Added a shared theme manager, `PrimerLight` startup, live `PrimerDark`
  selection, and a shared theme-aware application stylesheet.
- Added `Dark mode` toggles to both games and replaced the tic-tac-toe AI
  checkbox with an AtlantaFX toggle. AtlantaFX `ToggleSwitch` does not expose
  an FXML-compatible `onAction` property, so theme changes listen to
  `selectedProperty()` in each presentation controller instead.
- Reworked only presentation structure and styles: chess coordinate text
  fields became labels, visual chess styles moved from inline Java code to
  CSS classes, and tic-tac-toe received styled board cells, side panel, score,
  activity, and reset surfaces. Game and AI state flow did not change.
- Applied a theme-aware `winning-line` style to tic-tac-toe result lines after
  implementation review found that their default black stroke lacked contrast
  in dark mode. The line now follows AtlantaFX's foreground color and remains
  dark in light mode and light in dark mode.
- `./gradlew clean build` passed on 2026-07-17. Dependency inspection confirmed
  AtlantaFX 2.1.0 and JavaFX 21.0.8 on the runtime classpath. XML validation and
  the repository search for MaterialFX, `StringUtils`, and `CheckBox` passed.
- Both `./gradlew run` and `./gradlew run --args='tictactoe'` reached a running
  JavaFX application on macOS; tic-tac-toe also completed controller and AI
  initialization. The processes were then intentionally stopped.
- Outstanding manual review: visual comparison of both light and dark modes,
  mouse/keyboard toggle interaction, layout at tic-tac-toe minimum and maximum
  bounds, and Windows/Linux rendering. System screenshot capture was not
  available in the implementation environment.
