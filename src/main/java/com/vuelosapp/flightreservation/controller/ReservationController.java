package com.vuelosapp.flightreservation.controller;

import com.vuelosapp.flightreservation.dto.ReservationDTO;
import com.vuelosapp.flightreservation.dto.ReservationRequestDTO;
import com.vuelosapp.flightreservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Controller", description = "APIs para la gestión de reservas de vuelos")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Crear una nueva reserva de vuelo")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Obtener los detalles de una reserva por su número de referencia")
    public ResponseEntity<ReservationDTO> getReservationByReference(
            @PathVariable String reference) {
        return ResponseEntity.ok(reservationService.getReservationByReference(reference));
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Obtener todas las reservas del usuario autenticado")
    public ResponseEntity<List<ReservationDTO>> getUserReservations() {
        return ResponseEntity.ok(reservationService.getUserReservations());
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Obtener todas las reservas (solo ADMIN)")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        // En una implementación real, aquí deberíamos tener paginación y filtros
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}
