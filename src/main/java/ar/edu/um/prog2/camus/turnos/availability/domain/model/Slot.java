package ar.edu.um.prog2.camus.turnos.availability.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Slot(LocalDate date, LocalTime startTime, LocalTime endTime) {}
