package com.example.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@XmlRootElement(name = "Flight")
@JsonRootName(value = "Flight")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Flight {

    @Id
    @Column(name = "flightnumber")
    private String flightnumber;
    @Column(name = "price")
    private double price;
    @Column(name = "origin")
    private String origin;
    @Column(name = "destination")
    private String destination;
    @Column(name = "departuretime")
    private String departuretime;
    @Column(name = "arrivaltime")
    private String arrivaltime;
    @Column(name = "seatsleft")
    private int seatsleft;
    @Column(name = "description")
    private String description;

    @Embedded
    private Plane plane;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ManyToMany
    @JoinTable(name = "flight_passengers",
            joinColumns = @JoinColumn(name = "flight_id", referencedColumnName = "flightnumber"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private Set<Passenger> passengers = new HashSet<Passenger>();

    public Flight() {

    }

    public String getFlightnumber() {
        return flightnumber;
    }

    public void setFlightnumber(String flightnumber) {
        this.flightnumber = flightnumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departuretime) {
        this.departuretime = departuretime;
    }

    public String getArrivaltime() {
        return arrivaltime;
    }

    public void setArrivaltime(String arrivaltime) {
        this.arrivaltime = arrivaltime;
    }

    public int getSeatsleft() {
        return seatsleft;
    }

    public void setSeatsleft(int seatsleft) {
        this.seatsleft = seatsleft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void removeCircle() {

        Iterator<Passenger> pIt = this.getPassengers().iterator();
        Passenger passenger = null;

        while (pIt.hasNext()) {
            passenger = pIt.next();
            passenger.setReservation(null);
        }
    }
}