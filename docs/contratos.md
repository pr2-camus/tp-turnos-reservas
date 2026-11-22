# Contratos entre servicios (turnos)

Errores: `application/problem+json` con `status`, `title`, `detail`, `code` y `path`.

## API que expone turnos (JWT obligatorio)

| Método y ruta | Cuerpo | Respuesta |
|---|---|---|
| `POST /api/authenticate` | `{username, password, rememberMe}` | `{id_token}` (público) |
| `GET /api/availability?professionalId&date` | – | lista de `{date, startTime, endTime}` |
| `POST /api/reservations` | `{professionalId, date, startTime}` | 201, `ReservationProcessResponse` |
| `POST /api/reservations/{id}/phone` | `{phoneNumber}` (≤ 30) | `ReservationProcessResponse` |
| `GET /api/reservations` / `GET /api/reservations/{id}` | – | mis reservas / una reserva |
| `POST /api/reservations/{id}/cancel` | `{reason?}` (≤ 500) | `ReservationProcessResponse` |

```json
{ "reservationProcessId": "…", "status": "WAITING_FOR_PHONE", "expiresAt": "2026-11-30T15:10:00Z",
  "professionalId": 1, "date": "2026-12-01", "startTime": "09:30" }
```

Una reserva solo es visible para su dueña/o; si no es suya responde `404 RESERVATION_NOT_FOUND`.

## Servicios que consume

| Destino | Llamada | Credencial | Reintento |
|---|---|---|---|
| Catálogo | `GET /api/professionals/{id}/agenda` | JWT del usuario (se reenvía) | no |
| Cátedra | `GET /api/appointment-occupancies` | JWT técnico | no (lectura) |
| Cátedra | `POST /api/appointment-holds`, `…/{holdId}/confirm`, `POST /api/appointments/{id}/cancel` | JWT técnico | **nunca** (tienen efectos) |
| Cátedra | `GET /api/appointments?page&size` (recuperación) | JWT técnico | sí, 3 intentos con espera creciente |

## Kafka

| Topic | Sentido | Contenido |
|---|---|---|
| `KAFKA_ACTIONS_TOPIC` | consume | eventos del proceso: `INFORMATION_REQUESTED`, `CONFIRMED`, `INFORMATION_REJECTED`, `EXPIRED`, `INVALID`, `CANCELLED` |
| `KAFKA_PHONE_TOPIC` | publica | `AdditionalInformationSubmitted` con `eventId`, `groupId`, `reservationProcessId`, `holdId`, `requestEventId`, `information.phoneNumber`, `occurredAt`, `schemaVersion: 1`. La key del mensaje es el `reservationProcessId` |

## Traducción de errores

| `ErrorKind` | HTTP | Ejemplos de `code` |
|---|---|---|
| `VALIDATION` | 400 | `PATIENT_NAME_REQUIRED`, `VALIDATION_ERROR` |
| `NOT_FOUND` | 404 | `RESERVATION_NOT_FOUND`, `PROFESSIONAL_NOT_FOUND` |
| `CONFLICT` | 409 | `SLOT_NOT_AVAILABLE`, `PHONE_NOT_EXPECTED`, `RESERVATION_EXPIRED`, `CANCEL_NOT_ALLOWED`, `INVALID_STATE_TRANSITION` |
| `UNAVAILABLE` | 503 | `CATALOG_UNAVAILABLE`, `CATEDRA_UNAVAILABLE`, `KAFKA_UNAVAILABLE` |
