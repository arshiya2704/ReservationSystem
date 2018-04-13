package com.example.reservationsystem.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.example.reservationsystem.exception.ResourceNotFoundException;
import com.example.reservationsystem.model.Passenger;
import com.example.reservationsystem.model.Plane;
import com.example.reservationsystem.model.Flight;

import com.example.reservationsystem.model.Reservation;
import com.example.reservationsystem.repository.FlightsRepository;
import com.example.reservationsystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/airline")
public class FlightsDeleteResource {
    @Autowired
    private FlightsRepository flightRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DeleteMapping(value = "/{flightnumber}",produces = "application/xml")
    public ResponseEntity<?> deleteFlight (@PathVariable(value = "flightnumber")String flightnumber){
        Flight flight = flightRepository.findById(flightnumber).orElseThrow(()-> new ResourceNotFoundException("Sorry, the requested flight with number "+ flightnumber +" does not exist",404));

        if(flight.getPassengers().isEmpty()){
            flightRepository.delete(flight);
        }
        else {
            throw new ResourceNotFoundException("This flight has one or more reservations", 400);
        }
        //return ResponseEntity.ok().build();
        throw new ResourceNotFoundException("Flight with number " + flightnumber +" is deleted successfully",200);
    }

}
