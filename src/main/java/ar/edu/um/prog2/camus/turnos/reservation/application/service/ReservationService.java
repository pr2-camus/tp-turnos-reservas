package ar.edu.um.prog2.camus.turnos.reservation.application.service;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.StartReservationUseCase;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final StartReservationUseCase startReservationUseCase;

    public ReservationService(StartReservationUseCase startReservationUseCase) {
        this.startReservationUseCase = startReservationUseCase;
    }

    public ReservationProcess startReservation(long professionalId, LocalDate date, LocalTime startTime) {
        return startReservationUseCase.startReservation(professionalId, date, startTime);
    }
}
