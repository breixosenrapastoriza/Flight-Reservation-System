package com.vuelosapp.flightreservation.repository;

import com.vuelosapp.flightreservation.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);
    List<Airport> findByCityContainingIgnoreCase(String city);
}
