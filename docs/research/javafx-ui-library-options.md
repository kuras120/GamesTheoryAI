# JavaFX UI library options

Status: research snapshot

Last reviewed: 2026-07-16

## Decision context

The evaluation considered a sustainable JavaFX UI library strategy for an
application built with JDK 21 and JavaFX 21.0.8 that declared MaterialFX
11.17.0 at the time.

At the time of evaluation, the application did not use MaterialFX controls or
themes. Its only MaterialFX API reference was `StringUtils.EMPTY` in
`LinePredictor`. Replacing that value with the Java empty string would remove
the dependency without a UI migration.

GitHub stars, contributor counts, commit activity, releases, and licenses can
change after the review date.

## Method

The comparison covers libraries that provide broad JavaFX controls, a modern
theme, or both. Specialized charting, calendar, icon, animation, test, and
application-framework libraries are outside the comparison.

- Stars, contributors, commits, branches, tags, and GitHub releases were read
  from the GitHub API on 2026-07-16.
- Contributor counts are lifetime entries returned by GitHub's contributors
  API, including anonymous contributors. They measure community breadth, not
  the number of currently active maintainers.
- A release means a stable published artifact. Maven Central was used when a
  project published a tag or artifact without creating a GitHub Release.
- Release frequency is the number of stable releases in the trailing 12 and 24
  months. Pre-releases such as alpha, beta, RC, and EA were excluded.
- Recent commits count commits to the default branch since 2025-07-16. This is
  a supporting signal: release cadence matters more to consumers than work on
  an unreleased branch.

## Quantitative comparison

