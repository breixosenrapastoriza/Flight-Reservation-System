package com.vuelosapp.flightreservation.repository;

import com.vuelosapp.flightreservation.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE " +
           "f.origin.code = :originCode AND " +
           "f.destination.code = :destinationCode AND " +
           "DATE(f.departureTime) = :departureDate AND " +
           "f.availableSeats >= :passengers AND " +
           "f.status = 'SCHEDULED'")
    List<Flight> findAvailableFlights(
        @Param("originCode") String originCode,
        @Param("destinationCode") String destinationCode,
        @Param("departureDate") LocalDate departureDate,
        @Param("passengers") int passengers
    );
    
    @Query("SELECT f FROM Flight f WHERE " +
           "f.origin.code = :originCode AND " +
           "f.destination.code = :destinationCode AND " +
           "f.departureTime >= :startDate AND " +
           "f.departureTime < :endDate AND " +
           "f.availableSeats >= :passengers AND " +
           "f.status = 'SCHEDULED'")
    List<Flight> findAvailableFlightsBetweenDates(
        @Param("originCode") String originCode,
        @Param("destinationCode") String destinationCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("passengers") int passengers
    );
}
