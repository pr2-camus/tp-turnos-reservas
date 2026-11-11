package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.SubmitPhoneUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.PhoneSubmissionPublisher;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SubmitPhoneUseCaseImpl implements SubmitPhoneUseCase {

    private final ReservationProcessRepository repository;
    private final CurrentUserProvider currentUserProvider;
    private final PhoneSubmissionPublisher publisher;

    public SubmitPhoneUseCaseImpl(
        ReservationProcessRepository repository,
        CurrentUserProvider currentUserProvider,
        PhoneSubmissionPublisher publisher
    ) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
        this.publisher = publisher;
    }

    @Override
    public ReservationProcess submitPhone(String reservationProcessId, String phoneNumber) {
        ReservationUser user = currentUserProvider.getCurrentUser();
        ReservationProcess process = repository
            .findByReservationProcessId(reservationProcessId)
            .filter(p -> p.isOwnedBy(user.id()))
            .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", ErrorKind.NOT_FOUND, "No existe la reserva."));

        if (process.getStatus() != ReservationStatus.WAITING_FOR_PHONE) {
            throw new BusinessException(
                "PHONE_NOT_EXPECTED",
                ErrorKind.CONFLICT,
                "La reserva esta en estado " + process.getStatus() + " y no espera un telefono."
            );
        }
        if (process.getRequestEventId() == null) {
            throw new BusinessException(
                "PHONE_NOT_REQUESTED",
                ErrorKind.CONFLICT,
                "Todavia no se pidio el telefono. Intenta de nuevo en unos segundos."
            );
        }
        if (process.isExpiredAt(Instant.now())) {
            throw new BusinessException("RESERVATION_EXPIRED", ErrorKind.CONFLICT, "La reserva ya vencio.");
        }

        // Se guarda antes de publicar: si la catedra contesta muy rapido, el listener ya ve el estado nuevo.
        process.transitionTo(ReservationStatus.PHONE_SUBMITTED);
        repository.save(process);
        try {
            publisher.publish(
                new PhoneSubmission(
                    "phone-submitted-" + UUID.randomUUID(),
                    process.getReservationProcessId(),
                    process.getHoldId(),
                    process.getRequestEventId(),
                    phoneNumber,
                    Instant.now()
                )
            );
        } catch (RuntimeException e) {
            process.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
            repository.save(process);
            throw e;
        }
        return process;
    }
}
