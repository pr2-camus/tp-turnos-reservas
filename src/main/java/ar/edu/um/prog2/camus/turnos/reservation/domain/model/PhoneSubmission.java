package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import java.time.Instant;

public record PhoneSubmission(
    String eventId,
    String reservationProcessId,
    String holdId,
    String requestEventId,
    String phoneNumber,
    Instant occurredAt
) {}
