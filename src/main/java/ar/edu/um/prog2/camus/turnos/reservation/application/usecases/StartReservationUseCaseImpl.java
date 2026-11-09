package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.in.GetAvailabilityUseCase;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldCreated;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.StartReservationUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StartReservationUseCaseImpl implements StartReservationUseCase {

    private final CurrentUserProvider currentUserProvider;
    private final GetAvailabilityUseCase getAvailabilityUseCase;
    private final HoldClient holdClient;
    private final ReservationProcessRepository repository;

    public StartReservationUseCaseImpl(
        CurrentUserProvider currentUserProvider,
        GetAvailabilityUseCase getAvailabilityUseCase,
        HoldClient holdClient,
        ReservationProcessRepository repository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.getAvailabilityUseCase = getAvailabilityUseCase;
        this.holdClient = holdClient;
        this.repository = repository;
    }

    @Override
    public ReservationProcess startReservation(long professionalId, LocalDate date, LocalTime startTime) {
        ReservationUser user = currentUserProvider.getCurrentUser();

        List<Slot> free = getAvailabilityUseCase
            .getAvailability(professionalId, date)
            .orElseThrow(() ->
                new BusinessException("PROFESSIONAL_NOT_FOUND", ErrorKind.NOT_FOUND, "No existe el profesional " + professionalId + ".")
            );
        boolean slotFree = free.stream().anyMatch(s -> s.startTime().equals(startTime));
        if (!slotFree) {
            throw new BusinessException("SLOT_NOT_AVAILABLE", ErrorKind.CONFLICT, "El turno elegido no esta disponible.");
        }

        String externalPatientId = "user-" + user.id();
        HoldCreated hold = holdClient.createHold(professionalId, date, startTime, externalPatientId);
        ReservationProcess process = repository.save(
            new ReservationProcess(user.id(), hold.holdId(), hold.reservationProcessId(), professionalId, date, startTime, hold.expiresAt())
        );

        try {
            holdClient.confirmHold(hold.holdId(), externalPatientId, user.firstName(), user.lastName());
        } catch (BusinessException e) {
            // Un rechazo definitivo cierra el proceso. Si la catedra no respondio no se sabe que paso: la recuperacion lo reconcilia.
            if (e.getKind() != ErrorKind.UNAVAILABLE) {
                process.transitionTo("HOLD_EXPIRED".equals(e.getCode()) ? ReservationStatus.EXPIRED : ReservationStatus.FAILED);
                repository.save(process);
            }
            throw e;
        }

        process.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        return repository.save(process);
    }
}
