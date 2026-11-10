package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;

public interface CurrentUserProvider {
    /** Usuario autenticado en el pedido actual. */
    ReservationUser getCurrentUser();
}
