# tp-turnos-reservas

## Qué es

Servicio de **turnos** (puerto 8082): consulta la disponibilidad de un profesional y gestiona la reserva de un turno contra la cátedra, con recuperación ante fallos.

## Cómo ejecutarlo

```bash
cp .env.example .env      # completar valores (no se versiona)
docker compose up -d      # turnos, catálogo y sus bases
```

Requiere VPN/ZeroTier para llegar a la cátedra (API, Kafka). Variables: ver `.env.example`.

## Documentación

1. [Arquitectura](docs/arquitectura.md)
2. [Contratos entre servicios](docs/contratos.md)
3. [Modelo de datos](docs/modelo-de-datos.md)
4. [Idempotencia y recuperación](docs/idempotencia-y-recuperacion.md)
5. [Seguridad](docs/seguridad.md)
6. [Pruebas](docs/pruebas.md)

<!-- calidad:inicio -->

![Calidad](https://img.shields.io/badge/Calidad-58%2F100-yellow) ![Aprobar](https://img.shields.io/badge/Aprobar-no-red) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Build](https://img.shields.io/badge/Build-OK-green) ![Duplicación](https://img.shields.io/badge/Duplicaci%C3%B3n-9.2%25-orange) ![%POO](https://img.shields.io/badge/%25POO-75%25-yellow) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![D1](https://img.shields.io/badge/D1-2-red) ![D2](https://img.shields.io/badge/D2-0-red) ![D4](https://img.shields.io/badge/D4-90-green) ![D5](https://img.shields.io/badge/D5-100-green) ![D6](https://img.shields.io/badge/D6-91-green) ![D7](https://img.shields.io/badge/D7-100-green)

**Calidad (heurístico, 7 dimensiones):** índice **58/100** · build **OK** · aprobar **no (4/11)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->
