# Python backend delivery options

Status: research recommendation; not current runtime behavior

Last reviewed: 2026-07-14

## Decision

Choose how to deliver the `games_theory` backend with the JavaFX desktop
application. The production application should work offline and should not
require users to install or configure Python.

This document evaluates the viable architecture categories. Individual tools
may change, but each delivery model has the same fundamental trade-offs.

## Decision criteria

The preferred solution should provide:

1. no user-managed Python installation;
2. offline play after installing GamesTheoryAI;
3. Windows, macOS, and Linux support;
4. predictable stdout, stderr, and exit-code behavior;
5. isolated and persistent Q-learning data;
6. reproducible, versioned artifacts;
7. acceptable startup and move latency;
8. a clear ownership boundary between the Java application and Python backend.

## Shared runtime contract

Every local option should expose the same application-facing contract:

```text
games-theory-backend init <application-data-directory>
games-theory-backend move --config <application-data-directory> <scores> <cells...>
```

The backend writes only the move JSON to stdout, diagnostics to stderr, and
uses a non-zero exit code for failure. Mutable data lives outside the packaged
application:

```text
<application-data>/GamesTheoryAI/
├── runtime/
└── data/
    ├── config.json
    ├── qtable.json
    └── state.json
```

Java must pass this directory explicitly. Neither initialization nor move
selection may depend on the process working directory.

## Option 1: require system Python and create a virtual environment

The application discovers a compatible Python installation, creates a private
virtual environment on first launch, and installs the bundled wheel into it.

Advantages:

- smallest application download;
- one Java distribution can support several operating systems;
- easy to debug using normal Python tooling;
- minimal change to the current wheel-based release process.

Disadvantages:

- Python becomes an undocumented product prerequisite unless installation is
  handled explicitly;
- discovery differs between Windows, macOS, and Linux;
- compatible Python versions and architectures must be validated;
- first launch is slower and may require network access for dependencies;
- system Python upgrades can invalidate an existing environment;
- corporate machines may block Python, `pip`, or process execution.

Assessment: acceptable for contributors and development builds, but not the
preferred production experience.

## Option 2: bootstrap managed Python with `uv`

