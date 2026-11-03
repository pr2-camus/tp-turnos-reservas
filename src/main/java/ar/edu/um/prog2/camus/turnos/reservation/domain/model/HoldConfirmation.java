package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import java.time.Instant;

public record HoldConfirmation(String reservationProcessId, Instant expiresAt) {}
