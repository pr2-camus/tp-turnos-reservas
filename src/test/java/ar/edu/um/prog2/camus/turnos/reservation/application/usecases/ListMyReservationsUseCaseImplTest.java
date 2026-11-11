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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListMyReservationsUseCaseImplTest {

    private ReservationProcessRepository repository;
    private CurrentUserProvider currentUserProvider;
    private ListMyReservationsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationProcessRepository.class);
        currentUserProvider = mock(CurrentUserProvider.class);
        useCase = new ListMyReservationsUseCaseImpl(repository, currentUserProvider);
    }

    private ReservationProcess processOf(Long userId, String processId) {
        return ReservationProcess.restore(
            1L,
            userId,
            "hold-" + processId,
            processId,
            null,
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.parse("2026-11-09T15:00:00Z"),
            ReservationStatus.CONFIRMED
        );
    }

    @Test
    void devuelveSoloLasReservasDelUsuarioAutenticado() {
        when(currentUserProvider.getCurrentUser()).thenReturn(new ReservationUser(1L, "Ana", "Perez"));
        ReservationProcess mine = processOf(1L, "process-1");
        when(repository.findAllByUserId(1L)).thenReturn(List.of(mine));

        assertThat(useCase.listMyReservations()).containsExactly(mine);
    }

    @Test
    void elOtroUsuarioNoVeLasReservasDelPrimero() {
        when(currentUserProvider.getCurrentUser()).thenReturn(new ReservationUser(2L, "Beto", "Gomez"));
        when(repository.findAllByUserId(2L)).thenReturn(List.of());

        assertThat(useCase.listMyReservations()).isEmpty();
    }
}
