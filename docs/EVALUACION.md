<!-- evaluacion:inicio -->
# Evaluación — pr2-camus/tp-turnos-reservas

> Documento **generado automáticamente** por la cátedra de Programación II (UM): RepoSense + análisis de calidad de **7 dimensiones**. **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas) + build: es una criba para orientar la revisión, **no una nota**. La **documentación** del repositorio no se evalúa.
> Generado: 2026-11-12.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad | **51/100** |
| Tipo de repositorio | `backend` |
| Build (`mvnw verify` / `gradlew check`) | OK |
| Código (LOC) | 9,569 |
| Archivos de código | 209 |
| Clases | 184 |
| Código dentro de clases (%POO) | 79% |
| Funciones fuera de clases | 0 |
| Duplicación (puerta) | 10.3% |
| Duplicación lógica | 11.0% |
| Duplicación de boilerplate | 3.1% |
| Cobertura | 56% |
| Tope duro aplicado a D1 | `POO_BAJO` → D1 ≤ 60 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

## 2. Semáforo de aprobación

Criterios de la cátedra (`CRITERIOS-CALIDAD.md`): **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **no cumple** — 4/11 |
| Destacar (`exigente`) | **no cumple** — 3/11 |

**Para aprobar, falta cumplir:** SOLID; Hexagonal; Duplicación; Cobertura

**Para destacar, falta cumplir:** SOLID; POO; Hexagonal; Duplicación; Cobertura

## 3. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| zoecamus | 1 | 0 | 0 | 0 | 8 | 0% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis (que mira el estado de `main`)._

## 4. Dimensiones evaluadas

Cada dimensión indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el puntaje queda por debajo de la referencia, se agregan preguntas para **investigar la causa** (no hay recetas).

| Dimensión | Peso | Puntaje |
|:--|--:|--:|
| **D1** Diseño OO, SOLID y patrones (20%) | 20% | 1 |
| **D2** Arquitectura hexagonal — backend (15%) | 15% | 0 |
| **D3** Frontend KMP: MVVM y buenas prácticas (10%) | 10% | N/A |
| **D4** Integración y contrato (10%) | 10% | 90 |
| **D5** Seguridad y robustez (10%) | 10% | 100 |
| **D6** Pruebas y cobertura (20%) | 20% | 80 |
| **D7** Proceso y entrega (15%) | 15% | 69 |

### D1. Diseño OO, SOLID y patrones (20%)

- **Qué mide:** Métricas orientadas a objetos (clases, métodos, campos, cohesión, acoplamiento, herencia) y proxies de SOLID, más patrones de diseño.
- **Por qué importa:** El eje de la materia es el buen uso de la POO y su arquitectura: un diseño con clases claras y poco acopladas es más fácil de entender, probar y cambiar.
- **Tu resultado:** **1/100**
- **Para investigar la causa:**
    - ¿Se pensó el modelo de clases antes de codificar o se agrupó después?
    - ¿Qué clases concentran muchas responsabilidades a la vez?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### D2. Arquitectura hexagonal — backend (15%)

- **Qué mide:** Separación en capas (dominio, puertos de entrada/salida, adaptadores) y dirección de las dependencias del backend.
- **Por qué importa:** Aislar el dominio de la infraestructura permite cambiar la base de datos, el framework o el transporte sin tocar las reglas de negocio.
- **Tu resultado:** **0/100**
- **Para investigar la causa:**
    - ¿El dominio importa framework o detalles de persistencia?
    - ¿Dónde vive la lógica de negocio frente a los controladores?
    - ¿Se definieron puertos para los servicios que se consumen?

### D4. Integración y contrato (10%)

- **Qué mide:** Cumplimiento del contrato de integración de la cátedra (formato de errores, paginación, autenticación, endpoints esperados).
- **Por qué importa:** Los servicios deben poder integrarse entre sí y con la app sin sorpresas; el contrato es lo que hacen cumplir las pruebas de integración.
- **Tu resultado:** **90/100**

### D5. Seguridad y robustez (10%)

