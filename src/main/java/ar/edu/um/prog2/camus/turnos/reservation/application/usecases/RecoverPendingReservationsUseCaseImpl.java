package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.RecoveryResult;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.RecoverPendingReservationsUseCase;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CatedraReservationsClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecoverPendingReservationsUseCaseImpl implements RecoverPendingReservationsUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverPendingReservationsUseCaseImpl.class);

    private static final Set<ReservationStatus> OPEN = EnumSet.of(
        ReservationStatus.HOLD_CREATED,
        ReservationStatus.WAITING_FOR_PHONE,
        ReservationStatus.PHONE_SUBMITTED,
        ReservationStatus.CONFIRMED
    );

    private final ReservationProcessRepository repository;
    private final CatedraReservationsClient catedraClient;

    public RecoverPendingReservationsUseCaseImpl(ReservationProcessRepository repository, CatedraReservationsClient catedraClient) {
        this.repository = repository;
        this.catedraClient = catedraClient;
    }

    @Override
    public RecoveryResult recover() {
        List<ReservationProcess> open = repository.findAllByStatusIn(OPEN);
        if (open.isEmpty()) {
            return new RecoveryResult(0, 0, true);
        }

        Map<String, CatedraReservationStatus> remote = fetchRemote();
        boolean available = remote != null;
        Instant now = Instant.now();
        int reconciled = 0;
        int expired = 0;

        for (ReservationProcess p : open) {
            try {
                ReservationStatus before = p.getStatus();
                // Primero la catedra: un telefono enviado puede haberse confirmado aunque se perdio el evento.
                boolean reconciledNow = available && reconcile(p, remote.get(p.getReservationProcessId()), now);
                // Sin respuesta de la catedra no se vence un proceso con telefono enviado: podria estar confirmado.
                boolean canExpire = available || p.getStatus() != ReservationStatus.PHONE_SUBMITTED;
                boolean expiredNow = !reconciledNow && canExpire && p.isExpiredAt(now);
                if (expiredNow) {
                    p.transitionTo(ReservationStatus.EXPIRED);
                }
                if (reconciledNow || expiredNow) {
                    repository.save(p);
                    log.info("Recuperacion: proceso {} pasa de {} a {}", p.getReservationProcessId(), before, p.getStatus());
                    reconciled += reconciledNow ? 1 : 0;
                    expired += expiredNow ? 1 : 0;
                }
            } catch (BusinessException e) {
                log.warn("No se pudo recuperar el proceso {}: {}", p.getReservationProcessId(), e.getMessage());
            }
        }
        return new RecoveryResult(reconciled, expired, available);
    }

    private Map<String, CatedraReservationStatus> fetchRemote() {
        try {
            Map<String, CatedraReservationStatus> byProcess = new HashMap<>();
            for (CatedraReservation r : catedraClient.listReservations()) {
                byProcess.put(r.reservationProcessId(), r.status());
            }
            return byProcess;
        } catch (BusinessException e) {
            log.warn("La catedra no respondio durante la recuperacion: {}", e.getMessage());
            return null;
        }
    }

    /** Alinea el proceso local con lo que dice la catedra. Devuelve true si cambio. */
    private boolean reconcile(ReservationProcess p, CatedraReservationStatus remote, Instant now) {
        if (remote == null) {
            return false;
        }
        return switch (remote) {
            case CONFIRMED -> {
                if (p.getStatus() == ReservationStatus.CONFIRMED) {
                    yield false;
                }
                advanceToConfirmed(p);
                yield true;
            }
            case CANCELLED -> {
                advanceToConfirmed(p);
                p.transitionTo(ReservationStatus.CANCELLED);
                yield true;
            }
            case FAILED -> {
                if (p.getStatus() == ReservationStatus.CONFIRMED) {
                    log.warn("Proceso {} confirmado localmente pero FAILED en la catedra: se deja como esta.", p.getReservationProcessId());
                    yield false;
                }
                // En la catedra FAILED agrupa fallos y vencimientos: si ya paso el plazo, se registra como vencido.
                p.transitionTo(p.isExpiredAt(now) ? ReservationStatus.EXPIRED : ReservationStatus.FAILED);
                yield true;
            }
            case PHONE_PENDING, UNKNOWN -> false;
        };
    }

    private void advanceToConfirmed(ReservationProcess p) {
        if (p.getStatus() == ReservationStatus.HOLD_CREATED) {
            p.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        }
        if (p.getStatus() == ReservationStatus.WAITING_FOR_PHONE) {
            p.transitionTo(ReservationStatus.PHONE_SUBMITTED);
        }
        if (p.getStatus() == ReservationStatus.PHONE_SUBMITTED) {
            p.transitionTo(ReservationStatus.CONFIRMED);
        }
    }
}
