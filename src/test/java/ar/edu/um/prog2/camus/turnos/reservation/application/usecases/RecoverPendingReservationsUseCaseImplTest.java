package ar.edu.um.prog2.camus.turnos.reservation.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservation;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.CatedraReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.RecoveryResult;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationProcess;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CatedraReservationsClient;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.ReservationProcessRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecoverPendingReservationsUseCaseImplTest {

    private static final Instant PASADO = Instant.parse("2020-01-01T00:00:00Z");
    private static final Instant FUTURO = Instant.parse("2099-01-01T00:00:00Z");

    private ReservationProcessRepository repository;
    private CatedraReservationsClient catedra;
    private RecoverPendingReservationsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationProcessRepository.class);
        catedra = mock(CatedraReservationsClient.class);
        useCase = new RecoverPendingReservationsUseCaseImpl(repository, catedra);
    }

    private ReservationProcess process(String id, ReservationStatus status, Instant expiresAt) {
        return ReservationProcess.restore(
            1L,
            1L,
            "hold-" + id,
            id,
            null,
            201L,
            LocalDate.of(2026, 11, 16),
            LocalTime.of(12, 0),
            expiresAt,
            status
        );
    }

    private void abiertos(ReservationProcess... processes) {
        when(repository.findAllByStatusIn(any())).thenReturn(List.of(processes));
    }

    private void catedraDice(CatedraReservation... reservations) {
        when(catedra.listReservations()).thenReturn(List.of(reservations));
    }

    private void catedraCaida() {
        when(catedra.listReservations()).thenThrow(new BusinessException("CATEDRA_UNAVAILABLE", ErrorKind.UNAVAILABLE, "caida"));
    }

    @Test
    void sinProcesosAbiertosNoConsultaALaCatedra() {
        abiertos();

        RecoveryResult result = useCase.recover();

        assertThat(result).isEqualTo(new RecoveryResult(0, 0, true));
        verify(catedra, never()).listReservations();
    }

    @Test
    void unProcesoVencidoQueLaCatedraNoConoceSeVence() {
        ReservationProcess p = process("p1", ReservationStatus.WAITING_FOR_PHONE, PASADO);
        abiertos(p);
        catedraDice();

        RecoveryResult result = useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(result.expired()).isEqualTo(1);
        verify(repository).save(p);
    }

    @Test
    void unProcesoVigenteSeDejaIgual() {
        ReservationProcess p = process("p1", ReservationStatus.WAITING_FOR_PHONE, FUTURO);
        abiertos(p);
        catedraDice();

        useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        verify(repository, never()).save(any());
    }

    @Test
    void unTelefonoEnviadoQueLaCatedraConfirmoNoSeVenceSinoQueSeConfirma() {
        ReservationProcess p = process("p1", ReservationStatus.PHONE_SUBMITTED, PASADO);
        abiertos(p);
        catedraDice(new CatedraReservation("p1", CatedraReservationStatus.CONFIRMED));

        RecoveryResult result = useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.reconciled()).isEqualTo(1);
        assertThat(result.expired()).isZero();
        verify(repository).save(p);
    }

    @Test
    void unaReservaConfirmadaQueLaCatedraCancelaPasaACancelada() {
        ReservationProcess p = process("p1", ReservationStatus.CONFIRMED, FUTURO);
        abiertos(p);
        catedraDice(new CatedraReservation("p1", CatedraReservationStatus.CANCELLED));

        useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(repository).save(p);
    }

    @Test
    void unProcesoPendienteQueLaCatedraDaPorFallidoPasaAFallido() {
        ReservationProcess p = process("p1", ReservationStatus.WAITING_FOR_PHONE, FUTURO);
        abiertos(p);
        catedraDice(new CatedraReservation("p1", CatedraReservationStatus.FAILED));

        useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.FAILED);
    }

    @Test
    void unaReservaConfirmadaQueLaCatedraDaPorFallidaNoSeToca() {
        ReservationProcess p = process("p1", ReservationStatus.CONFIRMED, FUTURO);
        abiertos(p);
        catedraDice(new CatedraReservation("p1", CatedraReservationStatus.FAILED));

        useCase.recover();

        assertThat(p.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(repository, never()).save(any());
    }

    @Test
    void sinRespuestaDeLaCatedraSeVenceLoQueNoTieneTelefonoEnviado() {
        ReservationProcess esperando = process("p1", ReservationStatus.WAITING_FOR_PHONE, PASADO);
        abiertos(esperando);
        catedraCaida();

        RecoveryResult result = useCase.recover();

        assertThat(esperando.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(result.catedraAvailable()).isFalse();
    }

    @Test
    void sinRespuestaDeLaCatedraNoSeVenceUnTelefonoEnviado() {
        ReservationProcess enviado = process("p2", ReservationStatus.PHONE_SUBMITTED, PASADO);
        abiertos(enviado);
        catedraCaida();

        useCase.recover();

        assertThat(enviado.getStatus()).isEqualTo(ReservationStatus.PHONE_SUBMITTED);
        verify(repository, never()).save(any());
    }
}
