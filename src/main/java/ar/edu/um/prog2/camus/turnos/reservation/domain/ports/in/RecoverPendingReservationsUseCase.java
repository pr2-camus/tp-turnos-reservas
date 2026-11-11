package ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.RecoveryResult;

public interface RecoverPendingReservationsUseCase {
    /** Reconcilia con la catedra los procesos abiertos y vence los que ya pasaron su plazo. */
    RecoveryResult recover();
}
