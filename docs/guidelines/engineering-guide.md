# Engineering Guide

This guide defines how GamesTheoryAI code should be designed, implemented,
tested, and reviewed.

## Application Structure

- Give each class, module, and build task one primary responsibility.
- Prefer small collaborators composed by an orchestrator over a class that
  mixes discovery, process execution, parsing, hashing, and filesystem work.
- Keep orchestrators focused on sequencing and mapping outcomes. Move detailed
  work behind narrow interfaces or intention-revealing methods.
- Keep game rules and product behavior separate from JavaFX controllers,
  external processes, and filesystem infrastructure.
- Keep shared utilities free of chess- or tic-tac-toe-specific decisions.
- Keep build task declarations focused on dependencies, inputs, outputs, and
  high-level actions. Extract multi-step build logic into small helper methods.
- Inject infrastructure boundaries where replacement is needed for testing;
  do not construct process and filesystem dependencies inside game logic.
- Use Lombok for mechanical Java boilerplate such as constructors for injected
  final collaborators, accessors, logging fields, and utility-class structure.
  Keep explicit code when construction performs validation or setup, or when
  generated behavior would hide ownership, mutation, or domain semantics.

## JavaFX And Concurrency

- Do not block the JavaFX application thread with filesystem, network, child-
  process, dependency-installation, or other long-running work.
- Publish UI state changes on the JavaFX application thread.
- Keep presentation state in controllers or shared UI components and keep it
  out of game rules and runtime-integration services.
- Invalidate asynchronous results when the game state that produced their
  input has been reset or replaced.
- Preserve unrelated game functionality when an optional capability is
  unavailable.

## JavaFX Presentation

- Keep standard JavaFX controls as the default control boundary. Use AtlantaFX
  as the shared theme layer and add library-specific controls only when they
  satisfy a named interaction requirement.
- Apply `PrimerLight` before loading a view. Map the runtime dark-mode choice
  centrally to `PrimerLight` or `PrimerDark`; controllers must delegate theme
  selection instead of constructing themes themselves.
- Keep visual rules in the shared application stylesheet. Prefer semantic
  style classes over inline Java or FXML styles.
- Use AtlantaFX looked-up colors for theme-sensitive surfaces, borders, text,
  focus, and selection states. Fixed colors are acceptable only for deliberate
  identity such as the chessboard palette, and they must remain legible in both
  themes.
- Keep theme controls separate from game and integration services. Changing a
  theme must not rebuild the scene or mutate game, score, activity, selection,
  or AI state.
- Verify light and dark appearance, keyboard focus, disabled controls, and
  layout bounds when presentation code, FXML, CSS, or UI dependencies change.

## External Processes And Runtime Integration

- Pass external commands as explicit argument lists and set bounded execution
  times.
- Consume standard output and standard error independently so neither stream
  can block the process.
- Reserve standard output for machine-readable process contracts and standard
  error for diagnostics.
- Validate exit status, output shape, value ranges, and current application
  state before applying an external result.
- Keep mutable runtime data outside packaged resources and repository paths.
- Make runtime preparation safe to repeat and safe under concurrent launches.
- Expose missing optional runtimes as clear availability states instead of
  failing unrelated application features.

## Error Handling

- Preserve detailed technical diagnostics while presenting concise,
  actionable messages to players.
- Distinguish unavailable prerequisites, preparation failures, invalid process
  results, and fatal runtime failures.
- Restore a usable UI state after recoverable asynchronous or process errors.
- Avoid catching exceptions without either handling the failed state or
  preserving useful context for diagnosis.

## Testing

- Add unit tests when domain rules, parsing, validation, path selection,
  command construction, state transitions, or failure handling change.
- Replace process, filesystem, time, and runtime-discovery boundaries in tests
  instead of relying on the host environment.
- Cover malformed output, non-zero exits, timeouts, unavailable dependencies,
  stale asynchronous results, and invalid moves where relevant.
- Keep fixtures deterministic and independent of network services or local
  platform runtimes.
- Run checks proportionate to the change and record any outstanding manual
  JavaFX or cross-platform verification.

## Code Review Standard

- Prioritize correctness, game-state integrity, UI-thread safety, process
  lifecycle failures, persistence compatibility, and behavioral regressions.
- Verify that responsibilities and side effects have clear ownership.
- Confirm that asynchronous results cannot mutate stale or reset state.
- Require tests for changed rules, parsing, validation, persistence, and
  failure handling.
- Check that abstractions reduce coupling without hiding domain behavior or
  runtime contracts.
- Avoid cosmetic feedback unless readability obscures behavior, safety, or
  correctness.
