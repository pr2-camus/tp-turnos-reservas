package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

public record ReservationEvent(String eventId, ReservationEventType type, String reservationProcessId) {}
