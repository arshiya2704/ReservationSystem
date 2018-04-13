package com.example.reservationsystem.resource;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.reservationsystem.exception.ResourceNotFoundException;
import com.example.reservationsystem.model.Passenger;
import com.example.reservationsystem.model.Plane;
import com.example.reservationsystem.model.Flight;

import com.example.reservationsystem.model.Reservation;
import com.example.reservationsystem.repository.FlightsRepository;
import com.example.reservationsystem.repository.ReservationRepository;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/flight")
public class FlightsResource {

    @Autowired
    private FlightsRepository flightRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    String format = "yyyy-MM-dd-HH";

    private boolean overlapping(Flight newFlight, Flight oldFlights, Set<Flight> relatedFlights) throws ParseException{
        //addedFlights is the list you want to add into a exited reservation or a new reservation
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        //Date dateD = sdf.parse(departureTime);
        PriorityQueue<Flight> queue = new PriorityQueue<>(new Comparator<Flight>(){
            public int compare(Flight a, Flight b){
                return a.getDeparturetime().compareTo(b.getDeparturetime());
            }
        });
        //addedFlights must be not null

        queue.add(newFlight);

        //reservatedFlights may be null
        if(relatedFlights!=null){
            relatedFlights.remove(oldFlights);
        }
        queue.addAll(relatedFlights);
        Flight previousFlight = null;
        for(Flight flight: queue){
            if(previousFlight!=null){
                if(!(sdf.parse(previousFlight.getArrivaltime()).before(sdf.parse(flight.getArrivaltime())))){
                    //System.out.println("times are overlap");
                    return false;
                }
            }
            previousFlight = flight;
        }
        return true;
    }

    @GetMapping(value = "/{flightnumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> getFlightById(@PathVariable(value = "flightnumber") String flightnumber){
        Flight f= flightRepository.findById(flightnumber).orElseThrow(() -> new ResourceNotFoundException("Sorry, the requested flight with number "+ flightnumber +" does not exist",404));
        f.removeCircle();
        return new ResponseEntity<>(flightToJSONString(f), HttpStatus.OK);
    }

