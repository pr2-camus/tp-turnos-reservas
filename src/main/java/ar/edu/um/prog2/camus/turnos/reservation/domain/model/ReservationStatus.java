package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import java.util.EnumSet;
import java.util.Set;

public enum ReservationStatus {
    HOLD_CREATED,
    WAITING_FOR_PHONE,
    PHONE_SUBMITTED,
    CONFIRMED,
    EXPIRED,
    INVALID,
    FAILED,
    CANCELLED;

    /** Estados a los que se puede pasar desde este. Vacio si es final. */
    public Set<ReservationStatus> allowedNext() {
        return switch (this) {
            case HOLD_CREATED -> EnumSet.of(WAITING_FOR_PHONE, EXPIRED, INVALID, FAILED);
            case WAITING_FOR_PHONE -> EnumSet.of(PHONE_SUBMITTED, EXPIRED, INVALID, FAILED);
            case PHONE_SUBMITTED -> EnumSet.of(CONFIRMED, WAITING_FOR_PHONE, EXPIRED, INVALID, FAILED);
            case CONFIRMED -> EnumSet.of(CANCELLED);
            case EXPIRED, INVALID, FAILED, CANCELLED -> EnumSet.noneOf(ReservationStatus.class);
        };
    }

    public boolean isFinal() {
        return allowedNext().isEmpty();
    }

    public boolean canTransitionTo(ReservationStatus next) {
        return allowedNext().contains(next);
    }
}
