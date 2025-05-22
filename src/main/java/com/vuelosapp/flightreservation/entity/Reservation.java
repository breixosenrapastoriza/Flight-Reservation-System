package com.vuelosapp.flightreservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String passengerName;
    
    @Column(nullable = false)
    private String passengerEmail;
    
    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;
    
    @Column(nullable = false)
    private Integer passengers;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    @Column(nullable = false)
    private LocalDateTime bookingDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;
    
    public enum ReservationStatus {
        CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
    }
}
