package ar.edu.um.prog2.camus.turnos.availability.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Occupancy;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.OccupancyStatus;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.Professional;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.ProfessionalAgenda;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.WeeklySchedule;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.CatalogClient;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.OccupancyClient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetAvailabilityUseCaseImplTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 11, 9);

    private CatalogClient catalog;
    private OccupancyClient occupancy;
    private GetAvailabilityUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        catalog = mock(CatalogClient.class);
        occupancy = mock(OccupancyClient.class);
        useCase = new GetAvailabilityUseCaseImpl(catalog, occupancy);
    }

    private ProfessionalAgenda agenda(boolean enabled) {
        Professional p = new Professional(201L, 101L, "Ana", "Gomez", enabled);
        WeeklySchedule lunes = new WeeklySchedule(1L, 201L, DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(15, 0), 30, true);
        WeeklySchedule miercoles = new WeeklySchedule(2L, 201L, DayOfWeek.WEDNESDAY, LocalTime.of(17, 0), LocalTime.of(20, 0), 30, true);
        return new ProfessionalAgenda(p, List.of(lunes, miercoles));
    }

    @Test
    void generaTodosLosTurnosDelDiaSiNoHayOcupaciones() {
        when(catalog.getAgenda(201L)).thenReturn(Optional.of(agenda(true)));
        when(occupancy.getOccupancies(201L, LUNES, LUNES)).thenReturn(List.of());

        List<Slot> slots = useCase.getAvailability(201L, LUNES).orElseThrow();

        assertThat(slots).hasSize(6);
        assertThat(slots.get(0).startTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(slots.get(5).endTime()).isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    void quitaLosTurnosOcupadosYRetenidos() {
        when(catalog.getAgenda(201L)).thenReturn(Optional.of(agenda(true)));
        when(occupancy.getOccupancies(201L, LUNES, LUNES)).thenReturn(
            List.of(
                new Occupancy(LUNES, LocalTime.of(12, 0), OccupancyStatus.CONFIRMED, null),
                new Occupancy(LUNES, LocalTime.of(13, 30), OccupancyStatus.HELD, null)
            )
        );

        List<Slot> slots = useCase.getAvailability(201L, LUNES).orElseThrow();

        assertThat(slots).hasSize(4);
        assertThat(slots).extracting(Slot::startTime).doesNotContain(LocalTime.of(12, 0), LocalTime.of(13, 30));
    }

    @Test
    void sinHorarioEseDiaDevuelveListaVacia() {
        when(catalog.getAgenda(201L)).thenReturn(Optional.of(agenda(true)));

        assertThat(useCase.getAvailability(201L, LUNES.plusDays(1)).orElseThrow()).isEmpty();
    }

    @Test
    void profesionalDeshabilitadoNoTieneTurnos() {
        when(catalog.getAgenda(201L)).thenReturn(Optional.of(agenda(false)));

        assertThat(useCase.getAvailability(201L, LUNES).orElseThrow()).isEmpty();
    }

    @Test
    void profesionalInexistenteDevuelveVacio() {
        when(catalog.getAgenda(999L)).thenReturn(Optional.empty());

        assertThat(useCase.getAvailability(999L, LUNES)).isEmpty();
    }
}
