package com.example.reservationsystem.repository;

import com.example.reservationsystem.model.Flight;
import com.example.reservationsystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation, String> {
    @Query(value = "SELECT distinct flight_id,passenger_id  FROM flight_passengers  where ('p1' = :id or passenger_id=:id) and ('flightNumber' = :fno or flight_id=:fno)",
            nativeQuery=true
    )
    List<Object> findPassengerById(@Param("id") String id, @Param("fno") String fno);
}
