package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.ListMyReservationsUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ListMyReservationsUseCaseImpl implements ListMyReservationsUseCase {

    private final ReservationProcessRepository repository;
    private final CurrentUserProvider currentUserProvider;

    public ListMyReservationsUseCaseImpl(ReservationProcessRepository repository, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public List<ReservationProcess> listMyReservations() {
        ReservationUser user = currentUserProvider.getCurrentUser();
        return repository.findAllByUserId(user.id());
    }
}
