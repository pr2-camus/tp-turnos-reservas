package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;

public interface SubmitPhoneUseCase {
    ReservationProcess submitPhone(String reservationProcessId, String phoneNumber);
}
