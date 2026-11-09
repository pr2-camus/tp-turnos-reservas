package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEvent;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEventType;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.ProcessReservationEventUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;
import tools.jackson.databind.ObjectMapper;

class ReservationActionsListenerTest {

    private ProcessReservationEventUseCase useCase;
    private Acknowledgment ack;
    private ReservationActionsListener listener;

    @BeforeEach
    void setUp() {
        useCase = mock(ProcessReservationEventUseCase.class);
        ack = mock(Acknowledgment.class);
        listener = new ReservationActionsListener(useCase, new ObjectMapper());
    }

    private void receive(String json) {
        listener.onMessage(new ConsumerRecord<>("acciones", 0, 0L, "process-1", json), ack);
    }

    @Test
    void unEventoConocidoSeProcesaYSeConfirmaElOffset() {
        receive(
            """
            {"eventId":"e-1","eventType":"AppointmentConfirmed","reservationProcessId":"process-1","campoNuevo":"se ignora"}
            """
        );
        verify(useCase).process(new ReservationEvent("e-1", ReservationEventType.CONFIRMED, "process-1"));
        verify(ack).acknowledge();
    }

    @Test
    void unTipoDesconocidoSeIgnoraPeroSeConfirmaElOffset() {
        receive(
            """
            {"eventId":"e-1","eventType":"AlgoNuevo","reservationProcessId":"process-1"}
            """
        );
        verify(useCase, never()).process(any());
        verify(ack).acknowledge();
    }

    @Test
    void unJsonRotoSeDescartaYSeConfirmaElOffset() {
        receive("{roto");
        verify(useCase, never()).process(any());
        verify(ack).acknowledge();
    }
}
