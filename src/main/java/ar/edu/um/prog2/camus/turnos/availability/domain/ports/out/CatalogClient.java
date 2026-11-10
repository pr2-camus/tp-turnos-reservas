package ar.edu.um.prog2.camus.turnos.availability.domain.ports.out;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.ProfessionalAgenda;
import java.util.Optional;

public interface CatalogClient {
    /** Agenda vigente del profesional segun el catalogo. Vacio si no existe. */
    Optional<ProfessionalAgenda> getAgenda(long professionalId);
}
