package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.scheduling;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.RecoveryResult;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.in.RecoverPendingReservationsUseCase;
import org.junit.jupiter.api.Test;

class ReservationRecoverySchedulerTest {

    private final RecoverPendingReservationsUseCase useCase = mock(RecoverPendingReservationsUseCase.class);
    private final ReservationRecoveryScheduler scheduler = new ReservationRecoveryScheduler(useCase);

    @Test
    void alArrancarEjecutaLaRecuperacion() {
        when(useCase.recover()).thenReturn(new RecoveryResult(1, 0, true));

        scheduler.onStartup();

        verify(useCase).recover();
    }

    @Test
    void cadaTandaPeriodicaEjecutaLaRecuperacion() {
        when(useCase.recover()).thenReturn(new RecoveryResult(0, 2, false));

        scheduler.periodic();
        scheduler.periodic();

        verify(useCase, times(2)).recover();
    }

    @Test
    void siNoHayNovedadesNoFalla() {
        when(useCase.recover()).thenReturn(new RecoveryResult(0, 0, true));

        assertThatCode(scheduler::periodic).doesNotThrowAnyException();
    }

    @Test
    void siLaRecuperacionFallaElSchedulerNoPropagaElError() {
        when(useCase.recover()).thenThrow(new IllegalStateException("boom"));

        assertThatCode(scheduler::periodic).doesNotThrowAnyException();
        assertThatCode(scheduler::onStartup).doesNotThrowAnyException();
    }
}
