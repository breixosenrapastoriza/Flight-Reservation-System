package com.vuelosapp.flightreservation.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.Arrays;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "El ID del vuelo es obligatorio")
    @Positive(message = "El ID del vuelo debe ser un número positivo")
    private Long flightId;
    
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
    
    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean termsAccepted;
    
    // Validación personalizada para asegurar que el email no sea un correo temporal
    @AssertFalse(message = "No se permiten correos electrónicos temporales")
    public boolean isTemporaryEmail() {
        if (passengerEmail == null) {
            return false;
        }
        // Lista de dominios temporales comunes (puedes ampliarla)
        String[] tempDomains = {"tempmail", "mailinator", "10minutemail", "guerrillamail", "yopmail", "temp-mail"};
        String emailDomain = passengerEmail.substring(passengerEmail.indexOf('@') + 1).toLowerCase();
        return Arrays.stream(tempDomains).anyMatch(emailDomain::contains);
    }
}
