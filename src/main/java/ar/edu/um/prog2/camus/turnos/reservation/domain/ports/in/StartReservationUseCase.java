package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import java.time.LocalDate;
import java.time.LocalTime;

public interface StartReservationUseCase {
    ReservationProcess startReservation(long professionalId, LocalDate date, LocalTime startTime);
}
