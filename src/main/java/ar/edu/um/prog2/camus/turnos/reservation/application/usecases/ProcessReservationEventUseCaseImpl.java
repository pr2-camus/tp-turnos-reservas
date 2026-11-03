package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationEvent;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.ProcessReservationEventUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ProcessedEventRepository;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessReservationEventUseCaseImpl implements ProcessReservationEventUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessReservationEventUseCaseImpl.class);

    private final ProcessedEventRepository processedEvents;
    private final ReservationProcessRepository repository;

    public ProcessReservationEventUseCaseImpl(ProcessedEventRepository processedEvents, ReservationProcessRepository repository) {
        this.processedEvents = processedEvents;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void process(ReservationEvent event) {
        if (!processedEvents.registerIfNew(event.eventId())) {
            LOG.info("Evento duplicado ignorado: {}", event.eventId());
            return;
        }
        Optional<ReservationProcess> found = repository.findByReservationProcessId(event.reservationProcessId());
        if (found.isEmpty()) {
            LOG.warn("Evento {} para un proceso desconocido: {}", event.eventId(), event.reservationProcessId());
            return;
        }
        ReservationProcess process = found.orElseThrow();
        switch (event.type()) {
            case INFORMATION_REQUESTED -> onInformationRequested(process, event);
            case CONFIRMED -> moveTo(process, ReservationStatus.CONFIRMED);
            case INFORMATION_REJECTED -> moveTo(process, ReservationStatus.WAITING_FOR_PHONE);
            case EXPIRED -> moveTo(process, ReservationStatus.EXPIRED);
            case INVALID -> moveTo(process, ReservationStatus.INVALID);
            case CANCELLED -> moveTo(process, ReservationStatus.CANCELLED);
        }
        repository.save(process);
    }

    private void onInformationRequested(ReservationProcess process, ReservationEvent event) {
        if (process.getStatus().isFinal()) {
            LOG.info(
                "Pedido de informacion tardio ignorado: el proceso {} ya esta en {}",
                process.getReservationProcessId(),
                process.getStatus()
            );
            return;
        }
        process.setRequestEventId(event.eventId());
        if (process.getStatus() == ReservationStatus.HOLD_CREATED) {
            process.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        }
    }

    /** Aplica la transicion si es valida. Un evento tardio sobre un estado final no reabre el proceso. */
    private void moveTo(ReservationProcess process, ReservationStatus next) {
        if (process.getStatus().canTransitionTo(next)) {
            process.transitionTo(next);
        } else {
            LOG.info(
                "Evento ignorado: el proceso {} no puede pasar de {} a {}",
                process.getReservationProcessId(),
                process.getStatus(),
                next
            );
        }
    }
}
