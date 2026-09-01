# CB-OBS-001 — Firebase Crashlytics

Status: LOCKED

## Problem
Los cierres inesperados en APK de prueba no tienen telemetría centralizada, haciendo difícil detectar errores fuera de una sesión de prueba manual.

## Desired behavior
CompraBien queda preparada para Firebase Crashlytics. Si existe `app/google-services.json`, Gradle aplica Google Services + Crashlytics y la app registra errores no fatales; si no existe, el build sigue funcionando y Crashlytics queda deshabilitado de forma explícita.

## Scope
- Plugins oficiales Google Services y Firebase Crashlytics.
- Firebase BOM + Crashlytics SDK.
- `CrashReporter` seguro que no rompe builds/configuraciones sin Firebase.
- BuildConfig `CRASHLYTICS_ENABLED` según presencia de configuración.

## Non-goals
No crear el proyecto Firebase ni inventar `google-services.json`.

## Acceptance criteria
- [ ] AC1 el proyecto compila sin `google-services.json`.
- [ ] AC2 al agregar un `google-services.json` válido, los plugins se aplican automáticamente.
- [ ] AC3 existe una API central para registrar errores no fatales.
- [ ] AC4 no se exponen secretos/configuración privada en el repo.
- [ ] AC5 entrega real en consola Firebase queda BLOCKED hasta conectar el proyecto.

## Data/privacy impact
Crashlytics puede recopilar diagnósticos técnicos del dispositivo y fallos cuando esté habilitado. No registrar datos personales, búsquedas ni contenido sensible como claves de Crashlytics.

## UI/UX impact
Ninguno visible en uso normal.

## Verification
CI sin Firebase + verificación real posterior con Firebase conectado y un error de prueba controlado.
