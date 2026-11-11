package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.CancelReservationUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import org.springframework.stereotype.Component;

@Component
public class CancelReservationUseCaseImpl implements CancelReservationUseCase {

    private final ReservationProcessRepository repository;
    private final CurrentUserProvider currentUserProvider;
    private final HoldClient holdClient;

    public CancelReservationUseCaseImpl(
        ReservationProcessRepository repository,
        CurrentUserProvider currentUserProvider,
        HoldClient holdClient
    ) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
        this.holdClient = holdClient;
    }

    @Override
    public ReservationProcess cancelReservation(String reservationProcessId, String reason) {
        ReservationUser user = currentUserProvider.getCurrentUser();
        ReservationProcess process = repository
            .findByReservationProcessId(reservationProcessId)
            .filter(p -> p.isOwnedBy(user.id()))
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", ErrorKind.NOT_FOUND, "No existe la reserva."));

        if (process.getStatus() == ReservationStatus.CANCELLED) {
            return process;
        }
        if (process.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(
                "CANCEL_NOT_ALLOWED",
                ErrorKind.CONFLICT,
                "Solo se puede cancelar una reserva confirmada. Estado actual: " + process.getStatus() + "."
            );
        }

        // Primero la catedra: si falla, la reserva sigue confirmada y se puede reintentar.
        holdClient.cancelReservation(reservationProcessId, reason);

        // El evento de Kafka puede haber cancelado la reserva mientras tanto: se vuelve a leer.
        ReservationProcess current = repository.findByReservationProcessId(reservationProcessId).orElse(process);
        if (current.getStatus() != ReservationStatus.CANCELLED) {
            current.transitionTo(ReservationStatus.CANCELLED);
            repository.save(current);
        }
        return current;
    }
}
