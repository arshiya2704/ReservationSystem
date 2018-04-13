package com.example.reservationsystem;

import static org.junit.Assert.*;
import org.json.JSONObject;
import org.junit.Test;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class ReservationSystemApplicationTests{
	@Test
	public void getPassenger() {
		
		try {
			// Retrieve details of passenger 1
			// We have saved first passenger with below details for testing purpose
			HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8080/passenger/8a8080af62ae47e40162ae82d1220003")
					  .header("accept", "application/json")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("passenger");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			System.out.println("jsonResponse firstname"+passenger.get("firstname"));
			
			assertEquals("Kshitij", passenger.get("firstname"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void getPassengerXMLError() {
		
		try {
			// Retrieve details of passenger -1
			// No such passenger possible, so we should get error response
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8080/passenger/123?xml=true")
					  .header("accept", "application/json")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("BadRequest");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			
			assertEquals("404", passenger.get("code"));
			
			
		} catch (Exception e) {
			System.out.println("inside getPassengerXML catch");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void deleteReservation() {
		
		try {
			// Delete details of reservation -1
			// No such reservation possible, so we should get error response
			
			HttpResponse<JsonNode> jsonResponse = Unirest.delete("http://localhost:8080/reservation/123")
					  .header("accept", "application/json")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("BadRequest");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			
			assertEquals("404", passenger.get("code"));
			
			
		} catch (Exception e) {
			System.out.println("inside getPassengerXML catch");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void deleteFlight() {
		
		try {
			// Delete details of flight -1
			// No such flight possible, so we should get error response
			
			HttpResponse<JsonNode> jsonResponse = Unirest.delete("http://localhost:8089/airline/A234")
					  .header("accept", "application/json")
					  //.queryString("passengerId", 1)
					  //.field("parameter", "value")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("BadRequest");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			
			assertEquals("200", passenger.get("code"));
			
			
		} catch (Exception e) {
			System.out.println("inside getPassengerXML catch");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void getFlightXML() {
		
		try {
			// Retrieve details of flight -2
			// No such flight possible, so we should get error response
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8089/flight/123?xml=true")
					  .header("accept", "application/json")
					  //.queryString("passengerId", 1)
					  //.field("parameter", "value")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("BadRequest");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			
			assertEquals("404", passenger.get("code"));
			
			
		} catch (Exception e) {
			System.out.println("inside getPassengerXML catch");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void deletePassenger() {
		
		try {
			// Delete details of reservation -1
			// No such reservation possible, so we should get error response
			
			HttpResponse<JsonNode> jsonResponse = Unirest.delete("http://localhost:8080/passenger/777")
					  .header("accept", "application/json")
					  .asJson();
			
			JSONObject res = jsonResponse.getBody().getObject();
			JSONObject passenger = res.getJSONObject("BadRequest");
					
			System.out.println("jsonResponse "+jsonResponse.getBody().toString());
			
			assertEquals("404", passenger.get("code"));
			
			
		} catch (Exception e) {
			System.out.println("inside getPassengerXML catch");
			e.printStackTrace();
		}
		
	}
	
	
	
		
	}