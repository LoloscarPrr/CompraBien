# CB-CORE-001 — Adoptar skill TLC

Status: LOCKED

## Problem
CompraBien no tiene todavía un contrato de trabajo persistente para agentes/cambios de código, lo que facilita cambios sin especificación o verificación consistente.

## Desired behavior
Todo cambio relevante usa `Initialize → Think → Lock → Code → Verify`, con specs `CB-*` como fuente de verdad.

## Scope
- `AGENTS.md` raíz.
- `.agents/skills/tlc-spec-driven/SKILL.md`.
- `docs/specs/` e initialization snapshot.

## Non-goals
No cambia comportamiento de usuario ni datos de la app.

## Acceptance criteria
- [ ] AC1 `AGENTS.md` obliga a usar el skill.
- [ ] AC2 existe `SKILL.md` adaptado a CompraBien.
- [ ] AC3 existe snapshot de inicialización.
- [ ] AC4 nuevas specs usan IDs `CB-*`.

## Data/persistence impact
Ninguno.

## UI/UX impact
Ninguno.

## Verification
Inspección de archivos y CI sin regresiones.
