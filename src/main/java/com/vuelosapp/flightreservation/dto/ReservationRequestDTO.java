package com.vuelosapp.flightreservation.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "El ID del vuelo es obligatorio")
    private Long flightId;
    
    @NotBlank(message = "El nombre del pasajero es obligatorio")
    private String passengerName;
    
    @NotBlank(message = "El email del pasajero es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String passengerEmail;
    
    @Min(value = 1, message = "Debe haber al menos un pasajero")
    private Integer passengers;
}
