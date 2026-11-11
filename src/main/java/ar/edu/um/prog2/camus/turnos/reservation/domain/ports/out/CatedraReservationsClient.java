package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import java.util.List;

public interface CatedraReservationsClient {
    /** Reservas de la cuenta tecnica segun la catedra. Falla con UNAVAILABLE si no responde. */
    List<CatedraReservation> listReservations();
}
