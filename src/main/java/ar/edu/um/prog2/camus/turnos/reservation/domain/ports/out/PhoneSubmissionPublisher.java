package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.PhoneSubmission;

public interface PhoneSubmissionPublisher {
    /** Envia el telefono a la catedra. Si no se puede enviar, lanza un error de servicio no disponible. */
    void publish(PhoneSubmission submission);
}