The application ships or downloads the standalone `uv` executable. On first
launch, Java asks `uv` to install a pinned managed Python version, create a
private environment, and install the bundled `games_theory` wheel. `uv`
supports automatic Python downloads and virtual-environment management without
an existing Python installation. See the official guides for
[installing managed Python](https://docs.astral.sh/uv/guides/install-python/)
and [working with environments](https://docs.astral.sh/uv/pip/environments/).

Advantages:

- the user does not install Python manually;
- the project does not build or redistribute CPython itself;
- Python and environment versions can be pinned;
- `uv` handles platform-specific managed Python selection;
- the same Java bootstrap workflow applies across supported systems;
- substantially less packaging work than PyInstaller or embedded CPython.

Disadvantages:

- first launch requires network access unless Python archives are also bundled;
- `uv` itself is a platform-specific executable that must be selected,
  extracted, or downloaded and checksum-verified;
- installation can fail behind proxies or restrictive corporate networks;
- first launch downloads a relatively large Python runtime;
- the application owns bootstrap progress, retries, locking, cleanup, and
  user-facing errors;
- fully offline installation still requires bundling the managed Python
  archives and loses much of the simplicity benefit.

Assessment: strongest near-term option if network access on first launch is
acceptable. It removes the user-managed Python prerequisite without requiring
the project to build Python binaries.

## Option 3: package an existing virtual environment

The build creates `venv` and places it in the JAR or application archive.

Advantages:

- superficially close to the current implementation;
- no first-run dependency installation.

Disadvantages:

- virtual environments contain platform-specific binaries, absolute paths, and
  symbolic links;
- they are not portable across operating systems or architectures;
- a JAR resource is not a normal executable filesystem path;
- POSIX permissions and symbolic links are fragile in ZIP-based packaging;
- a Linux CI-created environment cannot serve Windows or macOS users.

Assessment: reject. The Python 3.14 `𝜋thon` symlink exposed the packaging
problem, but removing that one link would not make a virtual environment
portable.

## Option 4: bundle an embedded CPython runtime

Each OS-specific application includes a pinned Python interpreter and installed
backend packages next to the Java runtime. Java invokes that interpreter using
an absolute path controlled by the application.

Advantages:

- no system Python requirement;
- standard Python behavior and straightforward backend debugging;
- dependencies can be prepared before release;
- no extraction overhead when the runtime remains unpacked.

Disadvantages:

- separate artifacts are required for every OS and architecture;
- distribution size grows significantly;
- Python layouts and redistribution differ by platform; CPython documents a
  dedicated [embeddable package on Windows](https://docs.python.org/3/using/windows.html#the-embeddable-package),
  but equivalent packaging needs separate work on macOS and Linux;
- the application becomes responsible for interpreter security updates;
- entry scripts and library search paths must remain relocatable.

Assessment: viable and predictable, but it creates the most runtime-packaging
work for the Java repository.

## Option 5: freeze the backend as a standalone executable

A freezer such as PyInstaller collects the entry script, Python interpreter,
imports, and resources into a platform-specific executable or directory.
PyInstaller explicitly supports self-contained `onefile` and `onedir` bundles;
users do not need Python installed. It must run separately for each target OS
and architecture. See the official
[operating model](https://pyinstaller.org/en/stable/operating-mode.html).

Advantages:

- no system Python or first-run installation;
- preserves the existing process boundary and stdout/stderr contract;
- Python packaging details remain isolated from Java;
- the backend can be versioned and released independently;
- small integration surface in the Java application.

Disadvantages:

- an OS/architecture CI matrix is required;
- dynamic imports and package resources may require hooks or a spec file;
- `onefile` extracts dependencies for every invocation and starts more slowly;
- current POSIX bundles use symbolic links, so `onedir` archives must preserve
  them. PyInstaller documents these constraints in
  [Common Issues and Pitfalls](https://pyinstaller.org/en/stable/common-issues-and-pitfalls.html#requirements-imposed-by-symbolic-links-in-frozen-application);
- macOS and Windows artifacts may require signing;
- newly generated executables can receive additional antivirus scrutiny.

Assessment: best match for the current architecture and the strongest
candidate for a proof of concept.

### `onefile` versus `onedir`

`onefile` is operationally simple because Java locates one executable. It is
less attractive when a new backend process is started for every move, because
the embedded runtime is extracted each time.

`onedir` avoids repeated extraction and is easier to diagnose, but the complete
directory must be distributed and POSIX symbolic links must survive packaging.

Both modes should be measured before selection.

## Option 6: compile/package with Nuitka

Nuitka translates Python modules through C and can create standalone or
one-file distributions. Its current modes and platform requirements are
described in the official [Nuitka user manual](https://nuitka.net/user-documentation/user-manual.html).

Advantages:

- no user-managed Python for standalone output;
- potential startup or execution improvements for some workloads;
- stronger transformation of Python source than a bytecode-focused freezer;
- retains a local executable process boundary.

Disadvantages:

- requires a native compiler toolchain in CI;
- builds are typically more complex and slower;
- still requires one build per OS and architecture;
- performance gains are workload-dependent and unnecessary unless measurement
  shows the Q-learning backend is CPU-bound;
- commercial-only features must be evaluated separately if they become
  relevant.

Assessment: credible fallback if PyInstaller cannot package the resources or
if measured performance justifies the extra build complexity. It is not the
first experiment.

## Option 7: run Python inside the JVM

A JVM Python implementation or embedding layer, such as GraalPy, can execute
Python from Java without a separate OS process. GraalPy documents both Java
embedding and Python package compatibility in its
[official documentation](https://www.graalvm.org/python/docs/).

Advantages:

- direct Java-to-Python calls without process startup or JSON parsing;
- one lifecycle controlled by the JVM;
- potential for a richer typed integration API.

Disadvantages:

- replaces the simple and well-isolated process boundary with an in-process
  language runtime;
- package compatibility must be verified rather than assumed;
- runtime size, startup, licensing, and Java distribution requirements change;
- Python failures can affect the JavaFX process directly;
- the current CLI contract and independently releasable backend become less
  useful.

Assessment: technically possible, but disproportionate for a backend that
already has a small CLI surface.

## Option 8: port the Q-learning backend to Java

The state encoder, repositories, reward policy, action selection, and move
derivation are reimplemented in the JVM.

Advantages:

- one language and runtime in production;
- no secondary process or Python packaging;
- simplest final desktop distribution;
- direct unit testing and debugging from Java.

Disadvantages:

- duplicates an existing maintained implementation;
- behavior can drift between Python and Java;
- model persistence and numerical behavior require compatibility tests;
- future backend changes must be implemented twice or Python must be retired;
- highest initial product-code effort.

Assessment: sensible only if GamesTheoryAI becomes the sole owner of the
algorithm or Python is intentionally removed from the product architecture.

## Option 9: use a remote AI service

Java sends the board state to a hosted service and receives the selected move.

Advantages:

- no Python runtime in the desktop application;
- backend updates deploy independently;
- centralized monitoring and model evolution;
- one client artifact can serve all platforms.

Disadvantages:

- requires network connectivity and service hosting;
- introduces latency, availability, privacy, authentication, and operating
  costs;
- offline play is lost;
- Q-learning state ownership becomes a server-side product decision;
- much larger security and operational scope.

Assessment: reject for the current offline desktop product. Reconsider only if
centralized models or multiplayer services become product requirements.

## Comparison

Ratings are relative to this project: 5 is best, 1 is worst.

| Option | User experience | Offline | Cross-platform effort | Runtime isolation | Maintenance | Overall fit |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| System Python + private `venv` | 2 | 3 | 3 | 4 | 3 | 3 |
| `uv` managed Python bootstrap | 4 | 2 | 4 | 4 | 4 | 4 |
| Packaged `venv` | 1 | 4 | 1 | 3 | 1 | 1 |
| Embedded CPython | 5 | 5 | 2 | 4 | 2 | 4 |
| PyInstaller/cx_Freeze-style executable | 5 | 5 | 3 | 5 | 4 | 5 |
| Nuitka standalone executable | 5 | 5 | 2 | 5 | 3 | 4 |
| Python inside JVM | 5 | 5 | 3 | 2 | 2 | 2 |
| Java port | 5 | 5 | 5 | 5 | 2 | 3 |
| Remote service | 4 | 1 | 5 | 5 | 2 | 2 |

## Process lifecycle is a separate decision

The embedded-runtime and standalone-executable options can use either
lifecycle:

- start one backend process for every move;
- keep one backend process alive for the game and exchange newline-delimited
  requests and responses.

The first model preserves the current CLI and is simpler. The second reduces
startup cost but requires a persistent protocol, recovery after crashes, and
strict request correlation. It should be considered only after measuring the
packaged backend. Delivery format and process lifetime should not be changed in
the same proof of concept.

## Recommendation

### Production direction

Deliver `games_theory` as a versioned, checksummed, platform-specific standalone
backend executable. Build and publish the backend artifacts in the
`games_theory` repository, where Python dependencies and entry points are
owned. GamesTheoryAI should consume a pinned backend release and include only
the artifact matching its target OS and architecture.

Use PyInstaller for the first proof of concept because it preserves the current
CLI boundary with less build complexity than embedded CPython or Nuitka. This
is a recommendation for an experiment, not an irreversible tool choice.

If downloading dependencies on first launch is acceptable, run a smaller `uv`
bootstrap proof of concept first. It is the shortest route to a user experience
without a preinstalled Python. Keep the standalone executable as the production
direction when offline-first installation is required.

### Proof-of-concept sequence

1. Add one wrapper CLI exposing `init` and `move`.
2. Build both PyInstaller `onefile` and `onedir` variants on macOS.
3. Verify package resources, stdout/stderr separation, exit codes, and explicit
   data paths.
4. Test without Python available on `PATH`.
5. Measure size and cold/warm invocation latency.
6. Reproduce the successful variant on Windows and Linux CI runners.
7. Compare results with one Nuitka standalone build only if PyInstaller fails an
   acceptance criterion.
8. Publish signed or checksummed artifacts from the backend repository.
9. Integrate the selected executable into OS-specific Java distributions.

### Interim development behavior

Keep system Python and a local virtual environment only for contributor
workflows. Pin the CI Python version instead of using a moving `3.x` selector.
Do not package `venv` into JAR or distribution archives.

### Simplest implementation choices

- If a documented prerequisite is acceptable: require Python `>=3.9`, detect
  it from Java, and create the environment in application data.
- If first-run internet access is acceptable: bundle or download `uv`, let it
  install pinned managed Python, then create the environment.
- If neither Python nor network may be required: ship an embedded or frozen
  platform-specific backend.

## Acceptance criteria

- Users do not install Python or access the network to play.
- The backend works on every declared OS and architecture.
- `init` and `move` never write into the current working directory.
- stdout contains only the documented JSON response.
- stderr contains diagnostics and failures return non-zero exit codes.
- Backend versions and SHA-256 checksums are pinned by the Java build.
- Mutable Q-learning data survives application and backend upgrades.
- Cold and warm move latency meet an agreed threshold.
- CI verifies the executable on a machine environment where Python is absent
  from `PATH`.
