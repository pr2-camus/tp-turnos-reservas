package ar.edu.um.prog2.camus.turnos.availability.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record Occupancy(LocalDate date, LocalTime startTime, OccupancyStatus status, Instant expiresAt) {}
