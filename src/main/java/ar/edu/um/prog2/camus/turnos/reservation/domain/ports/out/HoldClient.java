package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldConfirmation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.HoldCreated;
import java.time.LocalDate;
import java.time.LocalTime;

public interface HoldClient {
    /** Crea un hold temporal en la catedra. */
    HoldCreated createHold(long professionalId, LocalDate date, LocalTime startTime, String externalPatientId);

    /** Confirma inicialmente el hold; la catedra pedira el telefono por Kafka. */
    HoldConfirmation confirmHold(String holdId, String externalPatientId, String firstName, String lastName);
}
