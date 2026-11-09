package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.messaging;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.PhoneSubmissionPublisher;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Publica AdditionalInformationSubmitted en el topic de telefono. La key es el reservationProcessId. */
@Component
public class KafkaPhoneSubmissionPublisher implements PhoneSubmissionPublisher {

    private static final long SEND_TIMEOUT_SECONDS = 5;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;
    private final String groupId;

    public KafkaPhoneSubmissionPublisher(
        KafkaTemplate<String, String> kafkaTemplate,
        ObjectMapper objectMapper,
        @Value("${kafka.topics.telefono}") String topic,
        @Value("${kafka.group-id}") String groupId
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.groupId = groupId;
    }

    @Override
    public void publish(PhoneSubmission submission) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", submission.eventId());
        payload.put("eventType", "AdditionalInformationSubmitted");
        payload.put("groupId", groupId);
        payload.put("reservationProcessId", submission.reservationProcessId());
        payload.put("holdId", submission.holdId());
        payload.put("requestEventId", submission.requestEventId());
        payload.put("information", Map.of("phoneNumber", submission.phoneNumber()));
        payload.put("occurredAt", submission.occurredAt().toString());
        payload.put("schemaVersion", 1);

        String json = objectMapper.writeValueAsString(payload);
        try {
            kafkaTemplate.send(topic, submission.reservationProcessId(), json).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw unavailable();
        } catch (ExecutionException | TimeoutException e) {
            throw unavailable();
        }
    }

    private static BusinessException unavailable() {
        return new BusinessException("KAFKA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "No se pudo enviar el telefono. Intenta de nuevo.");
    }
}
