package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.persistence.entity;

import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation_process")
public class ReservationProcessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "hold_id", nullable = false)
    private String holdId;

    @Column(name = "reservation_process_id", nullable = false, unique = true)
    private String reservationProcessId;

    @Column(name = "request_event_id")
    private String requestEventId;

    @Column(name = "professional_id", nullable = false)
    private Long professionalId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate date;

    @Convert(converter = LocalTimeStringConverter.class)
    @Column(name = "start_time", nullable = false, length = 5)
    private LocalTime startTime;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReservationStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHoldId() {
        return holdId;
    }

    public void setHoldId(String holdId) {
        this.holdId = holdId;
    }

    public String getReservationProcessId() {
        return reservationProcessId;
    }

    public void setReservationProcessId(String v) {
        this.reservationProcessId = v;
    }

    public String getRequestEventId() {
        return requestEventId;
    }

    public void setRequestEventId(String requestEventId) {
        this.requestEventId = requestEventId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
