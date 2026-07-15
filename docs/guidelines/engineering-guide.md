# Engineering guide

Use these guidelines when designing or reviewing changes. `AGENTS.md` decides
which guideline applies to a task; this file captures project-level rules that
should not be scattered through implementation comments.

## Design and documentation

- Keep the root `README.md` short, visitor-oriented, and focused on product
  purpose, contents, quick start, and the documentation entry point.
- Describe implemented product behavior and external contracts in
  `docs/domain`.
- Keep domain documentation independent of implementation. Do not include
  source maps, file paths, class names, frameworks, build tools, or repository
  structure there; domain rules and contracts define the behavior that code
  must follow.
- Put mappings between domain concepts and their implementation in repository
  or engineering guides, never in domain documents.
- Keep operational repository information in the repository guide.
- Put evaluated alternatives in `docs/research`; link the directory rather than
  individual research files from `AGENTS.md`.
- Use one file under `docs/projects` for each cohesive change that requires
  design approval.
- Update permanent domain and guideline documentation only after the related
  implementation is accepted.

## Change workflow

Large changes follow the [project lifecycle](project-lifecycle.md): proposal,
proposal acceptance, implementation, implementation acceptance, and only then
permanent documentation plus project-file cleanup.

A passing build is verification evidence, not product or design acceptance.
Material deviations discovered during implementation are written back to the
active project before review.

## Implementation boundaries

- Give each class, module, and build task one primary responsibility. Prefer
  small collaborators composed by an orchestrator over a long class that mixes
  discovery, process execution, parsing, hashing, and filesystem operations.
- Keep orchestrators focused on sequencing and mapping outcomes. Move detailed
  work behind narrow interfaces or named methods that can be understood and
  tested independently.
- Keep build task declarations focused on dependencies, inputs, outputs, and
  high-level actions. Extract multi-step build logic into small, intention-
  revealing helper methods.
- Keep game rules and product behavior separate from external-process and
  filesystem infrastructure.
- Keep shared utilities free of tic-tac-toe- or chess-specific decisions.
- Make external commands explicit argument lists with bounded execution time
  and independently consumed output streams.
- Reserve standard output for machine-readable process contracts and standard
  error for diagnostics.
- Do not block the JavaFX application thread with filesystem, network, or child
  process work. Publish UI changes on the JavaFX thread.
- Prefer user-facing availability states for missing optional capabilities;
  preserve unrelated game functionality.

## Testing and review

- Add unit tests for domain rules, parsing, validation, path selection, and
  command construction where those behaviors change.
- Make infrastructure boundaries replaceable in tests instead of invoking real
  network services or platform runtimes.
- Run checks proportionate to the change and report commands and results.
- Record manual JavaFX and cross-platform checks that remain outstanding.
- Keep implementation comments focused on local intent; durable architecture
  and workflow decisions belong in documentation.
