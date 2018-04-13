package com.example.reservationsystem.resource;

import com.example.reservationsystem.exception.ResourceNotFoundException;
import com.example.reservationsystem.model.Flight;
import com.example.reservationsystem.model.Passenger;
import com.example.reservationsystem.model.Plane;
import com.example.reservationsystem.model.Reservation;
import com.example.reservationsystem.repository.PassengersRepository;
import com.example.reservationsystem.repository.ReservationRepository;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import com.oracle.javafx.jmx.json.JSONException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.json.*;

import javax.servlet.http.HttpServletResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.logging.XMLFormatter;

@RestController
@RequestMapping(value = "/passenger")
public class PassengersResource {

    @Autowired
    PassengersRepository passengersRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping(value = "/all")
    public List<Passenger> getAll() {
        return passengersRepository.findAll();
    }


    @PostMapping
    public Passenger createPassenger(@Valid
                                     @RequestParam(value = "firstname", required = true) String param2,
                                     @RequestParam(value = "lastname", required = true) String param3,
                                     @RequestParam(value = "age", required = true) int param4,
                                     @RequestParam(value = "gender", required = true) String param5,
                                     @RequestParam(value = "phone", required = true) String param6, HttpServletResponse response) throws ResourceNotFoundException {
        Passenger p = new Passenger();
        try {
            p.setFirstname(param2);
            p.setLastname(param3);
            p.setAge(param4);
            p.setGender(param5);
            p.setPhone(param6);
            passengersRepository.save(p);
        } catch (Exception e) {
        	e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException("Another passenger with the same number already exists.", 400);
        }
        p.removeCircle();
        return p;
    }

    @GetMapping(value = "/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> getPassengerById(@PathVariable(value = "id") String id) {

        Passenger p = passengersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sorry, the requested passenger with id " + id + " does not exist", 404));
        p.removeCircle();
        return  new ResponseEntity<>(passengerToJSONString(p),HttpStatus.OK);
    }


