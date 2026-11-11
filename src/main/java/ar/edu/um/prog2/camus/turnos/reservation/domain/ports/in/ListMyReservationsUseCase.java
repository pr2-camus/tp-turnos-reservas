package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import java.util.List;

public interface ListMyReservationsUseCase {
    /** Reservas del usuario autenticado. */
    List<ReservationProcess> listMyReservations();
}
