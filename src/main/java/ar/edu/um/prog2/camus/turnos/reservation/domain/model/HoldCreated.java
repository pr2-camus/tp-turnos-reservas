package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import java.time.Instant;

public record HoldCreated(String holdId, String reservationProcessId, Instant expiresAt) {}
