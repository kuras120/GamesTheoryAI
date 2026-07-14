# Python runtime bootstrap

Status: `APPROVED FOR IMPLEMENTATION`

Approved by the repository owner before implementation.

## Problem

The tic-tac-toe AI currently relies on a Python virtual environment created in
`build/resources/main`. That couples runtime setup to the Gradle build, causes
platform-specific files and symbolic links to enter the resource tree, and does
not provide a controlled response when Python is missing from the user's
machine.

The application needs to create its own virtual environment on the target
machine. Chess and human-versus-human tic-tac-toe must remain usable when the AI
runtime is unavailable.

## Intended outcome

- GamesTheoryAI requires a system Python installation in version 3.9 or newer
  to enable the tic-tac-toe AI.
- Java detects Python on `PATH` when the tic-tac-toe view starts.
- Java creates and maintains a private virtual environment in application data,
  not in the repository or JAR.
- The `games_theory` wheel remains a packaged Java resource and is extracted
  before installation.
- Python initialization runs outside the JavaFX application thread.
- Missing or unsupported Python disables only the AI option and presents an
  actionable installation message.
- AI state is stored in an explicit application-data directory rather than the
  process working directory.

## Non-goals

- Installing Python automatically.
- Bundling CPython, `uv`, PyInstaller, or a portable Python runtime.
- Supporting Python older than 3.9.
- Changing the `games_theory` learning algorithm or JSON move contract.
- Redesigning chess.
- Introducing a persistent Python process; one process per move remains the
  accepted model for this project.

Alternative delivery models remain documented in
[`docs/research/python-backend-delivery-options.md`](../research/python-backend-delivery-options.md).

The minimum version is taken from the bundled package metadata
(`Requires-Python: >=3.9`), not from the Python version used by a developer or
CI runner.

## Proposed product behavior

### AI states

| State | Checkbox | Status text | Board |
| --- | --- | --- | --- |
| Preparing | disabled and unselected | `Preparing AI…` | playable without AI |
| Available | enabled and unselected | `AI available` | playable |
| Python missing | disabled and unselected | `AI unavailable. Install Python 3.9 or newer and restart the application.` | playable |
| Python unsupported | disabled and unselected | detected version and minimum requirement | playable |
| Setup failed | disabled and unselected | short setup error with restart guidance | playable |
| Move execution failed | disabled and unselected | AI runtime error | playable manually |

AI does not become selected automatically after background setup. This avoids
changing players in the middle of an already started game. The user explicitly
selects AI after it becomes available.

### Python discovery

The term `PATH` is used here rather than Java classpath. Candidates are checked
in this order:

- Windows: `py -3`, `python3`, `python`;
- macOS and Linux: `python3`, `python`.

Each candidate is executed with `--version`. A compatible interpreter must
report Python major version 3 and minor version 9 or newer. A command that
cannot start, times out, returns a non-zero exit code, or produces an
unparseable version is skipped.

The selected command may contain more than one token (`py -3`), so it must be
represented as a command prefix rather than a single executable string.

## Application directories

Use OS-appropriate application data:

```text
Windows: %LOCALAPPDATA%/GamesTheoryAI
macOS:   ~/Library/Application Support/GamesTheoryAI
Linux:   $XDG_DATA_HOME/GamesTheoryAI
         or ~/.local/share/GamesTheoryAI
```

Proposed layout:

```text
GamesTheoryAI/
├── runtime/
│   ├── games_theory.whl
│   ├── installed-wheel.sha256
│   └── venv/
└── data/
    ├── config.json
    ├── qtable.json
    └── state.json
```

The learning data is independent from the virtual environment so rebuilding or
upgrading the runtime does not erase the Q-table.

## Bootstrap flow

```text
JavaFX controller starts
        |
        v
Disable and clear AI checkbox; show Preparing
        |
        v
Start bootstrap on a virtual/background thread
        |
        v
Discover Python on PATH and validate >= 3.9
        |
        +---- unavailable ----> publish disabled AI state
        |
        v
Create application directories
        |
        v
Extract games_theory wheel from classpath/JAR
        |
        v
Create venv directly at its final target path
        |
        v
Run venv Python: -m pip install <wheel>
        |
        v
Run games-theory-init <application-data-directory>
        |
        v
Publish available AI state on JavaFX thread
```

The environment is created directly at its final path because virtual
environment entry scripts contain absolute interpreter paths and must not be
moved afterward.

## Runtime reuse and upgrades

The wheel is copied from application resources on every bootstrap and its
SHA-256 is calculated.

- If `venv`, required entry points, and a matching hash marker exist, skip
  environment creation and package installation.
- If the marker differs, install the new wheel with the environment's Python
  and update the marker only after success.
- If the environment is incomplete or its Python cannot execute, rebuild only
  the `runtime/venv` directory. Never delete `data/`.
- `games-theory-init <application-data-directory>` may run on every successful
  bootstrap because the command preserves existing files unless overwrite is
  explicitly requested.

Concurrent application instances must not modify the same environment at the
same time. Bootstrap acquires a lock file under `runtime/`; failure to acquire
it within a timeout disables AI with a retry/restart message.

## Proposed code boundaries

### Shared utilities

`ApplicationDirectories`

- resolves the OS-specific application-data root;
- contains no tic-tac-toe behavior.

`CommandRunner` and `CommandResult`

- execute argument lists with a timeout;
- consume stdout and stderr concurrently;
- return exit code and both streams;
- allow fake command execution in unit tests.

`DataReaderUtils`

- keeps existing model-reading behavior;
- gains resource-to-filesystem copying through `getResourceAsStream`, which
  works for exploded classes and packaged JARs.