| Library                                                           | Scope                                                                 | Stars | Contributors | Latest stable release | Stable releases, 12 / 24 months | Default-branch commits, 12 months | License      |
|-------------------------------------------------------------------|-----------------------------------------------------------------------|------:|-------------:|-----------------------|--------------------------------:|----------------------------------:|--------------|
| [MaterialFX](https://github.com/palexdev/MaterialFX)              | Material-styled replacement controls and utilities                    | 1,428 |           15 | `11.17.0`, 2023-11-13 |                           0 / 0 |                                 0 | LGPL-3.0     |
| [AtlantaFX](https://github.com/mkpaz/atlantafx)                   | Modern CSS themes for standard controls, plus a small set of controls | 1,375 |            9 | `2.1.0`, 2025-07-11   |                           0 / 1 |                                17 | MIT          |
| [ControlsFX](https://github.com/controlsfx/controlsfx)            | Mature supplementary controls and APIs                                | 1,701 |          182 | `11.2.3`, 2025-12-09  |                           1 / 2 |                                 7 | BSD-3-Clause |
| [GemsFX](https://github.com/dlsc-software-consulting-gmbh/GemsFX) | Broad modern controls and utilities                                   |   618 |           22 | `4.3.0`, 2026-07-15   |                        44 / 105 |                               290 | Apache-2.0   |
| [JFoenix](https://github.com/sshahine/JFoenix)                    | Material Design replacement controls                                  | 6,330 |           81 | `9.0.10`, 2020-06-04  |                           0 / 0 |                                 0 | MIT          |

The zero in AtlantaFX's trailing 12-month release column is a boundary effect:
version 2.1.0 was released five days before the 12-month cutoff. It is still
materially fresher than MaterialFX, but its cadence is irregular rather than
continuous.

## Qualitative comparison

### MaterialFX

MaterialFX remains the closest match when the requirement is specifically a
large Material Design control set. Its current stable artifact is not being
maintained through regular releases, however. Version 11.17.0 was published in
November 2023, and the default branch's last commit was in June 2024.

The project is not completely abandoned. Its `rewrite` branch was updated on
2026-05-28, and the maintainer continues publishing extracted building blocks
such as MFXCore, MFXResources, and VirtualizedFX. The replacement umbrella
artifacts are alpha or early-access builds rather than a stable successor to
11.17.0. This is evidence of active development, but not yet of a dependable
upgrade path for a production application.

Strengths:

- closest visual and API scope to an all-in-one Material Design toolkit;
- established adoption and a useful set of controls;
- building-block projects around the rewrite are active.

Risks:

- no stable release for over two and a half years;
- rewrite activity does not maintain the currently consumed stable line;
- a future rewrite is likely to make migration more substantial;
- LGPL is more restrictive operationally than the MIT, BSD, or Apache options.

Verdict: do not introduce new MaterialFX usage. Reassess the rewrite only after
it produces a stable release and a documented migration path.

### AtlantaFX

AtlantaFX is primarily a CSS-first theme library. It styles existing JavaFX
controls and supplies a smaller number of additions such as modal panes,
popovers, toggle switches, tab-like controls, and input helpers. The project
explicitly says that becoming another full control library is not its main
goal. It requires JavaFX 17 or newer, so the repository's JavaFX 21 baseline is
supported.

Strengths:

- lowest migration coupling: standard JavaFX controls remain standard controls;
- modern light and dark themes with customization support;
- compatible with FXML and the current JavaFX baseline;
- permissive MIT license;
- can be combined with focused control libraries.

Risks:

- one primary maintainer and only nine lifetime contributors;
- last code activity was in August 2025;
- releases have long gaps;
- not a replacement for MaterialFX-specific advanced controls.

Verdict: the best styling foundation if the application needs a modern visual
refresh while keeping its UI portable.

### ControlsFX

ControlsFX complements, rather than replaces, standard JavaFX. Its control set
includes validation, notifications, popovers, searchable and checkable lists,
range sliders, property sheets, spreadsheet views, and other mature desktop
patterns. It has the broadest contributor base in the viable set. Development
and releases are slow but continuing: 11.2.3 was published in December 2025,
and the default branch was updated in March 2026.

Strengths:

- by far the broadest contributor base;
- mature APIs, samples, and documentation;
- low visual lock-in because it complements standard JavaFX;
- permissive BSD license.

Risks:

- slow cadence and a large outstanding issue backlog;
- not a modern theme and not Material Design;
- should be added for needed controls, not merely to style the application.

Verdict: the safest general-purpose control extension, particularly when a
specific ControlsFX component solves a real requirement.

### GemsFX

GemsFX is the most actively released library in the comparison. It provides
date and time controls, input fields, responsive panes, drawers, dialogs,
image controls, progress indicators, filters, and many utilities. The library
module targets Java 11 and JavaFX 17+, so it is compatible with this
repository's Java 21 and JavaFX 21 baseline even though its demo uses newer
versions.

Strengths:

- strongest current maintenance signal by a wide margin;
- useful modern controls that neither core JavaFX nor AtlantaFX supplies;
- Apache-2.0 license;
- explicit AtlantaFX integration and styling support.

Risks:

- smaller adoption and contributor base than ControlsFX;
- very high release frequency can mean more upgrade churn;
- broad transitive dependency footprint, including ControlsFX and several
  supporting libraries;
- it is a control collection, not a complete application theme.

Verdict: the best actively developed source of additional controls. Add it only
when its concrete controls justify the dependency tree.

### JFoenix

JFoenix has the most stars, but that number reflects historical popularity.
The last Maven Central release was in June 2020 and the default branch's last
code activity was in 2021. Its published line was designed around Java 8/9-era
JavaFX.

Verdict: exclude. Stars alone are a poor maintenance signal, and JFoenix is a
larger compatibility and support risk than the current MaterialFX dependency.

## Recommendation for this repository

1. Remove MaterialFX without replacing it. Replace the single
   `StringUtils.EMPTY` reference with `""` and delete the dependency and version
   property. This reduces risk immediately without changing UI behavior.
2. Keep standard JavaFX controls as the stable API boundary.
3. If a visual refresh is planned, use AtlantaFX as the theme layer. This is a
   separate product/design change and should be previewed on both game screens
   before adoption.
4. Add ControlsFX or GemsFX only for named controls required by an accepted
   feature. Prefer ControlsFX for mature desktop patterns and community breadth;
   prefer GemsFX when its more modern or specialized control is a better fit.
5. Do not adopt JFoenix or deepen MaterialFX usage. Monitor MaterialFX's rewrite
   for a stable release, but do not base a current implementation on alpha or
   early-access artifacts.

This layered strategy avoids replacing one all-in-one dependency with another:

`JavaFX controls -> AtlantaFX theme (optional) -> ControlsFX/GemsFX controls (as needed)`

## Accepted Outcome

The application adopted AtlantaFX 2.1.0 on 2026-07-17 as the shared theme layer
for an accepted visual refresh. MaterialFX and its only utility call were
removed. Standard JavaFX controls remain the primary control boundary, while
AtlantaFX supplies `PrimerLight`, `PrimerDark`, and the toggle switches used
for theme and AI selection.

Both games start in `PrimerLight`, switch themes without rebuilding their
scenes, and share one theme-aware application stylesheet. The build excludes
AtlantaFX's transitive OpenJFX dependency so the JavaFX 21.0.8 runtime baseline
remains explicit. ControlsFX and GemsFX were not added because no accepted
feature required their controls.

## Sources

- [MaterialFX repository and documentation](https://github.com/palexdev/MaterialFX)
- [MaterialFX versions in Maven Central](https://central.sonatype.com/artifact/io.github.palexdev/materialfx/versions)
- [AtlantaFX repository, scope, requirements, and release](https://github.com/mkpaz/atlantafx)
- [AtlantaFX 2.1.0 in Maven Central](https://central.sonatype.com/artifact/io.github.mkpaz/atlantafx-base/2.1.0)
- [ControlsFX repository and documentation](https://github.com/controlsfx/controlsfx)
- [ControlsFX versions in Maven Central](https://central.sonatype.com/artifact/org.controlsfx/controlsfx/versions)
- [GemsFX repository, requirements, controls, and releases](https://github.com/dlsc-software-consulting-gmbh/GemsFX)
- [GemsFX project site](https://gemsfx.dlsc.com/)
- [JFoenix repository and compatibility documentation](https://github.com/sshahine/JFoenix)
