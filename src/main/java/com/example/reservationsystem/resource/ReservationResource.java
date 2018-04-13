package com.example.reservationsystem.resource;

import com.example.reservationsystem.exception.ResourceNotFoundException;
import com.example.reservationsystem.model.Flight;
import com.example.reservationsystem.model.Passenger;
import com.example.reservationsystem.model.Plane;
import com.example.reservationsystem.model.Reservation;
import com.example.reservationsystem.repository.FlightsRepository;
import com.example.reservationsystem.repository.PassengersRepository;
import com.example.reservationsystem.repository.ReservationRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/reservation")
public class ReservationResource {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PassengersRepository passengersRepository;

    @Autowired
    FlightsRepository flightsRepository;

    String format = "yyyy-MM-dd-HH";

    private boolean overlapping(Set<Flight> addedFlights, Set<Flight> removedFlights, Set<Flight> reservatedFlights) throws ParseException{
        //addedFlights is the list you want to add into a exited reservation or a new reservation
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        PriorityQueue<Flight> queue = new PriorityQueue<>(new Comparator<Flight>(){
            public int compare(Flight a, Flight b){
                return a.getDeparturetime().compareTo(b.getDeparturetime());
            }
        });
        //addedFlights must be not null
        for(Flight flight: addedFlights){
            queue.add(flight);
        }
        //reservatedFlights may be null
        if(reservatedFlights!=null){
            for(Flight flight:reservatedFlights){
                //removedFlights may be null
                if(removedFlights==null || !removedFlights.contains(flight)){
                    queue.add(flight);
                }
            }
        }
        Flight previousFlight = null;
        for(Flight flight: queue){
            if(previousFlight!=null){
                if(!(sdf.parse(previousFlight.getArrivaltime())).before(sdf.parse(flight.getDeparturetime()))){
                    //System.out.println("times are overlap");
                    return false;
                }
            }
            previousFlight = flight;
        }
        return true;
    }

//    @GetMapping(value = "/all")
//    public List<Reservation> getAll(){
//        return reservationRepository.findAll();
//    }
//
//
//    @PostMapping(produces = "application/xml")
//    public Reservation makeReservation (@Valid
//                               @RequestParam(value = "passengerId",required = true)String param2,
//                               @RequestParam(value = "flightLists",required = true)String[] param3,
//                               HttpServletResponse response) throws ParseException, ResourceNotFoundException {
//        Reservation r= new Reservation();
//        Passenger p = passengersRepository.findById(param2).orElseThrow(()-> new ResourceNotFoundException("Sorry, the requested passenger with id "+ param2 +" does not exist",404));
//        //try {
//            if (param3.length == 0) {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                throw new ResourceNotFoundException("Passenger reserve no flights", 400);
//            }
//            int price = 0;
//            Set<Flight> f1 = new HashSet<Flight>();
//            Set<Flight> flights = new HashSet<Flight>();
//        for(String f : param3){
//            if(flightsRepository.existsById(f)){
//                f1.add(flightsRepository.findById(f).orElseThrow(()-> new ResourceNotFoundException("check1",404)));
//            }
//        }
//
//            if (f1 == null || f1.size()!=param3.length) {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                throw new ResourceNotFoundException("Some flight in your reservation is not existing", 404);
//            }
//            //DONE check overlapping
//            if(!overlapping(f1,null,null)){
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                throw new ResourceNotFoundException("Flights are overlapping",400);
//            }
//
//            //DONE check if the data is duplicated
//            for (String flightNum : param3) {
//                Flight flight = flightsRepository.findById(flightNum).orElseThrow(()-> new ResourceNotFoundException("check2",404));
//                Set<Passenger> passengers = flight.getPassengers();
//
//                //bad request
//                if (passengers.contains(p)) {
//                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                    throw new ResourceNotFoundException("You already in one flight of the reservation", 400); //bad request
//                } else {
//                    int seat = flight.getSeatsleft();
//                    System.out.println("seat" + seat);
//                    if (seat == 0) {
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        throw new ResourceNotFoundException("Some flight in your reservation is full, cannot reservate for you", 400); //DONE bad request
//                    } else {
//                        seat--;
//                    }
//                    flight.setSeatsleft(seat);
//                    passengers.add(p);
//                }
//                flight.setPassengers(passengers);
//
//                price += flight.getPrice();
//                flights.add(flight);
//
//            }
//        r.setPrice(price);
//        r.setFlights(flights);
//        r.setPassenger(p);
//        reservationRepository.save(r);
//        return r;
//    }

