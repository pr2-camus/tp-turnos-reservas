package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import java.util.Optional;

public interface GetReservationUseCase {
    /** Devuelve la reserva solo si es del usuario autenticado. */
    Optional<ReservationProcess> getReservation(String reservationProcessId);
}
