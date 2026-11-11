package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.HoldClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CancelReservationUseCaseImplTest {

    private ReservationProcessRepository repository;
    private HoldClient holdClient;
    private CancelReservationUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationProcessRepository.class);
        holdClient = mock(HoldClient.class);
        CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
        when(currentUserProvider.getCurrentUser()).thenReturn(new ReservationUser(1L, "Ana", "Perez"));
        useCase = new CancelReservationUseCaseImpl(repository, currentUserProvider, holdClient);
    }

    private ReservationProcess process(Long ownerId, ReservationStatus status) {
        ReservationProcess p = ReservationProcess.restore(
            1L,
            ownerId,
            "hold-1",
            "process-1",
            null,
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.parse("2026-11-09T15:00:00Z"),
            status
        );
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void cancelaUnaReservaConfirmadaPropia() {
        ReservationProcess p = process(1L, ReservationStatus.CONFIRMED);

        ReservationProcess result = useCase.cancelReservation("process-1", "Cambio de planes");

        verify(holdClient).cancelReservation("process-1", "Cambio de planes");
        verify(repository).save(p);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void laReservaDeOtroUsuarioNoSeCancela() {
        process(2L, ReservationStatus.CONFIRMED);

        assertThatThrownBy(() -> useCase.cancelReservation("process-1", null))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("code", "RESERVATION_NOT_FOUND");
        verify(holdClient, never()).cancelReservation(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void unaReservaInexistenteNoSeCancela() {
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.cancelReservation("process-1", null))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("code", "RESERVATION_NOT_FOUND");
    }

    @Test
    void repetirLaCancelacionNoVuelveALaCatedraNiFalla() {
        ReservationProcess p = process(1L, ReservationStatus.CANCELLED);

        ReservationProcess result = useCase.cancelReservation("process-1", null);

        assertThat(result).isSameAs(p);
        verify(holdClient, never()).cancelReservation(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void unaReservaQueNoEstaConfirmadaNoSeCancela() {
        process(1L, ReservationStatus.WAITING_FOR_PHONE);

        assertThatThrownBy(() -> useCase.cancelReservation("process-1", null))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("code", "CANCEL_NOT_ALLOWED");
        verify(holdClient, never()).cancelReservation(any(), any());
    }

    @Test
    void siFallaLaCatedraLaReservaSigueConfirmada() {
        ReservationProcess p = process(1L, ReservationStatus.CONFIRMED);
        doThrow(new BusinessException("CATEDRA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "caida"))
            .when(holdClient)
            .cancelReservation("process-1", null);

        assertThatThrownBy(() -> useCase.cancelReservation("process-1", null)).isInstanceOf(BusinessException.class);

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(repository, never()).save(any());
    }

    @Test
    void siElEventoDeKafkaYaLaCancelaNoSeTransicionaDeNuevo() {
        ReservationProcess before = process(1L, ReservationStatus.CONFIRMED);
        ReservationProcess after = ReservationProcess.restore(
            1L,
            1L,
            "hold-1",
            "process-1",
            null,
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.parse("2026-11-09T15:00:00Z"),
            ReservationStatus.CANCELLED
        );
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.of(before), Optional.of(after));

        ReservationProcess result = useCase.cancelReservation("process-1", null);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(repository, never()).save(any());
    }
}
