package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.availability.domain.ports.in.GetAvailabilityUseCase;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class StartReservationUserNameTest {

    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final GetAvailabilityUseCase availability = mock(GetAvailabilityUseCase.class);
    private final HoldClient holdClient = mock(HoldClient.class);
    private final ReservationProcessRepository repository = mock(ReservationProcessRepository.class);
    private final StartReservationUseCaseImpl useCase = new StartReservationUseCaseImpl(
        currentUserProvider,
        availability,
        holdClient,
        repository
    );

    private void startConUsuario(String firstName, String lastName) {
        when(currentUserProvider.getCurrentUser()).thenReturn(new ReservationUser(1L, firstName, lastName));
        assertThatThrownBy(() -> useCase.startReservation(204L, LocalDate.of(2026, 11, 16), LocalTime.of(9, 0))).isInstanceOfSatisfying(
            BusinessException.class,
            e -> {
                assertThat(e.getCode()).isEqualTo("PATIENT_NAME_REQUIRED");
                assertThat(e.getKind()).isEqualTo(ErrorKind.VALIDATION);
            }
        );
        verifyNoInteractions(availability, holdClient, repository);
    }

    @Test
    void sinNombreNoSeCreaElHold() {
        startConUsuario(null, "Camus");
    }

    @Test
    void sinApellidoNoSeCreaElHold() {
        startConUsuario("Zoe", null);
    }

    @Test
    void unNombreEnBlancoSeTrataComoFaltante() {
        startConUsuario("   ", "Camus");
    }
}
