package com.vuelosapp.flightreservation.service;

import com.vuelosapp.flightreservation.dto.ReservationDTO;
import com.vuelosapp.flightreservation.dto.ReservationRequestDTO;
import com.vuelosapp.flightreservation.entity.Flight;
import com.vuelosapp.flightreservation.entity.Reservation;
import com.vuelosapp.flightreservation.entity.User;
import com.vuelosapp.flightreservation.exception.NotEnoughSeatsException;
import com.vuelosapp.flightreservation.exception.ResourceNotFoundException;
import com.vuelosapp.flightreservation.repository.FlightRepository;
import com.vuelosapp.flightreservation.repository.ReservationRepository;
import com.vuelosapp.flightreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // Obtener el vuelo y verificar disponibilidad
        Flight flight = flightRepository.findById(request.getFlightId())
            .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
            
        if (flight.getAvailableSeats() < request.getPassengers()) {
            throw new NotEnoughSeatsException("No hay suficientes asientos disponibles en este vuelo");
        }
        
        // Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setFlight(flight);
        reservation.setUser(user);
        reservation.setPassengerName(request.getPassengerName());
        reservation.setPassengerEmail(request.getPassengerEmail());
        reservation.setPassengers(request.getPassengers());
        reservation.setTotalPrice(flight.getPrice().multiply(BigDecimal.valueOf(request.getPassengers())));
        reservation.setBookingDate(LocalDateTime.now());
        
        // Generar ID único
        String id;
        do {
            id = "RES" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (reservationRepository.existsById(id));
        
        reservation.setId(id);
        
        // Actualizar asientos disponibles
        flight.setAvailableSeats(flight.getAvailableSeats() - request.getPassengers());
        flightRepository.save(flight);
        
        // Guardar la reserva
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reserva creada con ID: {}", id);
        
        return convertToDTO(savedReservation);
    }
    
    public ReservationDTO getReservationById(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return convertToDTO(reservation);
    }
    
    public List<ReservationDTO> getUserReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Primero intenta buscar por email, luego por username
        User user = userRepository.findByEmail(username)
            .or(() -> userRepository.findByUsername(username))
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
            
        return reservationRepository.findByUser(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelReservation(Long reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
            
        // Verificar que el usuario es el propietario de la reserva o es ADMIN
        boolean isOwner = reservation.getUser().getEmail().equals(username) || 
                         reservation.getUser().getUsername().equals(username);
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("No autorizado para cancelar esta reserva");
        }
        
        // Actualizar asientos disponibles
        Flight flight = reservation.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + reservation.getPassengers());
        flightRepository.save(flight);
        
        // Actualizar estado de la reserva
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        log.info("Reserva {} cancelada por el usuario {}", reservationId, username);
    }
    
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = modelMapper.map(reservation, ReservationDTO.class);
        
        // Mapear datos adicionales del vuelo
        Flight flight = reservation.getFlight();
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setOrigin(flight.getOrigin().getCode());
        dto.setDestination(flight.getDestination().getCode());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        
        return dto;
    }
}
