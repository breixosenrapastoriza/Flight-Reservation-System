package com.vuelosapp.flightreservation.config;

import com.vuelosapp.flightreservation.entity.*;
import com.vuelosapp.flightreservation.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader {
    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostConstruct
    @Transactional
    public void loadSampleData() {
        // Cargar aeropuertos de ejemplo si no existen
        if (airportRepository.count() == 0) {
            Airport madrid = new Airport(null, "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", "Spain");
            Airport barcelona = new Airport(null, "BCN", "Barcelona-El Prat", "Barcelona", "Spain");
            Airport paris = new Airport(null, "CDG", "Charles de Gaulle", "Paris", "France");
            Airport london = new Airport(null, "LHR", "Heathrow", "London", "UK");
            Airport berlin = new Airport(null, "BER", "Berlin Brandenburg", "Berlin", "Germany");
            Airport roma = new Airport(null, "FCO", "Leonardo da Vinci-Fiumicino", "Rome", "Italy");
            Airport newyork = new Airport(null, "JFK", "John F. Kennedy", "New York", "USA");
            
            List<Airport> airports = airportRepository.saveAll(List.of(madrid, barcelona, paris, london, berlin, roma, newyork));
            
            // Cargar vuelos de ejemplo
            LocalDateTime now = LocalDateTime.now();
            
            // Vuelos de Madrid a Barcelona
            Flight flight1 = new Flight(
                null, "IB1234", madrid, barcelona,
                now.plusDays(1).withHour(8).withMinute(0),
                now.plusDays(1).withHour(9).withMinute(15),
                200, 150, new BigDecimal("99.99"), Flight.FlightStatus.SCHEDULED
            );
            
            Flight flight2 = new Flight(
                null, "VY1234", madrid, barcelona,
                now.plusDays(1).withHour(14).withMinute(30),
                now.plusDays(1).withHour(15).withMinute(45),
                180, 180, new BigDecimal("79.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelos de Barcelona a Madrid
            Flight flight3 = new Flight(
                null, "IB5678", barcelona, madrid,
                now.plusDays(1).withHour(10).withMinute(0),
                now.plusDays(1).withHour(11).withMinute(15),
                200, 200, new BigDecimal("89.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelos de Madrid a París
            Flight flight4 = new Flight(
                null, "AF1234", madrid, paris,
                now.plusDays(2).withHour(9).withMinute(0),
                now.plusDays(2).withHour(11).withMinute(15),
                250, 100, new BigDecimal("149.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelos de París a Londres
            Flight flight5 = new Flight(
                null, "BA1234", paris, london,
                now.plusDays(3).withHour(11).withMinute(30),
                now.plusDays(3).withHour(11).withMinute(50), // Hora local de Londres
                220, 50, new BigDecimal("129.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelos adicionales para probar validaciones
            Flight flight6 = new Flight(
                null, "LH4321", berlin, roma,
                now.plusDays(4).withHour(12).withMinute(0),
                now.plusDays(4).withHour(14).withMinute(15),
                180, 10, new BigDecimal("199.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelo con pocos asientos disponibles
            Flight flight7 = new Flight(
                null, "AZ789", roma, madrid,
                now.plusDays(2).withHour(16).withMinute(30),
                now.plusDays(2).withHour(18).withMinute(45),
                5, 1, new BigDecimal("89.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelo de larga distancia con precio alto
            Flight flight8 = new Flight(
                null, "BA9876", london, newyork,
                now.plusDays(7).withHour(10).withMinute(0),
                now.plusDays(7).withHour(14).withMinute(30), // Hora local de NY
                300, 250, new BigDecimal("899.99"), Flight.FlightStatus.SCHEDULED
            );
            
            // Vuelo con muchos asientos disponibles
            Flight flight9 = new Flight(
                null, "IB3456", madrid, berlin,
                now.plusDays(3).withHour(7).withMinute(0),
                now.plusDays(3).withHour(10).withMinute(30),
                400, 400, new BigDecimal("149.99"), Flight.FlightStatus.SCHEDULED
            );
            
            flightRepository.saveAll(List.of(flight1, flight2, flight3, flight4, flight5, flight6, flight7, flight8, flight9));
            
            System.out.println("Datos de prueba de aeropuertos y vuelos cargados correctamente");
        }
        
        // Cargar usuarios de ejemplo si no existen
        if (userRepository.count() == 0) {
            // Usuario normal 1
            User user1 = new User();
            user1.setUsername("usuario1");
            user1.setEmail("usuario1@example.com");
            user1.setPassword(passwordEncoder.encode("usuario1123"));
            user1.setRole("USER");
            
            // Usuario normal 2 (para probar múltiples reservas)
            User user2 = new User();
            user2.setUsername("viajerofrecuente");
            user2.setEmail("viajero@ejemplo.com");
            user2.setPassword(passwordEncoder.encode("viaje1234"));
            user2.setRole("USER");
            
            // Usuario administrador
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            
            userRepository.saveAll(List.of(user1, user2, admin));
            System.out.println("Usuarios de prueba creados correctamente");
        }
        
        // Cargar reservas de ejemplo si no existen
        if (reservationRepository.count() == 0) {
            // Obtener usuarios y vuelos existentes
            User user1 = userRepository.findByEmail("usuario1@example.com")
                .orElseThrow(() -> new RuntimeException("Usuario de prueba no encontrado"));
            User user2 = userRepository.findByEmail("viajero@ejemplo.com")
                .orElseThrow(() -> new RuntimeException("Usuario viajero no encontrado"));
            
            List<Flight> flights = flightRepository.findAll();
            if (!flights.isEmpty()) {
                // Reservas para el primer vuelo (Madrid-Barcelona)
                Flight flight1 = flights.get(0);
                createSampleReservation(user1, flight1, "Juan Pérez", "juan.perez@example.com", 2);
                createSampleReservation(user1, flight1, "Ana García", "ana.garcia@example.com", 1);
                
                // Reservas para el vuelo a París
                Flight flight4 = flights.get(3);
                createSampleReservation(user1, flight4, "Carlos López", "carlos.lopez@example.com", 3);
                
                // Reserva para el vuelo a Roma (pocos asientos disponibles)
                Flight flight6 = flights.get(5);
                createSampleReservation(user2, flight6, "María Rodríguez", "maria.rodriguez@ejemplo.com", 4);
                
                // Reserva para el vuelo con un solo asiento disponible
                Flight flight7 = flights.get(6);
                createSampleReservation(user2, flight7, "Último Pasajero", "ultimo@ejemplo.com", 1);
                
                // Varias reservas para el usuario viajero frecuente
                Flight flight8 = flights.get(7); // Vuelo a Nueva York
                createSampleReservation(user2, flight8, "Viajero Frecuente", "viajero@ejemplo.com", 2);
                
                // Otra reserva para el mismo usuario en un vuelo diferente
                Flight flight9 = flights.get(8); // Vuelo a Berlín
                createSampleReservation(user2, flight9, "Viajero Frecuente", "viajero@ejemplo.com", 1);
                
                System.out.println("Reservas de prueba creadas correctamente");
            }
        }
    }
    
    private void createSampleReservation(User user, Flight flight, String passengerName, String passengerEmail, int passengers) {
        // Asegurarse de que hay suficientes asientos disponibles
        if (flight.getAvailableSeats() < passengers) {
            System.out.println("No hay suficientes asientos disponibles en el vuelo " + flight.getFlightNumber());
            return;
        }
        
        Reservation reservation = new Reservation();
        reservation.setFlight(flight);
        reservation.setUser(user);
        reservation.setPassengerName(passengerName);
        reservation.setPassengerEmail(passengerEmail);
        reservation.setPassengers(passengers);
        reservation.setTotalPrice(flight.getPrice().multiply(BigDecimal.valueOf(passengers)));
        reservation.setId("RES" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setBookingDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        // Actualizar asientos disponibles
        flight.setAvailableSeats(flight.getAvailableSeats() - passengers);
        
        // Guardar los cambios
        flightRepository.save(flight);
        reservationRepository.save(reservation);
    }
}
