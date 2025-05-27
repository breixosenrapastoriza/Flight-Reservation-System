package com.vuelosapp.flightreservation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchDTO {
    @NotBlank(message = "El código de aeropuerto de origen es obligatorio")
    @Size(min = 3, max = 3, message = "El código de aeropuerto debe tener exactamente 3 caracteres")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de aeropuerto solo puede contener letras mayúsculas")
    private String origin;
    
    @NotBlank(message = "El código de aeropuerto de destino es obligatorio")
    @Size(min = 3, max = 3, message = "El código de aeropuerto debe tener exactamente 3 caracteres")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de aeropuerto solo puede contener letras mayúsculas")
    private String destination;
    
    @NotNull(message = "La fecha de salida es obligatoria")
    @FutureOrPresent(message = "La fecha de salida debe ser hoy o en el futuro")
    private LocalDate departureDate;
    
    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos un pasajero")
    @Max(value = 10, message = "No se pueden buscar vuelos para más de 10 pasajeros a la vez")
    private Integer passengers = 1;
    
    // Validación personalizada para asegurar que el origen y destino sean diferentes
    @AssertFalse(message = "El origen y el destino no pueden ser iguales")
    public boolean isSameOriginAndDestination() {
        if (origin == null || destination == null) {
            return false;
        }
        return origin.equalsIgnoreCase(destination);
    }
    
    // Validación personalizada para la fecha máxima de búsqueda (hasta 1 año en el futuro)
    @AssertTrue(message = "La fecha de salida no puede ser mayor a un año a partir de hoy")
    public boolean isValidDepartureDate() {
        if (departureDate == null) {
            return true; // La validación de @NotNull ya manejará este caso
        }
        LocalDate maxDate = LocalDate.now().plusYears(1);
        return departureDate.isBefore(maxDate) || departureDate.isEqual(maxDate);
    }
}
