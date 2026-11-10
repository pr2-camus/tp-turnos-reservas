package ar.edu.um.prog2.camus.turnos.availability.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Occupancy;
import java.time.LocalDate;
import java.util.List;

public interface OccupancyClient {
    /** Turnos no disponibles (confirmados y retenidos) informados por la catedra. */
    List<Occupancy> getOccupancies(long professionalId, LocalDate from, LocalDate to);
}
