package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEvent;

public interface ProcessReservationEventUseCase {
    /** Aplica un evento de la catedra al proceso de reserva. Un evento repetido no cambia nada. */
    void process(ReservationEvent event);
}
