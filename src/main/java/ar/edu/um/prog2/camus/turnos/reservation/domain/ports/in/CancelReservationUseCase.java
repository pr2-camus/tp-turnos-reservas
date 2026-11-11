package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;

public interface CancelReservationUseCase {
    /** Cancela una reserva confirmada del usuario autenticado. Repetir la cancelacion no falla. */
    ReservationProcess cancelReservation(String reservationProcessId, String reason);
}
