package com.example.reservationsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Entity
@XmlRootElement(name = "Passenger")
@JsonRootName(value = "Passenger")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Passenger {

//    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "id")
	@Id
	@Column(length = 255)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
    private String id;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "age")
    private int age;
    @Column(name = "gender")
    private String gender;
    @Column(name = "phone")
    private String phone;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "passenger")
    private Set<Reservation> reservation = new HashSet<Reservation>(); 

    public Passenger() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



    public String getPhone() {
        return phone;
    }

    public Set<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(Set<Reservation> reservation) {
        this.reservation = reservation;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public void removeCircle() {

        Iterator<Reservation> reIt = this.getReservation().iterator();
        Iterator<Flight> flIt = null;
        Reservation reservation = null;

        while (reIt.hasNext()) {
            reservation = reIt.next();
            reservation.setPassenger(null);
            flIt = reservation.getFlights().iterator();
            while (flIt.hasNext()) {
                flIt.next().setPassengers(null);
            }

        }
    }
}