    @RequestMapping(
            value = "",
            params= {"passengerId", "flightLists"},
            method = RequestMethod.POST,
            produces = "application/xml")
    public @ResponseBody Object createReservation(
            @RequestParam("passengerId") String passengerId,
            @RequestParam("flightLists") String[] flightLists,
            HttpServletResponse response) throws ParseException{

        Passenger passenger = passengersRepository.findById(passengerId).orElseThrow(()-> new ResourceNotFoundException("Passenger does not exist",404));
        Reservation reservation = new Reservation();
        return storeReservation(reservation, passenger, flightLists, response);
        //return reservationRepository.findOne(reservation.getOrderNumber());
    }

    private Object storeReservation(Reservation reservation, Passenger passenger,
                                    String[] flightLists, HttpServletResponse response) throws ParseException{
        if(flightLists.length==0){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException("Passenger reserve no flights",400);
        }
        int price = 0;
        Set<Flight> f1 = new HashSet<Flight>();
        Set<Flight> flights = new HashSet<Flight>();

        for(String f : flightLists){
            if(flightsRepository.findById(f)!=null){
                f1.add(flightsRepository.findById(f).orElseThrow(()-> new ResourceNotFoundException("Flight does not exist",404)));
            }
        }
        if(f1 == null || f1.size()!=flightLists.length){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new ResourceNotFoundException("Some flight in your reservation is not existing",404);
        }
        //DONE check overlapping
        if(!overlapping(f1,null,null)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException("Flights are overlapping",400);
        }

        //DONE check if the data is duplicated
        for(String flightNum : flightLists){
            Flight flight = flightsRepository.findById(flightNum).orElseThrow(()-> new ResourceNotFoundException("dun know2",404));
            Set<Passenger> passengers = flight.getPassengers();

            //bad request
            if(passengers.contains(passenger)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new ResourceNotFoundException("You already in one flight of the reservation", 400); //bad request
            } else {
                int seat = flight.getSeatsleft();
                System.out.println("seat" + seat);
                if(seat == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new ResourceNotFoundException("Some flight in your reservation is full, cannot reservate for you",400); //DONE bad request
                } else {
                    seat--;
                }
                flight.setSeatsleft(seat);
                passengers.add(passenger);
            }
            flight.setPassengers(passengers);

            price += flight.getPrice();
            flights.add(flight);
        }

        reservation.setPrice(price);
        reservation.setFlights(flights);
        reservation.setPassenger(passenger);

        reservationRepository.save(reservation);
        reservation.removeCircle();
        return reservation;
    }

    @GetMapping(value = "/{reservationnumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> getReservationByNumber(@PathVariable(value = "reservationnumber") String reservationnumber){
        Reservation r= reservationRepository.findById(reservationnumber).orElseThrow(() -> new ResourceNotFoundException("Reservation with number " + reservationnumber + " does not exist",404));
        System.out.print("inside json get reservations"+r);
        r.removeCircle();
        return  new ResponseEntity<>(reservationToJSONString(r), HttpStatus.OK);
    }

    @GetMapping(value = "/{reservationnumber}",produces =  "application/xml" , params = "xml")
    public @ResponseBody Reservation getReservationByNumber(@RequestParam Boolean xml, @PathVariable(value = "reservationnumber") String reservationnumber){
        if(xml.equals(true)){
            Reservation r= reservationRepository.findById(reservationnumber).orElseThrow(
                    () -> new ResourceNotFoundException("Reservation with number " + reservationnumber + " does not exist",404));
            r.removeCircle();
            return r;
        }
        else{
            throw new ResourceNotFoundException("Parameter XML error", 400);
        }
    }

