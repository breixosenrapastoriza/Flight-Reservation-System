package com.vuelosapp.flightreservation.service;

import com.vuelosapp.flightreservation.dto.FlightDTO;
import com.vuelosapp.flightreservation.dto.FlightSearchDTO;
import com.vuelosapp.flightreservation.entity.Airport;
import com.vuelosapp.flightreservation.entity.Flight;
import com.vuelosapp.flightreservation.exception.ResourceNotFoundException;
import com.vuelosapp.flightreservation.repository.AirportRepository;
import com.vuelosapp.flightreservation.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;
    
    public List<FlightDTO> searchFlights(FlightSearchDTO searchDTO) {
        // Validar que los aeropuertos existan
        Airport origin = airportRepository.findByCode(searchDTO.getOrigin().toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Origin airport not found"));
            
        Airport destination = airportRepository.findByCode(searchDTO.getDestination().toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found"));
            
        // Convertir la fecha de búsqueda a rango de fechas
        LocalDateTime startOfDay = searchDTO.getDepartureDate().atStartOfDay();
        LocalDateTime endOfDay = searchDTO.getDepartureDate().plusDays(1).atStartOfDay();
        
        // Buscar vuelos disponibles
        List<Flight> flights = flightRepository.findAvailableFlightsBetweenDates(
            origin.getCode(),
            destination.getCode(),
            startOfDay,
            endOfDay,
            searchDTO.getPassengers()
        );
        
        // Mapear a DTOs
        return flights.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public FlightDTO getFlightDetails(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
        return convertToDTO(flight);
    }
    
    @Transactional
    public FlightDTO updateFlightStatus(Long flightId, Flight.FlightStatus newStatus) {
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));
            
        flight.setStatus(newStatus);
        flight = flightRepository.save(flight);
        
        return convertToDTO(flight);
    }
    
    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = modelMapper.map(flight, FlightDTO.class);
        dto.setOriginAirportCode(flight.getOrigin().getCode());
        dto.setDestinationAirportCode(flight.getDestination().getCode());
        dto.setStatus(flight.getStatus().name());
        return dto;
    }
}
