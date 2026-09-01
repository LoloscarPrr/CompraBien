# CB-UI-001 — Multiple layout adaptativo

Status: LOCKED

## Problem
La UI fue diseñada principalmente para un teléfono normal. En pantallas muy angostas se aprieta y en tablets desperdicia ancho, dificultando lectura y navegación.

## Desired behavior
Una sola lógica de pantalla se adapta a tres perfiles: compact `<360dp`, normal `360–839dp` y large `>=840dp`.

## Scope
- Márgenes y ancho máximo compartidos.
- Home: acciones apiladas en compact y en fila desde normal.
- Buscar: grid de categorías 1/2/3 columnas según perfil.
- Resultados e historial centrados y con ancho legible en large.
- Mismas funciones/datos en todos los perfiles.

## Non-goals
No crear pantallas duplicadas ni cambiar Price Core, catálogo o persistencia.

## Acceptance criteria
- [ ] AC1 compact evita controles comprimidos y usa márgenes menores.
- [ ] AC2 normal conserva la experiencia actual con mejor jerarquía.
- [ ] AC3 large centra contenido y aprovecha columnas adicionales sin estirar texto indefinidamente.
- [ ] AC4 navegación y lógica son compartidas entre perfiles.

## Data/persistence impact
Ninguno.

## Edge cases
Orientación horizontal, teléfonos <360dp y tablets >=840dp.

## Verification
Compilación CI + revisión en emuladores/dispositivos de cada clase cuando estén disponibles.
