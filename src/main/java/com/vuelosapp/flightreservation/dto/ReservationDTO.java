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
public class ReservationDTO {
    @NotBlank(message = "El ID de la reserva es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]{6,10}$", message = "El ID de reserva debe contener entre 6 y 10 caracteres alfanuméricos en mayúsculas")
    private String id;
    
    @NotNull(message = "El ID del vuelo es obligatorio")
    @Positive(message = "El ID del vuelo debe ser un número positivo")
    private Long flightId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long userId;
    
    @NotBlank(message = "El nombre del pasajero es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\s-]+", 
             message = "El nombre solo puede contener letras, espacios y guiones")
    private String passengerName;
    
    @NotBlank(message = "El email del pasajero es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String passengerEmail;
    
    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos un pasajero")
    @Max(value = 10, message = "No se pueden reservar más de 10 pasajeros a la vez")
    private Integer passengers;
    
    @NotNull(message = "El precio total es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio total debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio total debe tener como máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal totalPrice;
    
    @NotNull(message = "La fecha de reserva es obligatoria")
    @PastOrPresent(message = "La fecha de reserva no puede ser futura")
    private LocalDateTime bookingDate;
    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(PENDING|CONFIRMED|CANCELLED|COMPLETED)$", 
             message = "El estado debe ser uno de: PENDING, CONFIRMED, CANCELLED, COMPLETED")
    private String status;
    
    // Datos adicionales para facilitar la visualización
    @NotBlank(message = "El número de vuelo es obligatorio")
    private String flightNumber;
    
    @NotBlank(message = "El origen es obligatorio")
    private String origin;
    
    @NotBlank(message = "El destino es obligatorio")
    private String destination;
    
    @NotNull(message = "La hora de salida es obligatoria")
    private LocalDateTime departureTime;
    
    @NotNull(message = "La hora de llegada es obligatoria")
    private LocalDateTime arrivalTime;
    
    // Validación personalizada para asegurar que la fecha de llegada sea posterior a la de salida
    @AssertTrue(message = "La hora de llegada debe ser posterior a la hora de salida")
    public boolean isArrivalAfterDeparture() {
        if (departureTime == null || arrivalTime == null) {
            return true;  // La validación de @NotNull ya manejará estos casos
        }
        return arrivalTime.isAfter(departureTime);
    }
    
    // Validación personalizada para asegurar que la fecha de reserva no sea futura
    @AssertTrue(message = "La fecha de reserva no puede ser futura")
    public boolean isBookingDateValid() {
        if (bookingDate == null) {
            return true;  // La validación de @NotNull ya manejará este caso
        }
        return !bookingDate.isAfter(LocalDateTime.now());
    }
}
