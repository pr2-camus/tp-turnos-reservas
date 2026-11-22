# Cómo correr las pruebas (turnos)

Requisitos: Java 21 y Docker (Testcontainers con PostgreSQL).

```bash
./mvnw verify                          # formato, unitarios, integración y cobertura
./mvnw -Dtest=RecoverPendingReservationsUseCaseImplTest test
```

| Tipo | Qué cubre |
|---|---|
| Casos de uso (`*UseCaseImplTest`) | inicio, teléfono, evento, recuperación, cancelación con puertos simulados |
| Dominio (`ReservationProcessTest`) | reglas de transición |
| Adaptadores (`Catedra*RestAdapterTest`, `CatalogRestAdapterTest`, `KafkaPhoneSubmission*Test`, `ReservationActionsListenerTest`) | traducción de errores, payload Kafka, listener |
| `RetryTest` | reintentos solo ante fallos transitorios |
| Integración (`*IT`) | seguridad JWT, `ReservationControllerSecurityIT`, cuentas y usuarios (JHipster) |
| `TechnicalStructureTest` | reglas de arquitectura (ArchUnit) |

Los tests no dependen de variables de entorno: `src/test/resources/config/application.yml` tiene valores fijos y los clientes apuntan a puertos locales ficticios. Cobertura con JaCoCo (`target/site/jacoco`); SonarQube Cloud exige ≥ 80 % en código nuevo.
