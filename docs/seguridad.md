# Decisiones de seguridad (turnos)

- **Autenticación**: usuario y contraseña en `POST /api/authenticate`; devuelve un JWT HS512 (24 h; 30 días con *remember me*). Los servicios comparten `JWT_SECRET`.
- **Contraseñas**: hash **Argon2id** (columna de 255 caracteres), no BCrypt.
- **Autorización**: `/api/**` autenticado; `/api/admin/**` y `/management/**` solo `ROLE_ADMIN`; públicos: autenticación, registro, activación, reset de contraseña, `health`, `info`, `prometheus`.
- **Pertenencia**: las reservas se filtran por usuario; pedir una ajena da 404 (no revela que existe).
- **Dos credenciales hacia afuera**: al catálogo se reenvía el JWT del usuario; a la cátedra se usa un JWT técnico solo del lado servidor (`CATEDRA_JWT`).
- **Secretos**: todo por variables de entorno; `.env` ignorado por git, solo `.env.example` sin valores.
- **Datos personales**: el teléfono no se guarda en la base; se envía a Kafka y en logs aparece tapado (`***`).
- **Sin sesión**: API *stateless*, cookies `HttpOnly`.
- **Entradas validadas**: `@NotNull`, `@NotBlank`, `@Size` en los DTO.
- **Pendiente de revisar**: los usuarios de ejemplo de JHipster (`admin`, `user`) vienen en `user.csv` para desarrollo; deben cambiarse o eliminarse fuera de entornos de prueba.
