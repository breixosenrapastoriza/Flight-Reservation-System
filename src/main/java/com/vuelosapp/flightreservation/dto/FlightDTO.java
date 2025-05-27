package com.vuelosapp.flightreservation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private Long id;
    
    @NotBlank(message = "El número de vuelo es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{3,4}$", message = "El formato del número de vuelo no es válido (ej: AA123 o AAL1234)")
    private String flightNumber;
    
    @NotBlank(message = "El código de aeropuerto de origen es obligatorio")
    @Size(min = 3, max = 3, message = "El código de aeropuerto debe tener exactamente 3 caracteres")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de aeropuerto solo puede contener letras mayúsculas")
    private String originAirportCode;
    
    @NotBlank(message = "El código de aeropuerto de destino es obligatorio")
    @Size(min = 3, max = 3, message = "El código de aeropuerto debe tener exactamente 3 caracteres")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de aeropuerto solo puede contener letras mayúsculas")
    private String destinationAirportCode;
    
    @NotNull(message = "La hora de salida es obligatoria")
    @Future(message = "La hora de salida debe ser en el futuro")
    private LocalDateTime departureTime;
    
    @NotNull(message = "La hora de llegada es obligatoria")
    @Future(message = "La hora de llegada debe ser en el futuro")
    private LocalDateTime arrivalTime;
    
    @NotNull(message = "El número total de asientos es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 asiento")
    @Max(value = 500, message = "No puede haber más de 500 asientos")
    private Integer totalSeats;
    
    private Integer availableSeats;  // Se calcula automáticamente
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener como máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal price;
    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(SCHEDULED|DELAYED|CANCELLED|COMPLETED)$", 
             message = "El estado debe ser uno de: SCHEDULED, DELAYED, CANCELLED, COMPLETED")
    private String status;
    
    // Validación personalizada para asegurar que la fecha de llegada sea posterior a la de salida
    @AssertTrue(message = "La hora de llegada debe ser posterior a la hora de salida")
    public boolean isArrivalAfterDeparture() {
        if (departureTime == null || arrivalTime == null) {
            return true;  // La validación de @NotNull ya manejará estos casos
        }
        return arrivalTime.isAfter(departureTime);
    }
}
