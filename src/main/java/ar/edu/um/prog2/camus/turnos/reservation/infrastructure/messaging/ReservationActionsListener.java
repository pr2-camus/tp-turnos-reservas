package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.messaging;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEvent;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEventType;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.ProcessReservationEventUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/** Escucha las acciones de la catedra sobre las reservas. El offset se confirma despues de procesar. */
@Component
public class ReservationActionsListener {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationActionsListener.class);

    private final ProcessReservationEventUseCase useCase;
    private final ObjectMapper objectMapper;

    public ReservationActionsListener(ProcessReservationEventUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.acciones}", autoStartup = "${kafka.listener.enabled:true}")
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        JsonNode json;
        try {
            json = objectMapper.readTree(record.value());
        } catch (RuntimeException e) {
            LOG.warn("Mensaje descartado: no es un JSON valido");
            ack.acknowledge();
            return;
        }
        String eventId = text(json, "eventId");
        String eventType = text(json, "eventType");
        String processId = text(json, "reservationProcessId");
        ReservationEventType type = toType(eventType);
        if (eventId == null || type == null || processId == null) {
            LOG.info("Evento ignorado (tipo desconocido o incompleto): {}", eventType);
            ack.acknowledge();
            return;
        }
        useCase.process(new ReservationEvent(eventId, type, processId));
        ack.acknowledge();
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static ReservationEventType toType(String eventType) {
        if (eventType == null) {
            return null;
        }
        return switch (eventType) {
            case "AdditionalInformationRequested" -> ReservationEventType.INFORMATION_REQUESTED;
            case "AppointmentConfirmed" -> ReservationEventType.CONFIRMED;
            case "AdditionalInformationRejected" -> ReservationEventType.INFORMATION_REJECTED;
            case "AppointmentProcessExpired" -> ReservationEventType.EXPIRED;
            case "AppointmentProcessInvalid" -> ReservationEventType.INVALID;
            case "AppointmentCancelled" -> ReservationEventType.CANCELLED;
            default -> null;
        };
    }
}
