package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "processed_event")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
