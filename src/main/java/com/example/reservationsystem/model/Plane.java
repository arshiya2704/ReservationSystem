package com.example.reservationsystem.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

@Embeddable
@XmlRootElement
public class Plane {
    //@Column(name= "capacity")
    private int capacity;
    //@Column(name= "model")
    private String model;
    //@Column(name= "manufacturer")
    private String manufacturer;
    //@Column(name= "year")
    private int year;

    public Plane(){
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
