package ar.edu.um.prog2.camus.turnos.reservation.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ReservationProcessTest {

    private ReservationProcess nuevo() {
        return new ReservationProcess(
            1L,
            "hold-1",
            "process-1",
            201L,
            LocalDate.of(2026, 11, 9),
            LocalTime.of(12, 0),
            Instant.parse("2026-11-09T15:00:00Z")
        );
    }

    @Test
    void recorreElCaminoFelizHastaConfirmadaYCancelada() {
        ReservationProcess p = nuevo();
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.HOLD_CREATED);
        p.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        p.transitionTo(ReservationStatus.PHONE_SUBMITTED);
        p.transitionTo(ReservationStatus.CONFIRMED);
        p.transitionTo(ReservationStatus.CANCELLED);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void unRechazoDeTelefonoVuelveAEsperarTelefono() {
        ReservationProcess p = nuevo();
        p.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        p.transitionTo(ReservationStatus.PHONE_SUBMITTED);
        p.transitionTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, names = { "EXPIRED", "INVALID", "FAILED", "CANCELLED" })
    void unEstadoFinalNuncaVuelveAtras(ReservationStatus finalStatus) {
        assertThat(finalStatus.isFinal()).isTrue();
        for (ReservationStatus next : ReservationStatus.values()) {
            assertThat(finalStatus.canTransitionTo(next)).isFalse();
        }
    }

    @Test
    void rechazaTransicionesInvalidas() {
        ReservationProcess p = nuevo();
        assertThatThrownBy(() -> p.transitionTo(ReservationStatus.CONFIRMED)).isInstanceOf(BusinessException.class);
        p.transitionTo(ReservationStatus.EXPIRED);
        assertThatThrownBy(() -> p.transitionTo(ReservationStatus.WAITING_FOR_PHONE)).isInstanceOf(BusinessException.class);
        assertThat(p.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void confirmadaSoloPuedePasarACancelada() {
        assertThat(ReservationStatus.CONFIRMED.isFinal()).isFalse();
        assertThat(ReservationStatus.CONFIRMED.allowedNext()).containsExactly(ReservationStatus.CANCELLED);
    }

    @Test
    void verificaDuenoYVencimiento() {
        ReservationProcess p = nuevo();
        assertThat(p.isOwnedBy(1L)).isTrue();
        assertThat(p.isOwnedBy(2L)).isFalse();
        assertThat(p.isExpiredAt(Instant.parse("2026-11-09T16:00:00Z"))).isTrue();
        assertThat(p.isExpiredAt(Instant.parse("2026-11-09T14:00:00Z"))).isFalse();
    }
}
