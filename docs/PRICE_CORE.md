# CompraBien Price Core v0.3

Esta fase valida la arquitectura de comparación de precios antes de conectar fuentes externas.

## Estado de los datos

Todos los precios incluidos en esta fase son **datos DEMO ficticios**. No representan precios reales de retailers.

## Modelo inicial

Cada observación de precio contiene:

- `productId`
- `retailer`
- `price`
- `referencePrice`
- `capturedAt`
- `sourceLabel`
- `confidence`

## Reglas iniciales

- El mejor precio se obtiene ordenando observaciones válidas de menor a mayor.
- El porcentaje promocional se calcula solo cuando existe `referencePrice` mayor que el precio actual.
- La interfaz siempre debe mostrar la naturaleza de la fuente y la frescura del dato.
- Cuando se conecten fuentes reales, ningún precio podrá presentarse sin fuente y timestamp.

## Próximas fases

1. Persistencia local de observaciones.
2. Historial temporal.
3. Integración con backend.
4. Fuentes reales y normalización.
5. CompraBien Score.
