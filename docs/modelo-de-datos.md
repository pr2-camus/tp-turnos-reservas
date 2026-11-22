# Modelo de datos (turnos)

Liquibase (`config/liquibase/master.xml`): esquema inicial de JHipster, `password_hash_argon2`, `reservation_process`, `start_time_texto`, `processed_event`.

```mermaid
erDiagram
  jhi_user ||--o{ reservation_process : "reserva"
  jhi_user ||--o{ jhi_user_authority : tiene
  jhi_authority ||--o{ jhi_user_authority : otorga
  reservation_process {
    bigint id PK
    bigint user_id FK
    varchar hold_id
    varchar reservation_process_id UK
    varchar request_event_id "null hasta que la cátedra pide el teléfono"
    bigint professional_id
    date appointment_date
    varchar start_time "HH:mm"
    timestamptz expires_at
    varchar status "30"
  }
  processed_event { varchar event_id PK
    timestamptz processed_at }
```

- `reservation_process_id` es único: lo asigna la cátedra y es la clave con la que llegan los eventos.
- `status` se guarda como texto (`ReservationStatus`).
- `start_time` es texto `HH:mm` para evitar problemas de zona horaria con el horario del turno.
- `password_hash` admite 255 caracteres porque Argon2id es más largo que BCrypt.
