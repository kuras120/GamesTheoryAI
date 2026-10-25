# Inbox

Ideas and unfinished work. Entries need scoping when picked up.

## Python backend delivery experiment

Source: [delivery research](docs/research/python-backend-delivery-options.md).
The current wheelhouse runtime is implemented; the standalone backend remains
an unimplemented research recommendation.

- [ ] Prototype a backend wrapper with `init` and `move` in the backend repository.
- [ ] Compare PyInstaller `onefile` and `onedir` on macOS: resources, size,
  cold/warm latency, JSON stdout, diagnostic stderr, exit codes, and explicit data paths.
- [ ] Verify without Python on `PATH`, then reproduce on Windows and Linux.
- [ ] Try Nuitka only if PyInstaller misses an evaluation criterion; consider
  `uv` separately if first-launch downloads are acceptable.
- [ ] If the experiment succeeds, publish signed or checksummed artifacts and
  integrate a pinned backend into OS-specific Java distributions. Verify that
  learned data survives upgrades; keep process lifetime as a separate decision.

## AI asynchronous error handling

- [ ] Guard failed move callbacks with the request generation so a failure from
  before reset cannot disable AI or append failure activity in the new game.
- [ ] Map coordinate integer overflow to `AiException` so malformed backend
  output follows normal recovery instead of leaving the board disabled.

Evidence: `IntegrationService.changeTurn` checks generation only for successful
responses; `PythonExecutor.parseMove` calls `Integer.parseInt` without handling
`NumberFormatException`. These are existing limitations found during doc verification.