- **Qué mide:** Credenciales embebidas, consultas SQL por concatenación, y presencia de config de entorno; aislamiento de secretos.
- **Por qué importa:** Un secreto en el repositorio o una consulta construida por concatenación son riesgos concretos, no hipotéticos.
- **Tu resultado:** **100/100**

### D6. Pruebas y cobertura (20%)

- **Qué mide:** Cantidad y resultado de pruebas y cobertura de línea (JaCoCo/Kover, Surefire/Failsafe) cuando hay build.
- **Por qué importa:** Las pruebas son la red de seguridad para refactorizar e integrar; la cobertura acota las zonas sin verificar.
- **Tu resultado:** **80/100**

### D7. Proceso y entrega (15%)

- **Qué mide:** Artefactos verificables de proceso/entrega: `compose`, `.env.example`, CI (`.github/workflows/`), `CODEOWNERS` y wrapper de build (`mvnw`/`gradlew`). **No** puntúa documentación.
- **Por qué importa:** Un proyecto que compila, se levanta y se valida solo es más fácil de revisar y de integrar en equipo.
- **Tu resultado:** **69/100**

## 5. Señales detectadas

El análisis encontró las siguientes señales (archivo, sin número de línea a propósito):

**SRP/responsabilidad** (90)
- clase 'TurnosApp' grande (LOC=80, métodos=10, campos=2) — `src/main/java/ar/edu/um/prog2/camus/turnos/TurnosApp.java`
- método 'TurnosApp.logApplicationStartup' de 35 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/TurnosApp.java`
- clase 'DomainUserDetailsService' con baja cohesión (6%) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsService.java`
- clase 'SecurityUtils' grande (LOC=97, métodos=12, campos=3) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtils.java`
- clase 'SecurityUtils' con baja cohesión (2%) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtils.java`
- clase 'LoggingAspect' grande (LOC=100, métodos=13, campos=1) — `src/main/java/ar/edu/um/prog2/camus/turnos/aop/logging/LoggingAspect.java`
- clase 'ReservationProcessEntity' grande (LOC=85, métodos=20, campos=10) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/persistence/entity/ReservationProcessEntity.java`
- clase 'ReservationProcessEntity' con baja cohesión (5%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/persistence/entity/ReservationProcessEntity.java`
- clase 'KafkaPhoneSubmissionPublisher' con baja cohesión (17%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisher.java`
- clase 'ReservationActionsListener' con baja cohesión (17%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/ReservationActionsListener.java`
- método 'ReservationActionsListener.toType' con complejidad 8 — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/ReservationActionsListener.java`
- clase 'CatedraHoldRestAdapter' grande (LOC=96, métodos=18, campos=2) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/client/CatedraHoldRestAdapter.java`
- clase 'ReservationService' con baja cohesión (7%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/service/ReservationService.java`
- método 'StartReservationUseCaseImpl.startReservation' de 34 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImpl.java`
- método 'StartReservationUseCaseImpl.startReservation' con complejidad 6 — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImpl.java`
- método 'ProcessReservationEventUseCaseImpl.process' con complejidad 9 — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImpl.java`
- clase 'SubmitPhoneUseCaseImpl' con baja cohesión (0%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImpl.java`
- método 'SubmitPhoneUseCaseImpl.submitPhone' de 44 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImpl.java`
- clase 'ReservationProcess' grande (LOC=102, métodos=15, campos=10) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationProcess.java`
- clase 'ReservationProcess' con baja cohesión (7%) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationProcess.java`
- método 'ReservationStatus.allowedNext' con complejidad 6 — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationStatus.java`
- método 'CatalogRestAdapter.ProfessionalDto' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatalogRestAdapter.java`
- método 'ProfessionalDto.ProfessionalDto' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatalogRestAdapter.java`
- método 'GetAvailabilityUseCaseImpl.getAvailability' de 31 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/application/usecases/GetAvailabilityUseCaseImpl.java`
- método 'Professional.Professional' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/domain/model/Professional.java`
- clase 'UserService' grande (LOC=235, métodos=33, campos=4) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- método 'UserService.registerUser' de 34 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- método 'UserService.createUser' de 33 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- método 'UserService.updateUser' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- clase 'MailService' con baja cohesión (48%) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/MailService.java`
- método 'MailService.sendEmail' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/service/MailService.java`
- método 'MailService.sendEmailSync' con 5 parámetros — `src/main/java/ar/edu/um/prog2/camus/turnos/service/MailService.java`
- clase 'UserMapper' grande (LOC=112, métodos=13, campos=0) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/mapper/UserMapper.java`
- clase 'PasswordChangeDTO' con baja cohesión (40%) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/dto/PasswordChangeDTO.java`
- clase 'AdminUserDTO' grande (LOC=140, métodos=29, campos=14) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/dto/AdminUserDTO.java`
- clase 'AdminUserDTO' con baja cohesión (16%) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/dto/AdminUserDTO.java`
- clase 'UserDTO' con baja cohesión (31%) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/dto/UserDTO.java`
- clase 'SecurityMetersService' grande (LOC=35, métodos=7, campos=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/management/SecurityMetersService.java`
- clase 'SecurityMetersService' con baja cohesión (33%) — `src/main/java/ar/edu/um/prog2/camus/turnos/management/SecurityMetersService.java`
- método 'SecurityConfiguration.filterChain' de 30 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/config/SecurityConfiguration.java`
- clase 'Authority' grande (LOC=64, métodos=10, campos=3) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- clase 'Authority' con baja cohesión (24%) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- clase 'AbstractAuditingEntity' con baja cohesión (14%) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- clase 'User' grande (LOC=165, métodos=29, campos=14) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- clase 'User' con baja cohesión (8%) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- clase 'AuthenticateController' con baja cohesión (14%) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateController.java`
- clase 'AccountResource' grande (LOC=156, métodos=10, campos=5) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResource.java`
- clase 'AccountResource' con baja cohesión (49%) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResource.java`
- clase 'ErrorConstants' grande (LOC=11, métodos=1, campos=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ErrorConstants.java`
- clase 'ExceptionTranslator' grande (LOC=228, métodos=26, campos=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- clase 'ExceptionTranslator' con baja cohesión (1%) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- método 'ExceptionTranslator.customizeProblem' de 30 líneas — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- método 'ExceptionTranslator.customizeProblem' con complejidad 10 — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- método 'ExceptionTranslator.getCustomizedErrorDetails' con complejidad 6 — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- método 'ExceptionTranslator.getMappedStatus' con complejidad 6 — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- método 'ExceptionTranslator.resolveCode' con complejidad 13 — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- clase 'ManagedUserVM' con baja cohesión (17%) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/vm/ManagedUserVM.java`
- clase 'LoginVM' con baja cohesión (33%) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/vm/LoginVM.java`
- clase 'DomainUserDetailsServiceIT' grande (LOC=96, métodos=11, campos=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsServiceIT.java`
- clase 'SecurityUtilsUnitTest' grande (LOC=102, métodos=11, campos=0) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtilsUnitTest.java`
- clase 'JwtAuthenticationTestUtils' grande (LOC=57, métodos=10, campos=1) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/jwt/JwtAuthenticationTestUtils.java`
- clase 'TokenAuthenticationSecurityMetersIT' con baja cohesión (48%) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/jwt/TokenAuthenticationSecurityMetersIT.java`
- método 'LoggingAspectTest.setup' con complejidad 6 — `src/test/java/ar/edu/um/prog2/camus/turnos/aop/logging/LoggingAspectTest.java`
- clase 'SubmitPhoneUseCaseImplTest' grande (LOC=84, métodos=10, campos=3) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImplTest.java`
- clase 'StartReservationUseCaseImplTest' grande (LOC=66, métodos=7, campos=8) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImplTest.java`
- clase 'ProcessReservationEventUseCaseImplTest' grande (LOC=108, métodos=15, campos=3) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImplTest.java`
- clase 'ProcessReservationEventUseCaseImplTest' con baja cohesión (10%) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImplTest.java`
- método 'ProcessReservationEventUseCaseImplTest.processIn' con complejidad 7 — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImplTest.java`
- clase 'DateTimeWrapper' grande (LOC=96, métodos=19, campos=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/repository/timezone/DateTimeWrapper.java`
- clase 'DateTimeWrapper' con baja cohesión (11%) — `src/test/java/ar/edu/um/prog2/camus/turnos/repository/timezone/DateTimeWrapper.java`
- clase 'MailServiceIT' grande (LOC=177, métodos=12, campos=7) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/MailServiceIT.java`
- clase 'UserServiceIT' grande (LOC=135, métodos=9, campos=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/UserServiceIT.java`
- clase 'UserMapperTest' grande (LOC=129, métodos=11, campos=5) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/mapper/UserMapperTest.java`
- clase 'HibernateTimeZoneIT' grande (LOC=114, métodos=10, campos=8) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/timezone/HibernateTimeZoneIT.java`
- clase 'AuthorityResourceIT' grande (LOC=142, métodos=17, campos=8) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthorityResourceIT.java`
- clase 'AuthorityResourceIT' con baja cohesión (29%) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthorityResourceIT.java`
- clase 'UserResourceIT' grande (LOC=385, métodos=20, campos=21) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- método 'UserResourceIT.updateUser' de 38 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- método 'UserResourceIT.updateUserLogin' de 39 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- método 'UserResourceIT.updateUserExistingEmail' de 33 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- método 'UserResourceIT.updateUserExistingLogin' de 33 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- clase 'AccountResourceIT' grande (LOC=636, métodos=33, campos=8) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- método 'AccountResourceIT.testRegisterDuplicateLogin' de 36 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- método 'AccountResourceIT.testRegisterDuplicateEmail' de 49 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- método 'AccountResourceIT.testSaveAccount' de 30 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- método 'AccountResourceIT.testSaveExistingEmail' de 30 líneas — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- clase 'TestUtil' grande (LOC=145, métodos=15, campos=2) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/TestUtil.java`
- método 'TestUtil.asDecimal' con complejidad 7 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/TestUtil.java`
- método 'NumberMatcher.asDecimal' con complejidad 7 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/TestUtil.java`
- clase 'ExceptionTranslatorTestController' grande (LOC=47, métodos=10, campos=1) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslatorTestController.java`

**DIP/concretos** (104)
- clase 'TurnosApp' muy acoplada (CBO=24) — `src/main/java/ar/edu/um/prog2/camus/turnos/TurnosApp.java`
- clase 'DomainUserDetailsService' muy acoplada (CBO=16) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsService.java`
- clase 'DomainUserDetailsService' instancia 5 concretos (CBO=16) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsService.java`
- clase 'SecurityUtils' muy acoplada (CBO=17) — `src/main/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtils.java`
- clase 'LoggingAspect' muy acoplada (CBO=24) — `src/main/java/ar/edu/um/prog2/camus/turnos/aop/logging/LoggingAspect.java`
- clase 'ReservationProcessEntity' muy acoplada (CBO=9) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/persistence/entity/ReservationProcessEntity.java`
- clase 'KafkaPhoneSubmissionPublisher' muy acoplada (CBO=22) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisher.java`
- clase 'ReservationActionsListener' muy acoplada (CBO=24) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/ReservationActionsListener.java`
- clase 'CatedraHoldRestAdapter' muy acoplada (CBO=30) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/client/CatedraHoldRestAdapter.java`
- clase 'CatedraHoldRestAdapter' instancia 8 concretos (CBO=30) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/client/CatedraHoldRestAdapter.java`
- clase 'JhipsterCurrentUserAdapter' muy acoplada (CBO=10) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/user/JhipsterCurrentUserAdapter.java`
- clase 'ReservationController' muy acoplada (CBO=11) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/web/controller/ReservationController.java`
- clase 'ReservationService' muy acoplada (CBO=13) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/service/ReservationService.java`
- clase 'CancelReservationUseCaseImpl' muy acoplada (CBO=21) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/CancelReservationUseCaseImpl.java`
- clase 'StartReservationUseCaseImpl' muy acoplada (CBO=28) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImpl.java`
- clase 'StartReservationUseCaseImpl' instancia 3 concretos (CBO=28) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImpl.java`
- clase 'ProcessReservationEventUseCaseImpl' muy acoplada (CBO=20) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImpl.java`
- clase 'SubmitPhoneUseCaseImpl' muy acoplada (CBO=26) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImpl.java`
- clase 'SubmitPhoneUseCaseImpl' instancia 5 concretos (CBO=26) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImpl.java`
- clase 'HoldClient' muy acoplada (CBO=9) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/ports/out/HoldClient.java`
- clase 'ReservationProcess' muy acoplada (CBO=13) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationProcess.java`
- clase 'ReservationStatus' muy acoplada (CBO=11) — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationStatus.java`
- clase 'CatedraOccupancyRestAdapter' muy acoplada (CBO=18) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatedraOccupancyRestAdapter.java`
- clase 'CatedraOccupancyRestAdapter' instancia 3 concretos (CBO=18) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatedraOccupancyRestAdapter.java`
- clase 'CatalogRestAdapter' muy acoplada (CBO=24) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatalogRestAdapter.java`
- clase 'CatalogRestAdapter' instancia 5 concretos (CBO=24) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatalogRestAdapter.java`
- clase 'AvailabilityController' muy acoplada (CBO=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/web/controller/AvailabilityController.java`
- clase 'AvailabilityService' muy acoplada (CBO=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/application/service/AvailabilityService.java`
- clase 'GetAvailabilityUseCaseImpl' muy acoplada (CBO=9) — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/application/usecases/GetAvailabilityUseCaseImpl.java`
- clase 'UserService' muy acoplada (CBO=36) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- clase 'UserService' instancia 5 concretos (CBO=36) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/UserService.java`
- clase 'MailService' muy acoplada (CBO=21) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/MailService.java`
- clase 'AdminUserDTO' muy acoplada (CBO=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/service/dto/AdminUserDTO.java`
- clase 'WebConfigurer' muy acoplada (CBO=13) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/WebConfigurer.java`
- clase 'LiquibaseConfiguration' muy acoplada (CBO=18) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/LiquibaseConfiguration.java`
- clase 'CRLFLogConverter' muy acoplada (CBO=16) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/CRLFLogConverter.java`
- clase 'SecurityJwtConfiguration' muy acoplada (CBO=19) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/SecurityJwtConfiguration.java`
- clase 'SecurityConfiguration' muy acoplada (CBO=14) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/SecurityConfiguration.java`
- clase 'AsyncConfiguration' muy acoplada (CBO=12) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/AsyncConfiguration.java`
- clase 'AsyncConfiguration' instancia 3 concretos (CBO=12) — `src/main/java/ar/edu/um/prog2/camus/turnos/config/AsyncConfiguration.java`
- clase 'RestClientsConfig' muy acoplada (CBO=10) — `src/main/java/ar/edu/um/prog2/camus/turnos/shared/infrastructure/config/RestClientsConfig.java`
- clase 'User' muy acoplada (CBO=12) — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- clase 'AuthorityResource' muy acoplada (CBO=26) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthorityResource.java`
- clase 'AuthenticateController' muy acoplada (CBO=33) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateController.java`
- clase 'AuthenticateController' instancia 3 concretos (CBO=33) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateController.java`
- clase 'PublicUserResource' muy acoplada (CBO=18) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/PublicUserResource.java`
- clase 'AccountResource' muy acoplada (CBO=39) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResource.java`
- clase 'AccountResource' instancia 9 concretos (CBO=39) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResource.java`
- clase 'UserResource' muy acoplada (CBO=46) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java`
- clase 'UserResource' instancia 6 concretos (CBO=46) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java`
- clase 'BadRequestAlertException' muy acoplada (CBO=8) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/BadRequestAlertException.java`
- clase 'ExceptionTranslator' muy acoplada (CBO=79) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- clase 'ExceptionTranslator' instancia 4 concretos (CBO=79) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`
- clase 'TechnicalStructureTest' muy acoplada (CBO=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/TechnicalStructureTest.java`
- clase 'DomainUserDetailsServiceIT' muy acoplada (CBO=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsServiceIT.java`
- clase 'DomainUserDetailsServiceIT' instancia 3 concretos (CBO=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/DomainUserDetailsServiceIT.java`
- clase 'SecurityUtilsUnitTest' muy acoplada (CBO=14) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtilsUnitTest.java`
- clase 'SecurityUtilsUnitTest' instancia 15 concretos (CBO=14) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtilsUnitTest.java`
- clase 'JwtAuthenticationTestUtils' muy acoplada (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/jwt/JwtAuthenticationTestUtils.java`
- clase 'JwtAuthenticationTestUtils' instancia 4 concretos (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/security/jwt/JwtAuthenticationTestUtils.java`
- clase 'LoggingAspectTest' muy acoplada (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/aop/logging/LoggingAspectTest.java`
- clase 'LoggingAspectTest' instancia 5 concretos (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/aop/logging/LoggingAspectTest.java`
- clase 'ReservationActionsListenerTest' muy acoplada (CBO=10) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/ReservationActionsListenerTest.java`
- clase 'ReservationActionsListenerTest' instancia 3 concretos (CBO=10) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/ReservationActionsListenerTest.java`
- clase 'KafkaPhoneSubmissionPublisherTest' muy acoplada (CBO=11) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisherTest.java`
- clase 'KafkaPhoneSubmissionPublisherTest' instancia 5 concretos (CBO=11) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisherTest.java`
- clase 'CatedraHoldRestAdapterTest' muy acoplada (CBO=29) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/client/CatedraHoldRestAdapterTest.java`
- clase 'CancelReservationUseCaseImplTest' muy acoplada (CBO=22) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/CancelReservationUseCaseImplTest.java`
- clase 'CancelReservationUseCaseImplTest' instancia 3 concretos (CBO=22) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/CancelReservationUseCaseImplTest.java`
- clase 'SubmitPhoneUseCaseImplTest' muy acoplada (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImplTest.java`
- clase 'SubmitPhoneUseCaseImplTest' instancia 3 concretos (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/SubmitPhoneUseCaseImplTest.java`
- clase 'StartReservationUseCaseImplTest' muy acoplada (CBO=28) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImplTest.java`
- clase 'StartReservationUseCaseImplTest' instancia 7 concretos (CBO=28) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/StartReservationUseCaseImplTest.java`
- clase 'GetReservationUseCaseImplTest' muy acoplada (CBO=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/GetReservationUseCaseImplTest.java`
- clase 'ProcessReservationEventUseCaseImplTest' muy acoplada (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImplTest.java`
- clase 'ProcessReservationEventUseCaseImplTest' instancia 3 concretos (CBO=20) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ProcessReservationEventUseCaseImplTest.java`
- clase 'ListMyReservationsUseCaseImplTest' muy acoplada (CBO=14) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ListMyReservationsUseCaseImplTest.java`
- clase 'ListMyReservationsUseCaseImplTest' instancia 3 concretos (CBO=14) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/application/usecases/ListMyReservationsUseCaseImplTest.java`
- clase 'ReservationProcessTest' muy acoplada (CBO=14) — `src/test/java/ar/edu/um/prog2/camus/turnos/reservation/domain/model/ReservationProcessTest.java`
- clase 'DateTimeWrapper' muy acoplada (CBO=11) — `src/test/java/ar/edu/um/prog2/camus/turnos/repository/timezone/DateTimeWrapper.java`
- clase 'CatedraOccupancyRestAdapterTest' muy acoplada (CBO=15) — `src/test/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatedraOccupancyRestAdapterTest.java`
- clase 'GetAvailabilityUseCaseImplTest' muy acoplada (CBO=18) — `src/test/java/ar/edu/um/prog2/camus/turnos/availability/application/usecases/GetAvailabilityUseCaseImplTest.java`
- clase 'GetAvailabilityUseCaseImplTest' instancia 7 concretos (CBO=18) — `src/test/java/ar/edu/um/prog2/camus/turnos/availability/application/usecases/GetAvailabilityUseCaseImplTest.java`
- clase 'MailServiceIT' muy acoplada (CBO=26) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/MailServiceIT.java`
- clase 'MailServiceIT' instancia 10 concretos (CBO=26) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/MailServiceIT.java`
- clase 'UserServiceIT' muy acoplada (CBO=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/UserServiceIT.java`
- clase 'UserMapperTest' muy acoplada (CBO=10) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/mapper/UserMapperTest.java`
- clase 'UserMapperTest' instancia 4 concretos (CBO=10) — `src/test/java/ar/edu/um/prog2/camus/turnos/service/mapper/UserMapperTest.java`
- clase 'CRLFLogConverterTest' muy acoplada (CBO=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/CRLFLogConverterTest.java`
- clase 'CRLFLogConverterTest' instancia 9 concretos (CBO=12) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/CRLFLogConverterTest.java`
- clase 'WebConfigurerTest' muy acoplada (CBO=25) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/WebConfigurerTest.java`
- clase 'WebConfigurerTest' instancia 8 concretos (CBO=25) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/WebConfigurerTest.java`
- clase 'HibernateTimeZoneIT' muy acoplada (CBO=24) — `src/test/java/ar/edu/um/prog2/camus/turnos/config/timezone/HibernateTimeZoneIT.java`
- clase 'AuthorityResourceIT' muy acoplada (CBO=19) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthorityResourceIT.java`
- clase 'UserResourceIT' muy acoplada (CBO=27) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- clase 'UserResourceIT' instancia 13 concretos (CBO=27) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResourceIT.java`
- clase 'AccountResourceIT' muy acoplada (CBO=41) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- clase 'AccountResourceIT' instancia 38 concretos (CBO=41) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- clase 'PublicUserResourceIT' muy acoplada (CBO=10) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/PublicUserResourceIT.java`
- clase 'AuthenticateControllerIT' muy acoplada (CBO=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateControllerIT.java`
- clase 'AuthenticateControllerIT' instancia 5 concretos (CBO=9) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateControllerIT.java`
- clase 'TestUtil' muy acoplada (CBO=25) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/TestUtil.java`
- clase 'TestUtil' instancia 6 concretos (CBO=25) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/TestUtil.java`
- clase 'ExceptionTranslatorIT' muy acoplada (CBO=11) — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslatorIT.java`

**OCP/type-switch** (7)
- clase 'SecurityUtils' con 7 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/security/SecurityUtils.java`
- clase 'CatedraHoldRestAdapter' con 1 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/reservation/infrastructure/client/CatedraHoldRestAdapter.java`
- clase 'CatalogRestAdapter' con 1 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/availability/infrastructure/client/CatalogRestAdapter.java`
- clase 'Authority' con 1 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- clase 'User' con 1 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- clase 'AuthenticateController' con 1 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/AuthenticateController.java`
- clase 'ExceptionTranslator' con 29 type-checks — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ExceptionTranslator.java`

**POO** (4)
- clase 'SecurityMetersService' con 4 campos públicos (encapsulación) — `src/main/java/ar/edu/um/prog2/camus/turnos/management/SecurityMetersService.java`
- clase 'ErrorConstants' con 8 campos públicos (encapsulación) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/ErrorConstants.java`
- clase 'AuthorityAsserts' solo con métodos estáticos (¿utilitaria?) — `src/test/java/ar/edu/um/prog2/camus/turnos/domain/AuthorityAsserts.java`
- clase 'AuthorityTestSamples' solo con métodos estáticos (¿utilitaria?) — `src/test/java/ar/edu/um/prog2/camus/turnos/domain/AuthorityTestSamples.java`

**?** (2)
- clase 'LoginAlreadyUsedException' con herencia profunda (DIT=2) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/LoginAlreadyUsedException.java`
- clase 'EmailAlreadyUsedException' con herencia profunda (DIT=2) — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/errors/EmailAlreadyUsedException.java`

**duplicación** (6)
- duplicación lógica: 10.3% · boilerplate: 3.1%
- bloque de 8 líneas repetido 4 veces en src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java:63, src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java:64, src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java:65 — `src/main/java/ar/edu/um/prog2/camus/turnos/web/rest/UserResource.java`
- bloque de 8 líneas repetido 4 veces en src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:413, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:452, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:490 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- bloque de 8 líneas repetido 4 veces en src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:415, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:454, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:492 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- bloque de 8 líneas repetido 4 veces en src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:416, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:455, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:493 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`
- bloque de 8 líneas repetido 4 veces en src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:414, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:453, src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java:491 — `src/test/java/ar/edu/um/prog2/camus/turnos/web/rest/AccountResourceIT.java`

**hexagonal** (20)
- domain importa framework/infra: import com.fasterxml.jackson.annotation.JsonIgnoreProperties; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- domain importa framework/infra: import jakarta.persistence.*; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- domain importa framework/infra: import jakarta.validation.constraints.*; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- domain importa framework/infra: import org.springframework.data.domain.Persistable; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/Authority.java`
- domain importa framework/infra: import com.fasterxml.jackson.annotation.JsonIgnoreProperties; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import jakarta.persistence.Column; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import jakarta.persistence.EntityListeners; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import jakarta.persistence.MappedSuperclass; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import org.springframework.data.annotation.CreatedBy; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import org.springframework.data.annotation.CreatedDate; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import org.springframework.data.annotation.LastModifiedBy; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import org.springframework.data.annotation.LastModifiedDate; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import org.springframework.data.jpa.domain.support.AuditingEntityListener; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/AbstractAuditingEntity.java`
- domain importa framework/infra: import com.fasterxml.jackson.annotation.JsonIgnore; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import jakarta.persistence.*; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import jakarta.validation.constraints.Email; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import jakarta.validation.constraints.NotNull; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import jakarta.validation.constraints.Pattern; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import jakarta.validation.constraints.Size; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`
- domain importa framework/infra: import org.hibernate.annotations.BatchSize; — `src/main/java/ar/edu/um/prog2/camus/turnos/domain/User.java`

## 6. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (15 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice de calidad a lo largo de los commits"
    x-axis ["803b3b3", "a739600", "59b23c6", "149c677", "e0a17fe", "ab3d868", "423af86", "e31790e", "564ba12", "34de0d8", "b4637cc", "a3caa14", "0c95d60", "3aae4df", "70fab8f"]
    y-axis "Índice" 0 --> 100
    line [0, 15, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-09-21 | `803b3b3` | zoecamus | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-09-22 | `a739600` | Ing. Daniel Quinteros | 0 | 15 | 0% | 0.0% | 0 |
| 3 | 2026-10-30 | `59b23c6` | zoecamus | 6,752 | 44 | 82% | 13.4% | 0 |
| 4 | 2026-10-31 | `149c677` | zoecamus | 6,940 | 44 | 81% | 13.2% | 0 |
| 5 | 2026-11-01 | `e0a17fe` | zoecamus | 6,937 | 44 | 81% | 13.2% | 0 |
| 6 | 2026-11-02 | `ab3d868` | zoecamus | 6,947 | 44 | 81% | 13.2% | 0 |
| 7 | 2026-11-02 | `423af86` | zoecamus | 7,225 | 44 | 80% | 12.7% | 0 |
| 8 | 2026-11-03 | `e31790e` | zoecamus | 7,454 | 44 | 80% | 12.3% | 0 |
| 9 | 2026-11-09 | `564ba12` | zoecamus | 7,643 | 44 | 80% | 11.9% | 0 |
| 10 | 2026-11-09 | `34de0d8` | zoecamus | 8,345 | 44 | 80% | 11.6% | 0 |
| 11 | 2026-11-09 | `b4637cc` | zoecamus | 8,731 | 44 | 80% | 11.3% | 0 |
| 12 | 2026-11-11 | `a3caa14` | zoecamus | 9,247 | 44 | 79% | 10.7% | 0 |
| 13 | 2026-11-11 | `0c95d60` | zoecamus | 9,247 | 44 | 79% | 10.7% | 0 |
| 14 | 2026-11-11 | `3aae4df` | zoecamus | 9,255 | 44 | 79% | 10.7% | 0 |
| 15 | 2026-11-11 | `70fab8f` | zoecamus | 9,569 | 44 | 79% | 10.3% | 0 |

---
_Informe generado por la cátedra. Las dimensiones y umbrales corresponden a `calidad.py` (perfil `exigente`); el semáforo, a `CRITERIOS-CALIDAD.md`. La documentación del repositorio no se evalúa. Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
