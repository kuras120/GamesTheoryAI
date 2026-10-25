---
name: maintain-docs
description: Create or update durable repository documentation so it matches implemented behavior. Use when behavior, configuration, architecture, data meaning, setup, verification, portability, research conclusions, or repository routing changes.
---

# Documentation

Use AGENTS.md for context routing and available verification commands.

1. Read every document routed for the affected area, the implementation, and existing
   docs; describe observed behavior, not intentions.
2. Use repository routing when present to select one authoritative home for each
   fact. Do not duplicate project-specific facts in reusable skills or general guides.
3. When present, keep the root README lean: purpose, safety boundary, quick start,
   and essential documentation entry points. Do not add links to individual
   temporary plans.
4. Distinguish current support, known limitations, and proposals. Add dates and source links to time-sensitive research.
5. Update all affected links and commands. Mark generated artifacts clearly and do not edit them as source.
   When compressing, preserve unique facts and constraints; weigh the benefit of
   each deletion against information loss. Check diagram details against code,
   including alternate paths, errors, and asynchronous state changes.
6. Run the repository verification command when declared; otherwise choose
   proportionate checks. Include plan-specific checks, then check Markdown links
   and paths before closeout.
7. Update durable documentation during implementation. Before opening a PR,
   move unfinished work to INBOX and remove the temporary plan.
8. Treat temporary plans as one-way references: they may link to durable docs, but
   root guidance, reusable skills, durable docs, and other plans must not link to a
   specific temporary plan.
9. Before closeout, audit the affected documents for duplicated facts, stale history,
   and routing mistakes. Use `audit-docs`
   when available; otherwise perform these checks directly. Keep the audit focused
   on the changed area.
