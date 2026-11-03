package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.web.dto;

public record ReservationProcessResponse(
    String reservationProcessId,
    String status,
    String expiresAt,
    Long professionalId,
    String date,
    String startTime
) {}
