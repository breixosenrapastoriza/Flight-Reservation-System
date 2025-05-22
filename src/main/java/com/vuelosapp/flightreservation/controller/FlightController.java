package com.vuelosapp.flightreservation.controller;

import com.vuelosapp.flightreservation.dto.FlightDTO;
import com.vuelosapp.flightreservation.dto.FlightSearchDTO;
import com.vuelosapp.flightreservation.entity.Flight;
import com.vuelosapp.flightreservation.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flight Controller", description = "APIs for flight operations")
public class FlightController {
    private final FlightService flightService;
    
    @PostMapping("/search")
    @Operation(summary = "Search for available flights")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @Valid @RequestBody FlightSearchDTO searchDTO) {
        return ResponseEntity.ok(flightService.searchFlights(searchDTO));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get flight details by ID")
    public ResponseEntity<FlightDTO> getFlightDetails(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightDetails(id));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    @Operation(summary = "Update flight status (ADMIN only)")
    public ResponseEntity<FlightDTO> updateFlightStatus(
            @PathVariable Long id,
            @RequestParam Flight.FlightStatus status) {
        return ResponseEntity.ok(flightService.updateFlightStatus(id, status));
    }
}
