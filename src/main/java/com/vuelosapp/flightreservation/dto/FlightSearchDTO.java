package com.vuelosapp.flightreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchDTO {
    @NotBlank(message = "Origin airport code is required")
    private String origin;
    
    @NotBlank(message = "Destination airport code is required")
    private String destination;
    
    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;
    
    @Positive(message = "Number of passengers must be positive")
    private Integer passengers = 1;
}
