package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetReservationUseCaseImplTest {

    private ReservationProcessRepository repository;
    private GetReservationUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationProcessRepository.class);
        CurrentUserProvider users = mock(CurrentUserProvider.class);
        when(users.getCurrentUser()).thenReturn(new ReservationUser(7L, "Ana", "Perez"));
        useCase = new GetReservationUseCaseImpl(repository, users);
    }

    private void saved(Long userId) {
        ReservationProcess p = ReservationProcess.restore(
            1L,
            userId,
            "hold-1",
            "process-1",
            null,
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.now().plusSeconds(3600),
            ReservationStatus.HOLD_CREATED
        );
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.of(p));
    }

    @Test
    void devuelveLaReservaPropia() {
        saved(7L);
        assertThat(useCase.getReservation("process-1")).isPresent();
    }

    @Test
    void noDevuelveLaReservaDeOtroUsuario() {
        saved(99L);
        assertThat(useCase.getReservation("process-1")).isEmpty();
    }
}
