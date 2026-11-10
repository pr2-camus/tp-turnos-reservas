package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import java.util.List;
import java.util.Optional;

public interface ReservationProcessRepository {
    ReservationProcess save(ReservationProcess process);

    Optional<ReservationProcess> findByReservationProcessId(String reservationProcessId);

    List<ReservationProcess> findAllByUserId(Long userId);
}
