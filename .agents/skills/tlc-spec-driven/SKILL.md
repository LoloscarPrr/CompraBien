# tlc-spec-driven

## Purpose
Use specification-driven development for every CompraBien product change. The spec is the source of truth; implementation is complete only when acceptance criteria are verified.

## Trigger
Apply this skill whenever work changes CompraBien behavior, UX, data, architecture, integrations, build/release behavior, or fixes a bug.

## TLC workflow
TLC = Initialize → Think → Lock → Code → Verify.

### 0. Initialize
Before the first code change in a new working session:
1. Read root `AGENTS.md` and this skill.
2. Confirm repository, branch/base and latest relevant state.
3. Read current product/architecture docs and active specs.
4. Inspect app version/build metadata and affected modules.
5. Check baseline CI/build status when observable.
6. Record constraints and uncertainties instead of guessing.
7. Update `docs/specs/_initialization.md`.

### 1. Think
Identify the user problem, affected flows, regressions and non-goals before editing code.

### 2. Lock
Create/update a spec under `docs/specs/` before implementation. It must contain status, problem, desired behavior, scope/non-goals, acceptance criteria, data/persistence impact, UI/UX impact, edge cases and verification plan.

### 3. Code
Implement the smallest coherent change satisfying the locked spec. Preserve behavior outside scope and prefer reusable shared fixes.

### 4. Verify
Compare the implementation against every acceptance criterion. Mark each `PASS` or `BLOCKED` with evidence. A green build alone is not proof of correct UX.

## Definition of Done
A change is Done only when initialization is valid, the spec is locked, acceptance criteria are PASS/BLOCKED with evidence, persistence/regressions are checked, and user-visible documentation is updated when appropriate.

## Lifecycle
`DRAFT → LOCKED → IMPLEMENTING → VERIFYING → DONE`

## CompraBien conventions
- Spec IDs: `CB-<AREA>-NNN`, e.g. `CB-CORE-001`, `CB-UI-002`, `CB-OBS-003`.
- Specs live in `docs/specs/`.
- One spec describes one user-visible behavior or tightly coupled technical change.
- Bug fixes require a regression criterion.
- Shared UI changes must explicitly check compact, normal and large layouts.
- If Firebase or another external service is not configured, mark that verification `BLOCKED`; do not fake connectivity.

## Required PR footer
`Spec: CB-AREA-NNN`
