# Agent Briefing

For each assigned task, keep a temporary Markdown plan with implementation
checkboxes and update it as work progresses. Prefer tables and diagrams where
useful; choose sections to fit the task, with no fixed template. Remove the
plan before opening a PR, after updating durable docs and moving unfinished
work to [INBOX.md](INBOX.md). This is the only agent workflow gate.

Read the relevant context below before changing or reviewing an area. Preserve
existing user changes. Keep code, docs, plans, and user-facing messages in English.

| Task / area | Context |
| --- | --- |
| Structure, shared UI/utilities, build, packaging, Python runtime | [Architecture](docs/architecture.md) |
| Chess | [Chess domain](docs/domain/chess.md) |
| Tic-tac-toe rules, scoring, reset, activity | [Tic-tac-toe domain](docs/domain/tic-tac-toe.md) |
| AI moves, validation, availability | [AI domain](docs/domain/tic-tac-toe-ai.md), tic-tac-toe domain, and architecture |
| Technical alternatives and past decisions | Relevant notes in [research](docs/research/) |
| Pending ideas and unfinished work | [INBOX.md](INBOX.md) |
| Documentation maintenance or requested review | Relevant skill in [.agents/skills](.agents/skills/) and affected docs |

Update durable documentation when behavior, architecture, packaging, or data
meaning changes. Keep it concise and describe implemented behavior; preserve
research evidence and distinguish proposals from current support.

Keep repeatable verification under `scripts/` when needed and run checks
proportionate to the change (commands are in architecture). Preserve AI stdout
for its machine-readable move contract and stderr for diagnostics. Do not edit
or commit generated `build/` or `.gradle/` output.
