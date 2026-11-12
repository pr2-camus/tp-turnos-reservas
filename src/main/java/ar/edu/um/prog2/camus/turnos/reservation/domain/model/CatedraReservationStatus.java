package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

/** Estados con los que la catedra informa una reserva en su listado. */
public enum CatedraReservationStatus {
    PHONE_PENDING,
    CONFIRMED,
    CANCELLED,
    FAILED,
    UNKNOWN;

    /** Convierte el texto de la catedra; un valor nuevo o nulo queda como UNKNOWN. */
    public static CatedraReservationStatus from(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
