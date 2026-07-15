# Agent instructions

Treat `docs` as the source of repository context and implementation guidance.
Do not duplicate repository knowledge in this file.

Read documentation in this order:

1. Start with [`docs/guidelines/repository-guide.md`](docs/guidelines/repository-guide.md)
   for setup, commands, module ownership, testing, and the documentation map.
2. Read the relevant file under [`docs/domain`](docs/domain) for current product
   behavior and integration contracts.
3. Use [`docs/guidelines/engineering-guide.md`](docs/guidelines/engineering-guide.md)
   when designing, implementing, or reviewing a change.
4. Check [`docs/projects`](docs/projects) for an active proposal before making a
   larger change, and follow
   [`docs/guidelines/project-lifecycle.md`](docs/guidelines/project-lifecycle.md)
   when a project exists or must be created.
5. Consult [`docs/research`](docs/research) for evaluated alternatives and prior
   technical investigation. Research is supporting context, not implemented
   behavior.

Use `README.md` only as the human-facing product overview and quick start.
