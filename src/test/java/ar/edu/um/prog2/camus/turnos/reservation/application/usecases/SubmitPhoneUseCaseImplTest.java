package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.PhoneSubmissionPublisher;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SubmitPhoneUseCaseImplTest {

    private ReservationProcessRepository repository;
    private PhoneSubmissionPublisher publisher;
    private SubmitPhoneUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationProcessRepository.class);
        publisher = mock(PhoneSubmissionPublisher.class);
        CurrentUserProvider users = mock(CurrentUserProvider.class);
        when(users.getCurrentUser()).thenReturn(new ReservationUser(7L, "Ana", "Perez"));
        useCase = new SubmitPhoneUseCaseImpl(repository, users, publisher);
    }

    private ReservationProcess process(Long userId, ReservationStatus status, String requestEventId, Instant expiresAt) {
        ReservationProcess p = ReservationProcess.restore(
            1L,
            userId,
            "hold-1",
            "process-1",
            requestEventId,
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            expiresAt,
            status
        );
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.of(p));
        return p;
    }

    private Instant future() {
        return Instant.now().plusSeconds(3600);
    }

    @Test
    void publicaElTelefonoYPasaAEnviado() {
        ReservationProcess p = process(7L, ReservationStatus.WAITING_FOR_PHONE, "req-1", future());

        useCase.submitPhone("process-1", "+5492615551234");

        ArgumentCaptor<PhoneSubmission> sent = ArgumentCaptor.forClass(PhoneSubmission.class);
        verify(publisher).publish(sent.capture());
        assertThat(sent.getValue().phoneNumber()).isEqualTo("+5492615551234");
        assertThat(sent.getValue().requestEventId()).isEqualTo("req-1");
        assertThat(sent.getValue().reservationProcessId()).isEqualTo("process-1");
        assertThat(sent.getValue().eventId()).isNotBlank();
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.PHONE_SUBMITTED);
    }

    @Test
    void siFallaLaPublicacionVuelveAEsperarTelefono() {
        ReservationProcess p = process(7L, ReservationStatus.WAITING_FOR_PHONE, "req-1", future());
        doThrow(new BusinessException("KAFKA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "x"))
            .when(publisher)
            .publish(any());

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        verify(repository, times(2)).save(p);
    }

    @Test
    void laReservaDeOtroUsuarioNoSeEncuentra() {
        process(99L, ReservationStatus.WAITING_FOR_PHONE, "req-1", future());

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void unaReservaInexistenteNoSeEncuentra() {
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void siNoEstaEsperandoTelefonoSeRechaza() {
        process(7L, ReservationStatus.PHONE_SUBMITTED, "req-1", future());

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void siTodaviaNoSePidioElTelefonoSeRechaza() {
        process(7L, ReservationStatus.WAITING_FOR_PHONE, null, future());

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void unaReservaVencidaSeRechaza() {
        process(7L, ReservationStatus.WAITING_FOR_PHONE, "req-1", Instant.now().minusSeconds(60));

        assertThatThrownBy(() -> useCase.submitPhone("process-1", "123")).isInstanceOf(BusinessException.class);
        verify(publisher, never()).publish(any());
    }
}
