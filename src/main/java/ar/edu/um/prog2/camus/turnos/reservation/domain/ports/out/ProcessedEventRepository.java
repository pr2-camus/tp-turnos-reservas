package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

public interface ProcessedEventRepository {
    /** Registra el evento. Devuelve false si ya estaba registrado (mensaje duplicado). */
    boolean registerIfNew(String eventId);
}
