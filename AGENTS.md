# Agent Briefing

Use this file first to identify the required repository context. Read every
document routed for the touched area before planning, reviewing, or changing
it. Detailed rules and checklists belong in `docs/**`; do not duplicate them
here.

## Required Workflow

- For non-trivial changes, follow `docs/guidelines/project-lifecycle.md`,
  including its proposal and implementation acceptance gates.
- Keep active plans under `docs/projects/**`; remove completed plans after
  durable documentation and TODOs are updated.
- Use the smallest applicable set of documents from the routing table. Combine
  routes when a task crosses areas.
- Preserve existing user changes in the worktree.
- Keep code, documentation, plans, scripts, and user-facing messages in
  English.

## Repository Map

| Path | Responsibility |
| --- | --- |
| `src/main/java/com/games/theory/GUI.java` | Runtime entry point and game selection. |
| `src/main/java/com/games/theory/chess/**` | Chess application, controller, and board model. |
| `src/main/java/com/games/theory/tictactoe/**` | Tic-tac-toe UI, game rules, scoring, activity, storage, and AI integration. |
| `src/main/java/com/games/theory/tictactoe/integration/**` | Python runtime preparation, process execution, and AI move contract. |
| `src/main/java/com/games/theory/ui/**` | Shared JavaFX presentation behavior. |
| `src/main/java/com/games/theory/utils/**` | Shared application paths, resources, and process utilities. |
| `src/main/resources/**` | FXML views, stylesheets, and packaged application resources. |
| `src/test/**` | Unit and integration-boundary tests and their fixtures. |
| `build.gradle`, `libs.gradle`, `gradle.properties`, `settings.gradle`, `gradlew*`, `gradle/**` | Build, dependencies, Python wheel preparation, packaging, tests, coverage, and Gradle bootstrap. |
| `config/**` | Build-time constraints and dependency configuration. |
| `docs/domain/chess.md` | Implemented chess behavior. |
| `docs/domain/application-presentation.md` | Shared appearance, theme selection, and presentation-state behavior. |
| `docs/domain/tic-tac-toe.md` | Tic-tac-toe rules and player-facing behavior. |
| `docs/domain/tic-tac-toe-ai.md` | AI behavior, move contract, validation, and availability. |
| `docs/guidelines/repository-guide.md` | Environment, commands, testing, packaging, and release operation. |
| `docs/guidelines/engineering-guide.md` | Application design, coding, testing, and review rules. |
| `docs/guidelines/python-runtime.md` | Python dependency packaging, runtime bootstrap, paths, and failures. |
| `docs/guidelines/project-lifecycle.md` | Proposal, approval, implementation, and closeout process. |
| `docs/projects/**` | Temporary plans for active non-trivial work. |
| `docs/research/**` | Durable investigations and evaluated alternatives. |
| `scripts/**` | Repeatable verification and process automation. |
| `build/`, `.gradle/` | Generated build output; do not review or commit. |

## Task Routing

| Task or touched area | Read before work |
| --- | --- |
| Any source, resource, build, or test change | `docs/guidelines/repository-guide.md` and `docs/guidelines/engineering-guide.md` |
| Chess behavior, board interaction, or controller | `docs/domain/chess.md` |
| Tic-tac-toe rules, turns, scoring, reset, activity feed, or controller | `docs/domain/tic-tac-toe.md` |
| Tic-tac-toe AI behavior, move flow, protocol, validation, availability, or feedback | `docs/domain/tic-tac-toe.md` and `docs/domain/tic-tac-toe-ai.md` |
| Python discovery, wheelhouse, bootstrap, application data paths, or external process execution | `docs/domain/tic-tac-toe-ai.md` and `docs/guidelines/python-runtime.md` |
| Shared appearance, theme behavior, FXML, styling, or presentation infrastructure | `docs/domain/application-presentation.md` and every affected game domain document |
| Shared utilities or other cross-game behavior | Every affected domain document |
| Setup, Gradle, dependencies, packaging, tests, scripts, or release operation | Relevant sections of `docs/guidelines/repository-guide.md`; for Python packaging also read `docs/guidelines/python-runtime.md` |
| Tool selection or prior technical investigation | Relevant notes under `docs/research/**` |
| Documentation, review, refactoring, or runtime-flow changes | `docs/guidelines/engineering-guide.md` plus the documents for every touched area |
| Non-trivial planning and delivery | `docs/guidelines/project-lifecycle.md` and any matching active file under `docs/projects/**` |

## Repository-Specific Instructions

- Treat `src/main/**`, build and dependency configuration, game behavior, AI
  process contracts, packaged Python dependencies, and persistent application-
  data formats as production work.
- Keep repeatable verification and process automation under `scripts/**`.
- Preserve the tic-tac-toe AI process boundary: standard output is reserved
  for its machine-readable move contract and standard error for diagnostics.
- Update durable domain, guideline, or architecture documentation when
  behavior, boundaries, persistence meaning, packaging, or runtime flow
  changes.
- During review, apply `docs/guidelines/engineering-guide.md` and the required
  context for every touched area.
