package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.adapter;

import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ProcessedEventRepository;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity.ProcessedEventEntity;
import ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.repository.JpaProcessedEventRepository;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class JpaProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final JpaProcessedEventRepository jpaRepository;

    public JpaProcessedEventRepositoryAdapter(JpaProcessedEventRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean registerIfNew(String eventId) {
        if (jpaRepository.existsById(eventId)) {
            return false;
        }
        ProcessedEventEntity e = new ProcessedEventEntity();
        e.setEventId(eventId);
        e.setProcessedAt(Instant.now());
        jpaRepository.save(e);
        return true;
    }
}