    @GetMapping(produces = "application/xml")
    public Set<Reservation> searchReservation(@RequestParam(value = "passengerId", defaultValue = "p1", required = false) String param2,
                                       @RequestParam(value = "origin", required = false,defaultValue = "") String param3,
                                       @RequestParam(value = "destination", required = false,defaultValue = "") String param4,
                                       @RequestParam(value = "flightNumber", defaultValue = "flightNumber",required = false) String param5,
                                       HttpServletResponse response
    ){
        Passenger pass = null;
        Set<Reservation> reservations = new HashSet<>();


            List<Object> filters=reservationRepository.findPassengerById(param2,param5);
            Iterator itr = filters.iterator();
            int i=0;
            Set<String> temppassids=new HashSet<>();
            Set<String> tempflightids=new HashSet<>();
            while(itr.hasNext()) {
                Object[] arrObj = (Object[])itr.next();
                for(Object obj:arrObj) {
                    i+=1;
                    System.out.println(String.valueOf(obj));
                    if(i%2==1) tempflightids.add(String.valueOf(obj));
                    else temppassids.add(obj.toString());
                }
            }

            for (String passid:temppassids){
                reservations.addAll(passengersRepository.findById(passid).get().getReservation());
            }
            for (Reservation res : reservations) {
                System.out.println("fnocheck"+res.getFlights().size());
            }
            if(!param5.equals("flightNumber")) {
                for (Reservation res : reservations) {
                    res.getFlights().removeIf(flight -> !flight.getFlightnumber().equals(param5) );
                }
            }
            if(!param3.equals("")) {
                for (Reservation res : reservations) {
                    res.getFlights().removeIf(flight -> !flight.getOrigin().equals(param3));
                }
            }
            if(!param4.equals("")) {
                for (Reservation res : reservations) {
                    res.getFlights().removeIf(flight -> !flight.getDestination().equals(param4) );
                }
            }

            reservations.removeIf(res-> res.getFlights().size()==0 );
            for (Reservation res:reservations){
                res.removeCircle();
            }


       if(reservations.size()==0)
            throw  new ResourceNotFoundException("No reservation",404);
        else
           return reservations;






    }



    @PostMapping(value = "/{reservationNumber}")
    public Reservation updateReservation(@Valid
                                      @PathVariable(value = "reservationNumber")String reservationNumber,
                                      @RequestParam(value = "flightsAdded",required = false)String[] param2,
                                      @RequestParam(value = "flightsRemoved",required = false)String[] param3,
                                      HttpServletResponse response) throws ParseException{
        Reservation reservation = reservationRepository.findById(reservationNumber).orElseThrow(()-> new ResourceNotFoundException("Sorry, the requested reservation with number "+ reservationNumber +" does not exist",404));

        if(param2 == null&&param3 == null){
            return reservation;
        }

        Set<Flight> fAdd = new HashSet<Flight>();
        Set<Flight> fRem = new HashSet<Flight>();
        Set<Flight> flights = reservation.getFlights();
        Passenger passenger = reservation.getPassenger();
        double price = reservation.getPrice();
        if(param2 != null){
            for(String f : param2){
                if(flightsRepository.findById(f)!=null){
                    fAdd.add(flightsRepository.findById(f).orElseThrow(()-> new ResourceNotFoundException("Flights do not exist",404)));
                }
            }
            if(fAdd.size()!=param2.length){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new ResourceNotFoundException("some new flights you want to add are not existing",404);
            }
        }
        if(param3 != null){
            for(String f : param3){
                if(flightsRepository.findById(f)!=null){
                    fRem.add(flightsRepository.findById(f).orElseThrow(()-> new ResourceNotFoundException("Checkagain1",400)));
                }
            }
        }

        if(fAdd != null && !overlapping(fAdd, fRem, flights)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ResourceNotFoundException("Existing overlapped flights",404);
        }

        if(param3 != null){
            for(String flightNum : param3){
                Flight flight = flightsRepository.findById(flightNum).orElseThrow(()-> new ResourceNotFoundException("Checkagain2",400));
                Set<Passenger> passengers = flight.getPassengers();

                if(!passengers.contains(passenger)){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new ResourceNotFoundException("passenger not in flight " + flightNum,400); // DONE bad request
                } else {
                    int seat = flight.getSeatsleft();
                    System.out.println("seat" + seat);
                    seat++;
                    flight.setSeatsleft(seat);
                    passengers.remove(passenger);
                }
                flight.setPassengers(passengers);

                price -= flight.getPrice();
                flights.remove(flight);
            }
        }
        if(param2 != null) {
            for(String flightNum : param2){
                Flight flight = flightsRepository.findById(flightNum).orElseThrow(()-> new ResourceNotFoundException("Checkagain3",400));
                Set<Passenger> passengers = flight.getPassengers();

                if(passengers.contains(passenger)){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new ResourceNotFoundException("passenger already in flight " + flightNum,400); // DONE bad request
                } else {
                    int seat = flight.getSeatsleft();
                    System.out.println("seat" + seat);
                    if(seat == 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        throw new ResourceNotFoundException("Flight " + flightNum + " is full",400);//DONE bad request
                    } else {
                        seat--;
                    }
                    flight.setSeatsleft(seat);
                    passengers.add(passenger);
                }
                flight.setPassengers(passengers);

                price += flight.getPrice();
                flights.add(flight);
            }
        }
        reservation.setPrice(price);
        reservation.setFlights(flights);
        reservation.setPassenger(passenger);
        Reservation updatedReservation = reservationRepository.save(reservation);
        updatedReservation.removeCircle();
        return updatedReservation;
    }