    @GetMapping(value = "/{id}", produces=MediaType.APPLICATION_JSON_VALUE, params = "xml")
    public @ResponseBody ResponseEntity<?> getPassengerById(@RequestParam Boolean xml, @PathVariable(value = "id") String id) {
     //   try {
            if (xml.equals(true)) {
                System.out.println("check here");
                Passenger  p = passengersRepository.findById(id).orElseThrow(
                        () -> new ResourceNotFoundException("Sorry, the requested passenger with id " + id + " does not exist", 404));
                p.removeCircle();

                try {
					return  new ResponseEntity<>(XML.toString(new JSONObject(passengerToJSONString(p))),HttpStatus.OK);
				} catch (org.json.JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }
            else
                throw new ResourceNotFoundException( "Parameter XML error", 400);
			return null;

//        }
//        catch (Exception e){
//            System.out.println("in catch");
//            //throw  new ResourceNotFoundException("Sorry, the requested passenger with id " + id + " does not exist", 404);
//        }
//        return null;

    }


    @PutMapping(value = "/{id}")
    public Passenger updatePassenger(@Valid
                                     @PathVariable(value = "id") String id,
                                     @RequestParam(value = "firstname", required = true) String param2,
                                     @RequestParam(value = "lastname", required = true) String param3,
                                     @RequestParam(value = "age", required = true) int param4,
                                     @RequestParam(value = "gender", required = true) String param5,
                                     @RequestParam(value = "phone", required = true) String param6, HttpServletResponse response) {
        Passenger passenger = passengersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sorry, the requested passenger with id " + id + " does not exist", 404));
        //Passenger updatedPassenger = new Passenger();
        try {
            passenger.setFirstname(param2);
            passenger.setLastname(param3);
            passenger.setAge(param4);
            passenger.setGender(param5);
            passenger.setPhone(param6);
            passengersRepository.save(passenger);
        } catch (Exception e) {
        	e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException("Another passenger with the same number already exists.", 400);
        }
        //Passenger updatedPassenger = passengersRepository.save(passenger);
        return passenger;
    }

    @DeleteMapping(value = "/{id}", produces = "application/xml")
    public ResponseEntity<?> deletePassenger(@PathVariable(value = "id") String id) {
        Passenger passenger = passengersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sorry, the requested passenger with id " + id + " does not exist", 404));

        Set<Reservation> res = passenger.getReservation();
        for (Reservation re : res) {
            Set<Flight> fls = re.getFlights();
            for (Flight fl : fls) {
                Set<Passenger> pas = fl.getPassengers();
                pas.remove(passenger);
                fl.setPassengers(pas);
                fl.setSeatsleft(fl.getSeatsleft() + 1);
            }
            re.setPassenger(null);
            reservationRepository.delete(re);
        }

        passengersRepository.delete(passenger);

        //return ResponseEntity.ok().build();
        throw new ResourceNotFoundException("Passenger with id " + id + " is deleted successfully", 200);
    }

    public String passengerToJSONString(Passenger p){

        JSONObject result = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject reservationsJSON = new JSONObject();
        JSONObject arr[] = null;
        try {
            System.out.println("inside passengerToJSONString() try");
            result.put("passenger", fields);

            fields.put("id", "" + p.getId());
            fields.put("firstname", p.getFirstname());
            fields.put("lastname", p.getLastname());
            fields.put("age", "" + p.getAge());
            fields.put("gender", p.getGender());
            fields.put("phone", p.getPhone());


            int i = 0;
            Set<Reservation> reservations = p.getReservation();
            arr = new JSONObject[reservations.size()];
            System.out.println("reservations size() " + reservations.size());

            for (Reservation reservation : reservations) {
                System.out.println("Reservation");
                arr[i++] = reservationToJSONString(reservation);
                System.out.println(reservation.getReservationnumber());
                System.out.println(reservation.getPrice());
            }
            reservationsJSON.put("reservation", arr);
            fields.put("reservations", reservationsJSON);

        } catch (JSONException e) {
            System.out.println("inside passengerToJSONString() catch");

            e.printStackTrace();
        }
        return result.toString();
        }



    public JSONObject reservationToJSONString(Reservation reservation) {

        JSONObject result = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
        int i = 0;
                double price = 0;

        System.out.println("inside reservationToJSONString()");
        System.out.println("getReservation() flight size " + reservation.getFlights().size());

        try {
            result.put("reservationNumber", "" + reservation.getReservationnumber());

            for (Flight flight : reservation.getFlights()) {
                arr[i++] = flightToJSONString(flight);
                price += flight.getPrice();
            }
            result.put("price", "" + price);
            flightsJSON.put("flight", arr);
            result.put("flights", flightsJSON);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject flightToJSONString(Flight flight) {
        JSONObject json = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        System.out.println("inside flightToJSONString()");

        try {
            System.out.println("inside flightToJSONString() try 1");
            //json.put("flight", flightJSON);
            flightJSON.put("number", flight.getFlightnumber());
            flightJSON.put("price", "" + flight.getPrice());
            flightJSON.put("origin", flight.getOrigin());
            System.out.println("inside flightToJSONString() try 2");
            flightJSON.put("to", flight.getOrigin());
            flightJSON.put("departureTime", flight.getDeparturetime());
            flightJSON.put("arrivalTime", flight.getArrivaltime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            System.out.println("inside flightToJSONString() catch");
            e.printStackTrace();
        }
        System.out.println("inside flightToJSONString() returning");
        return flightJSON;
    }

    public JSONObject planeToJSONString(Plane plane) {
        JSONObject planeJSON = new JSONObject();

        try {
            planeJSON.put("capacity", "" + plane.getCapacity());
            planeJSON.put("model", plane.getModel());
            planeJSON.put("manufacturer", plane.getManufacturer());
            planeJSON.put("year", "" + plane.getYear());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planeJSON;


    }
}
