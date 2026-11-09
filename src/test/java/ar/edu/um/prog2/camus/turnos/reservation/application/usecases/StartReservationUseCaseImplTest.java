package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.in.GetAvailabilityUseCase;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldConfirmation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldCreated;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StartReservationUseCaseImplTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 11, 9);
    private static final LocalTime HORA = LocalTime.of(12, 30);
    private static final Instant VENCE = Instant.parse("2026-11-09T15:00:00Z");

    private CurrentUserProvider currentUser;
    private GetAvailabilityUseCase availability;
    private HoldClient holdClient;
    private ReservationProcessRepository repository;
    private StartReservationUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        currentUser = mock(CurrentUserProvider.class);
        availability = mock(GetAvailabilityUseCase.class);
        holdClient = mock(HoldClient.class);
        repository = mock(ReservationProcessRepository.class);
        useCase = new StartReservationUseCaseImpl(currentUser, availability, holdClient, repository);

        when(currentUser.getCurrentUser()).thenReturn(new ReservationUser(7L, "Zoe", "Camus"));
        when(availability.getAvailability(201L, LUNES)).thenReturn(Optional.of(List.of(new Slot(LUNES, HORA, LocalTime.of(13, 0)))));
        when(holdClient.createHold(201L, LUNES, HORA, "user-7")).thenReturn(new HoldCreated("hold-1", "process-1", VENCE));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private ReservationStatus lastSavedStatus() {
        ArgumentCaptor<ReservationProcess> captor = ArgumentCaptor.forClass(ReservationProcess.class);
        verify(repository, atLeastOnce()).save(captor.capture());
        return captor.getValue().getStatus();
    }

    @Test
    void dejaElProcesoEsperandoElTelefono() {
        when(holdClient.confirmHold("hold-1", "user-7", "Zoe", "Camus")).thenReturn(new HoldConfirmation("process-1", VENCE));

        ReservationProcess p = useCase.startReservation(201L, LUNES, HORA);

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(p.getUserId()).isEqualTo(7L);
        assertThat(p.getExpiresAt()).isEqualTo(VENCE);
    }

    @Test
    void siElTurnoNoEstaLibreNoCreaElHold() {
        assertThatThrownBy(() -> useCase.startReservation(201L, LUNES, LocalTime.of(14, 0))).isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.getCode()).isEqualTo("SLOT_NOT_AVAILABLE")
        );
        verify(holdClient, never()).createHold(any(Long.class), any(), any(), anyString());
    }

    @Test
    void siElProfesionalNoExisteDevuelveNoEncontrado() {
        when(availability.getAvailability(999L, LUNES)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.startReservation(999L, LUNES, HORA)).isInstanceOfSatisfying(BusinessException.class, e ->
            assertThat(e.getCode()).isEqualTo("PROFESSIONAL_NOT_FOUND")
        );
    }

    @Test
    void siLaCatedraRechazaLaConfirmacionElProcesoQuedaFallido() {
        when(holdClient.confirmHold(eq("hold-1"), anyString(), anyString(), anyString())).thenThrow(
            new BusinessException("EXTERNAL_PATIENT_MISMATCH", ErrorKind.VALIDATION, "rechazado")
        );

        assertThatThrownBy(() -> useCase.startReservation(201L, LUNES, HORA)).isInstanceOf(BusinessException.class);

        assertThat(lastSavedStatus()).isEqualTo(ReservationStatus.FAILED);
    }

    @Test
    void siLaCatedraNoRespondeElProcesoNoSeMarcaFallido() {
        when(holdClient.confirmHold(eq("hold-1"), anyString(), anyString(), anyString())).thenThrow(
            new BusinessException("CATEDRA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "sin respuesta")
        );

        assertThatThrownBy(() -> useCase.startReservation(201L, LUNES, HORA)).isInstanceOf(BusinessException.class);

        assertThat(lastSavedStatus()).isEqualTo(ReservationStatus.HOLD_CREATED);
    }
}
