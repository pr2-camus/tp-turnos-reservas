package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

/** Reserva tal como la ve la catedra: solo lo necesario para reconciliar. */
public record CatedraReservation(String reservationProcessId, CatedraReservationStatus status) {}
