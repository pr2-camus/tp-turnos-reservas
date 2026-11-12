package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class KafkaPhoneSubmissionPublisherPayloadTest {

    private KafkaTemplate<String, String> template;
    private KafkaPhoneSubmissionPublisher publisher;
    private PhoneSubmission submission;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        template = mock(KafkaTemplate.class);
        ObjectMapper mapper = JsonMapper.builder().build();
        publisher = new KafkaPhoneSubmissionPublisher(template, mapper, "topic-telefono", "grupo-test");
        submission = mock(PhoneSubmission.class);
        when(submission.eventId()).thenReturn("evt-1");
        when(submission.reservationProcessId()).thenReturn("process-1");
        when(submission.holdId()).thenReturn("hold-1");
        when(submission.requestEventId()).thenReturn("req-1");
        when(submission.phoneNumber()).thenReturn("2615550000");
        when(submission.occurredAt()).thenReturn(Instant.parse("2026-11-12T20:00:00Z"));
    }

    @Test
    void publicaElEventoConLaKeyYElPayloadEsperados() {
        when(template.send(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));

        publisher.publish(submission);

        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(template).send(eq("topic-telefono"), eq("process-1"), json.capture());
        assertThat(json.getValue())
            .contains("AdditionalInformationSubmitted")
            .contains("grupo-test")
            .contains("hold-1")
            .contains("2615550000");
    }

    @Test
    void siKafkaFallaDevuelveErrorDeDisponibilidad() {
        when(template.send(anyString(), anyString(), anyString())).thenReturn(
            CompletableFuture.failedFuture(new RuntimeException("sin broker"))
        );

        assertThatThrownBy(() -> publisher.publish(submission)).isInstanceOfSatisfying(BusinessException.class, e -> {
            assertThat(e.getCode()).isEqualTo("KAFKA_UNAVAILABLE");
            assertThat(e.getKind()).isEqualTo(ErrorKind.UNAVAILABLE);
        });
    }
}
