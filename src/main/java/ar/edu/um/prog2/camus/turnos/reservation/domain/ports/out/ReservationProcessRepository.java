package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationProcessRepository {
    ReservationProcess save(ReservationProcess process);

    Optional<ReservationProcess> findByReservationProcessId(String reservationProcessId);

    List<ReservationProcess> findAllByUserId(Long userId);

    /** Procesos cuyo estado esta dentro del conjunto indicado. */
    List<ReservationProcess> findAllByStatusIn(Collection<ReservationStatus> statuses);
}