    @DeleteMapping(value = "/{reservationNumber}",produces = "application/xml")
    public ResponseEntity<?> cancelReservation (@PathVariable(value = "reservationNumber")String reservationNumber){
        Reservation reservation = reservationRepository.findById(reservationNumber).orElseThrow(()-> new ResourceNotFoundException("Reservation with number "+ reservationNumber + " does not exist",404));
        Set<Reservation> res = reservation.getPassenger().getReservation();
        res.remove(reservation);

        Set<Flight> flights = reservation.getFlights();
        for(Flight f : flights) {
            Set<Passenger> pas = f.getPassengers();
            pas.remove(reservation.getPassenger());
            f.setPassengers(pas);
            f.setSeatsleft(f.getSeatsleft()+1);
        }
        reservation.setPassenger(null);
        reservationRepository.delete(reservation);
        //return ResponseEntity.ok().build();
        throw new ResourceNotFoundException("Reservation with number " + reservationNumber +" is deleted successfully",200);
    }

    public String reservationToJSONString(Reservation reservation){

        JSONObject result = new JSONObject();
        JSONObject container = new JSONObject();
        JSONObject passengerJSON = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
        int i = 0, price = 0;
        Passenger passenger = reservation.getPassenger();

        System.out.println("inside reservationToJSONString()");
        System.out.println("getReservation() flight size "+reservation.getFlights().size());

        try {
            result.put("reservation", container);

            container.put("reservationnumber", ""+reservation.getReservationnumber());

            passengerJSON.put("id", ""+passenger.getId());
            passengerJSON.put("firstname", passenger.getFirstname());
            passengerJSON.put("lastname", passenger.getLastname());
            passengerJSON.put("age", ""+passenger.getAge());
            passengerJSON.put("gender", passenger.getGender());
            passengerJSON.put("phone", passenger.getPhone());
            container.put("passenger", passengerJSON);

            for(Flight flight : reservation.getFlights()){
                arr[i++] =  flightToJSONString(flight);
                price += flight.getPrice();
                //flight.getPassengers().add(passenger);
            }
            container.put("price", ""+price);
            flightsJSON.put("flight", arr);
            container.put("flights", flightsJSON);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.toString();
    }

    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightJSON = new JSONObject();
        System.out.println("inside flightToJSONString()");

        try {
            System.out.println("inside flightToJSONString() try 1");
            flightJSON.put("number", flight.getFlightnumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getOrigin());
            System.out.println("inside flightToJSONString() try 2");
            flightJSON.put("to", flight.getOrigin());
            flightJSON.put("departureTime", flight.getDeparturetime());
            flightJSON.put("arrivalTime", flight.getArrivaltime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", ""+flight.getSeatsleft());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            System.out.println("inside flightToJSONString() catch");
            e.printStackTrace();
        }
        System.out.println("inside flightToJSONString() retruning");
        return flightJSON;
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

