package com.example.reservationsystem.repository;

import com.example.reservationsystem.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengersRepository extends JpaRepository<Passenger, String> {
}
