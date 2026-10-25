---
name: review-pull-request
description: Assess a pull-request or local diff on owner request, recommend a bounded corrective batch, and preserve non-blocking follow-ups.
---

# Pull Request Review

## Trigger And Boundary

Use this procedure only when the owner asks to review a pull request, its comments,
local changes, or the agent's completed changes. Do not wait automatically for CI, automated
reviewers, or human reviewers. Inspect the comments and check states available at the
time of the owner request.

Review one cohesive change. Do not require a pull request to contain only one product
or architecture area, and do not add unrelated work merely to make it larger.

## Evidence Collection

1. For a pull request, identify base and head and inspect the complete merge-base diff.
2. For local changes, inspect staged and unstaged diffs and read untracked files.
3. For material rewrites of durable documents, inspect a word-level diff to identify
   lost decisions, provenance, evidence, or constraints.
4. Read current review comments, when present, and distinguish them from unrelated
   discussion.
5. Use existing deterministic checks as evidence. Do not manually repeat a passing
   structure, formatting, index, or local-reference check unless diagnosing it.

## Triage

Present actionable comments in this table before recommending changes:

| Finding | Evidence or risk | Counterargument or limitation | Cost | Recommendation |
| --- | --- | --- | --- | --- |
| [Comment or observed issue] | [Concrete affected behavior or contract] | [Why it may not matter or be in scope] | low / medium / high | fix now / defer / reject / owner decision required |

Use these dispositions:

- **Fix now**: confirmed correctness, safety, data-loss, broken-reference, or
  acceptance-criteria defect.
- **Defer**: potentially useful but non-blocking work that needs a later, bounded
  change. Record it in the repository inbox when one exists, or report it to the
  owner in the review summary.
- **Reject**: stylistic preference, speculative future case, duplicate work, or
  disproportionate scope expansion. State the counterargument.
- **Owner decision required**: material product, architecture, data, or scope choice
  not already authorized.

For a review-only request, report findings. Apply corrections when the owner's
request authorizes fixes. Post review replies only with explicit authorization.

## Corrective Batch And Exit

When fixes are authorized, implement one coherent batch and run proportionate verification.
Reply to review comments only when the owner authorizes that response. Do not continue
the pull request indefinitely: after the corrective batch, send non-blocking new ideas
to the repository inbox when one exists, or report them to the owner. Continue only
for confirmed correctness, safety, data-loss, or owner-approved scope issues.

Read the affected domain and architecture docs when reviewing behavior or boundary
changes. Prioritize correctness, state integrity, UI-thread safety, process failures,
persistence compatibility, and regressions. Check that tests cover changed rules and
failure handling. Use existing scripts for mechanical validation where available.
