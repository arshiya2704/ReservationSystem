package com.example.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity
@XmlRootElement(name = "Reservation")
@JsonRootName(value = "Reservation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reservation {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name= "reservationnumber")
    private String reservationnumber;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="passenger_id", referencedColumnName="id")
    private Passenger passenger= null;

    @Column(name= "price")
    private double price;

    @ManyToMany
    @JoinTable(name = "reservations_flights",
            joinColumns = @JoinColumn(name = "reservation_id", referencedColumnName="reservationnumber"),
            inverseJoinColumns = @JoinColumn(name = "flight_id", referencedColumnName="flightnumber"))
    private Set<Flight> flights = new HashSet<Flight>();

    public Reservation(){

    }

    public String getReservationnumber() {
        return reservationnumber;
    }

    public void setReservationnumber(String reservationnumber) {
        this.reservationnumber = reservationnumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;}

    public void removeCircle() {
        Iterator<Flight> flIt = this.getFlights().iterator();;

        this.getPassenger().setReservation(null); // fetch passenger

        while(flIt.hasNext()){
            flIt.next().setPassengers(null);
        }
    }
}
