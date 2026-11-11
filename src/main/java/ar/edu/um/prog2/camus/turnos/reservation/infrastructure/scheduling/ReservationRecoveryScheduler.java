package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.scheduling;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.RecoveryResult;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.RecoverPendingReservationsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Dispara la recuperacion al arrancar y despues cada cierto tiempo. */
@Component
public class ReservationRecoveryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationRecoveryScheduler.class);

    private final RecoverPendingReservationsUseCase useCase;

    public ReservationRecoveryScheduler(RecoverPendingReservationsUseCase useCase) {
        this.useCase = useCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        run("arranque");
    }

    @Scheduled(
        fixedDelayString = "${reservation.recovery.interval-ms:60000}",
        initialDelayString = "${reservation.recovery.interval-ms:60000}"
    )
    public void periodic() {
        run("periodica");
    }

    private void run(String origin) {
        try {
            RecoveryResult result = useCase.recover();
            if (result.reconciled() > 0 || result.expired() > 0 || !result.catedraAvailable()) {
                log.info(
                    "Recuperacion ({}): {} reconciliadas, {} vencidas, catedra disponible: {}",
                    origin,
                    result.reconciled(),
                    result.expired(),
                    result.catedraAvailable()
                );
            }
        } catch (RuntimeException e) {
            log.error("Fallo la recuperacion de reservas ({})", origin, e);
        }
    }
}
