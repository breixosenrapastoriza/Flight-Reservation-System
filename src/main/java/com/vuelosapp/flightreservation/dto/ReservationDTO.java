package com.vuelosapp.flightreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private String id;
    private Long flightId;
    private Long userId;
    private String passengerName;
    private String passengerEmail;
    private Integer passengers;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private String status;
    
    // Datos adicionales para facilitar la visualización
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
