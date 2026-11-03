package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEvent;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEventType;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ProcessedEventRepository;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessReservationEventUseCaseImplTest {

    private ProcessedEventRepository processedEvents;
    private ReservationProcessRepository repository;
    private ProcessReservationEventUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        processedEvents = mock(ProcessedEventRepository.class);
        repository = mock(ReservationProcessRepository.class);
        useCase = new ProcessReservationEventUseCaseImpl(processedEvents, repository);
        when(processedEvents.registerIfNew(any())).thenReturn(true);
    }

    private ReservationProcess processIn(ReservationStatus target) {
        ReservationProcess p = new ReservationProcess(
            1L,
            "hold-1",
            "process-1",
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.parse("2026-11-09T15:00:00Z")
        );
        if (target != ReservationStatus.HOLD_CREATED) {
            p.transitionTo(target == ReservationStatus.EXPIRED ? ReservationStatus.EXPIRED : ReservationStatus.WAITING_FOR_PHONE);
        }
        if (target == ReservationStatus.PHONE_SUBMITTED || target == ReservationStatus.CONFIRMED) {
            p.transitionTo(ReservationStatus.PHONE_SUBMITTED);
        }
        if (target == ReservationStatus.CONFIRMED) {
            p.transitionTo(ReservationStatus.CONFIRMED);
        }
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.of(p));
        return p;
    }

    private void send(String eventId, ReservationEventType type) {
        useCase.process(new ReservationEvent(eventId, type, "process-1"));
    }

    @Test
    void elPedidoDeInformacionGuardaElRequestEventId() {
        ReservationProcess p = processIn(ReservationStatus.WAITING_FOR_PHONE);
        send("req-1", ReservationEventType.INFORMATION_REQUESTED);
        assertThat(p.getRequestEventId()).isEqualTo("req-1");
        verify(repository).save(p);
    }

    @Test
    void elPedidoAdelantadoMarcaEsperandoTelefono() {
        ReservationProcess p = processIn(ReservationStatus.HOLD_CREATED);
        send("req-1", ReservationEventType.INFORMATION_REQUESTED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(p.getRequestEventId()).isEqualTo("req-1");
    }

    @Test
    void laConfirmacionPasaAConfirmada() {
        ReservationProcess p = processIn(ReservationStatus.PHONE_SUBMITTED);
        send("ev-1", ReservationEventType.CONFIRMED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void elRechazoVuelveAEsperarTelefono() {
        ReservationProcess p = processIn(ReservationStatus.PHONE_SUBMITTED);
        send("ev-1", ReservationEventType.INFORMATION_REJECTED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }

    @Test
    void laExpiracionPasaAVencida() {
        ReservationProcess p = processIn(ReservationStatus.WAITING_FOR_PHONE);
        send("ev-1", ReservationEventType.EXPIRED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void procesoInvalidoPasaAInvalido() {
        ReservationProcess p = processIn(ReservationStatus.WAITING_FOR_PHONE);
        send("ev-1", ReservationEventType.INVALID);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.INVALID);
    }

    @Test
    void laCancelacionPasaDeConfirmadaACancelada() {
        ReservationProcess p = processIn(ReservationStatus.CONFIRMED);
        send("ev-1", ReservationEventType.CANCELLED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void unEventoDuplicadoNoCambiaNada() {
        when(processedEvents.registerIfNew("ev-1")).thenReturn(false);
        send("ev-1", ReservationEventType.CONFIRMED);
        verify(repository, never()).findByReservationProcessId(any());
        verify(repository, never()).save(any());
    }

    @Test
    void unEventoTardioSobreUnEstadoFinalNoReabreElProceso() {
        ReservationProcess p = processIn(ReservationStatus.EXPIRED);
        send("ev-1", ReservationEventType.CONFIRMED);
        send("ev-2", ReservationEventType.INFORMATION_REQUESTED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(p.getRequestEventId()).isNull();
    }

    @Test
    void unProcesoDesconocidoSeIgnora() {
        when(repository.findByReservationProcessId("process-1")).thenReturn(Optional.empty());
        send("ev-1", ReservationEventType.CONFIRMED);
        verify(repository, never()).save(any());
    }
}