    @GetMapping(value = "/{flightnumber}",produces = MediaType.APPLICATION_JSON_VALUE, params = "xml")
    public @ResponseBody ResponseEntity<?> getFlightById(@RequestParam Boolean xml, @PathVariable(value = "flightnumber") String flightnumber){
        if(xml.equals(true)){
            Flight f= flightRepository.findById(flightnumber).orElseThrow(() -> new ResourceNotFoundException("Sorry, the requested flight with number "+ flightnumber +" does not exist",404));
       f.removeCircle();
            try {
				//return  new ResponseEntity<>(XML.toString(flightToJSONString(f)),HttpStatus.OK);
				return  new ResponseEntity<>(XML.toString(new JSONObject(flightToJSONString(f))),HttpStatus.OK);
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else{
            throw new ResourceNotFoundException("Parameter XML error", 400);
        }
		return null;
    }

    @PostMapping(value = "/{flightnumber}", produces = "application/xml")
    public Flight updateFlight(@Valid
                                   @PathVariable(value = "flightnumber",required = true) String param1,
                               @RequestParam(value = "price",required = true) double param2,
                               @RequestParam(value = "origin",required = true) String param3,
                               @RequestParam(value = "destination",required = true) String param4,
                               @RequestParam(value = "departuretime",required = true) String param5,
                               @RequestParam(value = "arrivaltime",required = true) String param6,
                               @RequestParam(value = "description",required = true) String param7,
                               @RequestParam(value = "capacity",required = true) int param8,
                               @RequestParam(value = "model",required = true) String param9,
                               @RequestParam(value = "manufacturer",required = true) String param10,
                               @RequestParam(value = "year",required = true) int param11,
                               HttpServletResponse response) throws ParseException {
//        SimpleDateFormat format = new SimpleDateFormat("yy-mm-dd-hh");
//        Date arrivalTime = format.parse(param6);
//        Date departureTime = format.parse(param5);
        Plane p = new Plane();
       Flight f=null;

        if(flightRepository.findById(param1)!=null) {
             f = flightRepository.findById(param1).orElse(new Flight());

        }

        //try {
            f.setFlightnumber(param1);
            f.setSeatsleft(param8);
            p.setCapacity(param8);
            p.setModel(param9);
            p.setManufacturer(param10);
            p.setYear(param11);


        Flight newFlight = new Flight();
        newFlight.setDeparturetime(param5);
        newFlight.setArrivaltime(param6);
        //DONE overlapping check error code 400
        Set<Flight> relatedFlight = new HashSet<Flight>();
        for(Reservation re: reservationRepository.findAll()){
            if(re.getFlights().contains(f)){
                relatedFlight.addAll(re.getFlights());
            }
        }
        if(!overlapping(newFlight,f,relatedFlight)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException( "Overlapping occur in reservation after update, refuse to update", 400);
        }
        //flight.setPlane(plane);

        System.out.println("here plane capacity");
        //int x= f.getPlane().getCapacity();
        System.out.println(param8);
        if(f.getPlane() != null && f.getPlane().getCapacity() != param8){
            System.out.println("in loop");
            int leftS = f.getSeatsleft();
            leftS -= (f.getPlane().getCapacity() - param8);
            if(leftS >= 0){
                System.out.println("success");
                f.setSeatsleft(leftS);
            } else {
                //DONE exception: left seat is lower than zero
                System.out.println("fail");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new ResourceNotFoundException( "Left seat is lower than zero", 400);
            }
        }
        System.out.println("skipped condition");
        f.setPlane(p);
        f.setPrice(param2);
        f.setOrigin(param3);
        f.setDestination(param4);
        f.setDeparturetime(param5);
        f.setArrivaltime(param6);
        f.setDescription(param7);

            flightRepository.save(f);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            throw new ResourceNotFoundException("check.", 400);
//        }
        f.removeCircle();
        return f;
    }

    public String flightToJSONString(Flight flight){
        JSONObject json = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        JSONObject passenger = new JSONObject();
        JSONObject arr[] = new JSONObject[flight.getPassengers().size()];
        int i = 0;

        try {
            json.put("flight", flightJSON);
            flightJSON.put("flightNumber", flight.getFlightnumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getOrigin());
            flightJSON.put("to", flight.getOrigin());
            flightJSON.put("departureTime", flight.getDeparturetime());
            flightJSON.put("arrivalTime", flight.getArrivaltime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", ""+flight.getSeatsleft());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
            flightJSON.put("passengers", passenger);


            for(Passenger pass : flight.getPassengers()){

                arr[i++] = passengerToJSONString(pass);
            }
            passenger.put("passenger", arr);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private JSONObject passengerToJSONString(Passenger passenger) {
        JSONObject json = new JSONObject();
        System.out.println("inside passengerToJSONString");

        try {
            json.put("id", ""+passenger.getId());
            json.put("firstname", ""+passenger.getFirstname());
            json.put("lastname", ""+passenger.getLastname());
            json.put("age", ""+passenger.getAge());
            json.put("gender", ""+passenger.getGender());
            json.put("phone", ""+passenger.getPhone());

            System.out.println("Firstname "+passenger.getFirstname());
            System.out.println("Id "+passenger.getId());
            System.out.println("last "+passenger.getLastname());
            System.out.println("age "+passenger.getAge());
            System.out.println("gender "+passenger.getGender());
            System.out.println("phone "+passenger.getPhone());

        } catch (JSONException e) {
            System.out.println("inside passengerToJSONString() catch");
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject planeToJSONString(Plane plane){
        JSONObject planeJSON = new JSONObject();

        try {
            planeJSON.put("capacity", ""+plane.getCapacity());
            planeJSON.put("model", plane.getModel());
            planeJSON.put("manufacturer", plane.getManufacturer());
            planeJSON.put("year", ""+plane.getYear());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planeJSON;
    }
}

