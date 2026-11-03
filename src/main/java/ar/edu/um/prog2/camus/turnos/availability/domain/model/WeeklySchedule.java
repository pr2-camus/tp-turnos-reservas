package ar.edu.um.prog2.camus.turnos.availability.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WeeklySchedule(
    Long id,
    Long professionalId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    int slotDurationMinutes,
    boolean enabled
) {}
