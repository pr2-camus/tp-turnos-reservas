package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/** Proceso de reserva de un usuario. La regla de transiciones vive aca, en el dominio. */
public class ReservationProcess {

    private Long id;
    private final Long userId;
    private final String holdId;
    private final String reservationProcessId;
    private String requestEventId;
    private final Long professionalId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final Instant expiresAt;
    private ReservationStatus status;

    public ReservationProcess(
        Long userId,
        String holdId,
        String reservationProcessId,
        Long professionalId,
        LocalDate date,
        LocalTime startTime,
        Instant expiresAt
    ) {
        this.userId = userId;
        this.holdId = holdId;
        this.reservationProcessId = reservationProcessId;
        this.professionalId = professionalId;
        this.date = date;
        this.startTime = startTime;
        this.expiresAt = expiresAt;
        this.status = ReservationStatus.HOLD_CREATED;
    }

    /** Reconstruye un proceso ya guardado, con su estado actual. */
    public static ReservationProcess restore(
        Long id,
        Long userId,
        String holdId,
        String reservationProcessId,
        String requestEventId,
        Long professionalId,
        LocalDate date,
        LocalTime startTime,
        Instant expiresAt,
        ReservationStatus status
    ) {
        ReservationProcess p = new ReservationProcess(userId, holdId, reservationProcessId, professionalId, date, startTime, expiresAt);
        p.id = id;
        p.requestEventId = requestEventId;
        p.status = status;
        return p;
    }

    /** Cambia de estado si la transicion es valida; si no, lanza un error de conflicto. */
    public void transitionTo(ReservationStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new BusinessException(
                "INVALID_STATE_TRANSITION",
                ErrorKind.CONFLICT,
                "No se puede pasar de " + status + " a " + next + "."
            );
        }
        this.status = next;
    }

    public boolean isOwnedBy(Long someUserId) {
        return userId != null && userId.equals(someUserId);
    }

    public boolean isExpiredAt(Instant now) {
        return !status.isFinal() && status != ReservationStatus.CONFIRMED && now.isAfter(expiresAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getHoldId() {
        return holdId;
    }

    public String getReservationProcessId() {
        return reservationProcessId;
    }

    public String getRequestEventId() {
        return requestEventId;
    }

    public void setRequestEventId(String requestEventId) {
        this.requestEventId = requestEventId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
