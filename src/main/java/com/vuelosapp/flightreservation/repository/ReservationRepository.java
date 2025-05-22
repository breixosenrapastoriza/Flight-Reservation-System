package com.vuelosapp.flightreservation.repository;

import com.vuelosapp.flightreservation.entity.Reservation;
import com.vuelosapp.flightreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUser(User user);
    
    Optional<Reservation> findById(String id);
    
    boolean existsById(String id);
    
    List<Reservation> findByFlightId(Long flightId);
    
    List<Reservation> findByPassengerEmail(String email);
}
