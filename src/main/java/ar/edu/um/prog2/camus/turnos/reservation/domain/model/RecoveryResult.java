package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

/** Resumen de una pasada de recuperacion. */
public record RecoveryResult(int reconciled, int expired, boolean catedraAvailable) {}
