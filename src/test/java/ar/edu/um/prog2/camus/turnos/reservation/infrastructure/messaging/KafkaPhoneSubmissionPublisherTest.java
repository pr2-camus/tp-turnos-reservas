package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class KafkaPhoneSubmissionPublisherTest {

    private KafkaTemplate<String, String> template;
    private KafkaPhoneSubmissionPublisher publisher;
    private final PhoneSubmission submission = new PhoneSubmission(
        "phone-submitted-1",
        "process-1",
        "hold-1",
        "req-1",
        "+5492615551234",
        Instant.parse("2026-11-09T15:00:00Z")
    );

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        template = mock(KafkaTemplate.class);
        publisher = new KafkaPhoneSubmissionPublisher(template, new ObjectMapper(), "telefono-topic", "grupo-1");
    }

    @Test
    void publicaElEventoConTodosLosCamposYLaKeyDelProceso() {
        when(template.send(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));

        publisher.publish(submission);

        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(template).send(eq("telefono-topic"), eq("process-1"), json.capture());
        JsonNode node = new ObjectMapper().readTree(json.getValue());
        assertThat(node.get("eventId").asText()).isEqualTo("phone-submitted-1");
        assertThat(node.get("eventType").asText()).isEqualTo("AdditionalInformationSubmitted");
        assertThat(node.get("groupId").asText()).isEqualTo("grupo-1");
        assertThat(node.get("reservationProcessId").asText()).isEqualTo("process-1");
        assertThat(node.get("holdId").asText()).isEqualTo("hold-1");
        assertThat(node.get("requestEventId").asText()).isEqualTo("req-1");
        assertThat(node.get("information").get("phoneNumber").asText()).isEqualTo("+5492615551234");
        assertThat(node.get("occurredAt").asText()).isEqualTo("2026-11-09T15:00:00Z");
        assertThat(node.get("schemaVersion").asInt()).isEqualTo(1);
    }

    @Test
    void siKafkaFallaLanzaServicioNoDisponible() {
        when(template.send(anyString(), anyString(), anyString())).thenReturn(
            CompletableFuture.failedFuture(new RuntimeException("caido"))
        );

        assertThatThrownBy(() -> publisher.publish(submission)).isInstanceOf(BusinessException.class);
    }
}
