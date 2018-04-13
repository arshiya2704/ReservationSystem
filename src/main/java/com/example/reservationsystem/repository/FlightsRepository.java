package com.example.reservationsystem.repository;

import java.io.Serializable;

import com.example.reservationsystem.model.Flight;
//import org.springframework.data.jpa.repository.CRUDRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface FlightsRepository extends JpaRepository<Flight, String> {
}
