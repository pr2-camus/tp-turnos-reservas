package ar.edu.um.prog2.camus.turnos.availability.application.usecases;

import ar.edu.um.prog2.camus.turnos.availability.domain.model.Occupancy;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.ProfessionalAgenda;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.Slot;
import ar.edu.um.prog2.camus.turnos.availability.domain.model.WeeklySchedule;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.in.GetAvailabilityUseCase;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.CatalogClient;
import ar.edu.um.prog2.camus.turnos.availability.domain.ports.out.OccupancyClient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GetAvailabilityUseCaseImpl implements GetAvailabilityUseCase {

    private final CatalogClient catalogClient;
    private final OccupancyClient occupancyClient;

    public GetAvailabilityUseCaseImpl(CatalogClient catalogClient, OccupancyClient occupancyClient) {
        this.catalogClient = catalogClient;
        this.occupancyClient = occupancyClient;
    }

    @Override
    public Optional<List<Slot>> getAvailability(long professionalId, LocalDate date) {
        Optional<ProfessionalAgenda> agenda = catalogClient.getAgenda(professionalId);
        if (agenda.isEmpty()) {
            return Optional.empty();
        }
        if (!agenda.get().professional().enabled()) {
            return Optional.of(List.of());
        }

        List<WeeklySchedule> schedules = agenda
            .get()
            .weeklySchedules()
            .stream()
            .filter(WeeklySchedule::enabled)
            .filter(s -> s.dayOfWeek() == date.getDayOfWeek())
            .toList();
        if (schedules.isEmpty()) {
            return Optional.of(List.of());
        }

        Set<LocalTime> occupied = occupancyClient
            .getOccupancies(professionalId, date, date)
            .stream()
            .filter(o -> o.date().equals(date))
            .map(Occupancy::startTime)
            .collect(Collectors.toSet());

        List<Slot> free = new ArrayList<>();
        for (WeeklySchedule schedule : schedules) {
            free.addAll(buildSlots(schedule, date, occupied));
        }
        free.sort((a, b) -> a.startTime().compareTo(b.startTime()));
        return Optional.of(free);
    }

    private List<Slot> buildSlots(WeeklySchedule schedule, LocalDate date, Set<LocalTime> occupied) {
        List<Slot> slots = new ArrayList<>();
        int minutes = schedule.slotDurationMinutes();
        if (minutes <= 0) {
            return slots;
        }
        LocalTime start = schedule.startTime();
        while (true) {
            LocalTime end = start.plusMinutes(minutes);
            boolean wrapsMidnight = !end.isAfter(start);
            if (wrapsMidnight || end.isAfter(schedule.endTime())) {
                break;
            }
            if (!occupied.contains(start)) {
                slots.add(new Slot(date, start, end));
            }
            start = end;
        }
        return slots;
    }
}
