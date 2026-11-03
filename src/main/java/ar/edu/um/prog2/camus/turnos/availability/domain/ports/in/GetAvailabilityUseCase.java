package ar.edu.um.prog2.camus.turnos.availability.domain.ports.in;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GetAvailabilityUseCase {
    /** Turnos libres del profesional en la fecha. Vacio si el profesional no existe. */
    Optional<List<Slot>> getAvailability(long professionalId, LocalDate date);
}