### Tic-tac-toe integration

`PythonRuntimeManager`

- discovers and validates Python;
- owns application directories, locking, wheel extraction, venv creation,
  installation, initialization, and hash markers;
- returns a ready `PythonRuntime` or an unavailable result with a user-facing
  reason.

`PythonRuntime`

- immutable paths to venv Python, `games-theory`, `games-theory-init`, and the
  application-data directory.

`PythonExecutor`

- no longer installs dependencies;
- receives a ready runtime;
- invokes `games-theory --config <application-data-directory>`;
- validates process status and parses stdout JSON as today.

`IntegrationService`

- initializes the board immediately;
- starts runtime preparation asynchronously;
- publishes AI state through `Platform.runLater`;
- disables AI after a fatal runtime or move-execution failure.

### JavaFX view

`TicTacToe.fxml` gains a small wrapping label below the AI checkbox. The label
is the single source of user-visible runtime status. It must not expose command
lines, stack traces, or filesystem paths.

## Build changes

- Remove `createPythonVenv`, its task dependency, and the JAR/Shadow JAR venv
  exclusions from `libs.gradle`. The application creates the environment, so
  Gradle must neither create nor specially exclude one.
- Continue downloading the wheel during resource preparation and always store
  it under the stable resource name `games_theory.whl`. Runtime code must not
  contain a wheel version or distribution filename.
- Add a `GAME_THEORY_VERSION` Gradle property. Its default value is `latest`;
  a release tag can be supplied for reproducible builds without changing the
  destination resource name.
- Make `processResources` depend explicitly on the download task so task order
  is deterministic.
- Do not place `venv` under `build/resources/main` and do not package it in JAR
  or Shadow JAR.
- Remove Python and virtualenv setup from the ordinary CI build because Gradle
  no longer creates or runs a Python environment. Runtime-bootstrap tests use
  fake command execution; a real-Python smoke test can remain a separate job.

## Error handling

- Missing or old Python is an expected availability state, not an application
  exception.
- Filesystem, `venv`, `pip`, and initialization failures are logged with full
  diagnostic context but mapped to concise UI messages.
- Installation and command timeouts destroy the child process.
- stderr is diagnostic output. It does not make a successful command fail by
  itself.
- A non-zero exit code always fails the current bootstrap or move.
- Interrupted threads restore the interrupt flag.

## Dependency and network constraint

The current `games_theory` wheel metadata declares `jsbeautifier>=1.14.0`.
Installing only the bundled wheel may therefore contact a package index on
first setup. The base proposal reports installation failure clearly and does
not bundle an offline wheelhouse; proposal review may expand that scope.

Before implementation acceptance, review must confirm one of these choices:

1. first-time AI setup may require network access; or
2. extend this project to download and package all transitive wheels during the
   Gradle build.

## Test plan

### Unit tests

- parse valid Python versions including `3.9`, `3.14`, and patch suffixes;
- reject Python 2, Python 3.8, malformed, timed-out, and failed candidates;
- preserve multi-token Windows command prefixes;
- resolve application directories for Windows, macOS, Linux/XDG, and Linux
  fallback;
- copy a classpath resource to a temporary directory;
- create the expected bootstrap commands using a fake `CommandRunner`;
- skip installation when the wheel hash marker matches;
- reinstall without deleting data when the hash changes;
- map missing Python and command failures to unavailable AI states;
- include `--config <application-data-directory>` in every move command.

### Integration/build verification

- `./gradlew clean build` completes without packaging a virtual environment;
- JAR and Shadow JAR contain the wheel but no `venv/` entries;
- the default build resolves the latest wheel, while a build with an explicit
  `GAME_THEORY_VERSION` resolves the requested release under the same resource
  name;
- existing processor and move-parser tests remain green;
- a manual JavaFX check covers Preparing, Available, Missing Python, and setup
  failure states;
- a manual smoke test confirms that generated `data/` appears only under the
  application-data directory.

## Proposed implementation stages

1. Add command execution and application-directory utilities with tests.
2. Add runtime discovery, version validation, and unavailable results.
3. Add wheel extraction, locking, venv creation, installation, initialization,
   and hash reuse.
4. Refactor `PythonExecutor` to consume the ready runtime and explicit data
   path.
5. Add JavaFX AI status and asynchronous orchestration.
6. Remove build-time venv creation and make wheel resource ordering explicit.
7. Parameterize latest versus pinned wheel resolution and simplify CI.
8. Run automated verification and prepare the implementation review summary.

Permanent domain and guideline documentation is deliberately not updated in
these stages. It is updated only after implementation review and acceptance.

## Acceptance criteria

- With Python 3.9+ on `PATH`, AI setup succeeds without a build-local venv.
- With no Python on `PATH`, the application starts, games remain playable, AI
  stays disabled, and the minimum-version message is visible.
- With Python older than 3.9, the detected incompatibility is visible.
- Runtime initialization never blocks the JavaFX thread.
- The venv and AI data are outside the repository and packaged application.
- Existing Q-learning data survives runtime recreation and wheel upgrades.
- The wheel can be read from both Gradle-run resources and a packaged JAR.
- AI moves use the explicit data directory and retain the stdout/stderr
  contract.
- Full automated build and tests pass.

## Approved review decisions

- First-time setup may use the network to install `jsbeautifier`; an offline
  wheelhouse is outside this implementation.
- Successful bootstrap leaves AI unselected.
- Installing Python after application startup requires an application restart;
  a retry action is outside this implementation.
- Runtime discovery and application-data paths support Windows, macOS, and
  Linux in the initial implementation. Automated tests cover platform-specific
  selection; manual JavaFX verification may remain platform-dependent.
