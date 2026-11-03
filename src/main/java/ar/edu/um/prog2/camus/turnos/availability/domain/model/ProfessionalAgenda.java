package ar.edu.um.prog2.camus.turnos.availability.domain.model;

import java.util.List;

public record ProfessionalAgenda(Professional professional, List<WeeklySchedule> weeklySchedules) {}
