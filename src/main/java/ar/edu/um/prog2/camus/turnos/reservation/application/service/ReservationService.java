package ar.edu.um.prog2.camus.turnos.reservation.application.service;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.GetReservationUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.StartReservationUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.SubmitPhoneUseCase;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final StartReservationUseCase startReservationUseCase;
    private final SubmitPhoneUseCase submitPhoneUseCase;
    private final GetReservationUseCase getReservationUseCase;

    public ReservationService(
        StartReservationUseCase startReservationUseCase,
        SubmitPhoneUseCase submitPhoneUseCase,
        GetReservationUseCase getReservationUseCase
    ) {
        this.startReservationUseCase = startReservationUseCase;
        this.submitPhoneUseCase = submitPhoneUseCase;
        this.getReservationUseCase = getReservationUseCase;
    }

    public ReservationProcess startReservation(long professionalId, LocalDate date, LocalTime startTime) {
        return startReservationUseCase.startReservation(professionalId, date, startTime);
    }

    public ReservationProcess submitPhone(String reservationProcessId, String phoneNumber) {
        return submitPhoneUseCase.submitPhone(reservationProcessId, phoneNumber);
    }

    public ReservationProcess getReservation(String reservationProcessId) {
        return getReservationUseCase
            .getReservation(reservationProcessId)
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", ErrorKind.NOT_FOUND, "No existe la reserva."));
    }
}
