package com.vuelosapp.flightreservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flight_number", nullable = false)
    private String flightNumber;
    
    @ManyToOne
    @JoinColumn(name = "origin_id", nullable = false)
    private Airport origin;
    
    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Airport destination;
    
    @Column(nullable = false)
    private LocalDateTime departureTime;
    
    @Column(nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(nullable = false)
    private Integer totalSeats;
    
    @Column(nullable = false)
    private Integer availableSeats;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status = FlightStatus.SCHEDULED;
    
    public enum FlightStatus {
        SCHEDULED, ON_TIME, DELAYED, CANCELLED, COMPLETED
    }
}
