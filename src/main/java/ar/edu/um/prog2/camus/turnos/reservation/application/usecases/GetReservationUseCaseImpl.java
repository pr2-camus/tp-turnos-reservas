package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.GetReservationUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GetReservationUseCaseImpl implements GetReservationUseCase {

    private final ReservationProcessRepository repository;
    private final CurrentUserProvider currentUserProvider;

    public GetReservationUseCaseImpl(ReservationProcessRepository repository, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Optional<ReservationProcess> getReservation(String reservationProcessId) {
        ReservationUser user = currentUserProvider.getCurrentUser();
        return repository.findByReservationProcessId(reservationProcessId).filter(p -> p.isOwnedBy(user.id()));
    }
}
