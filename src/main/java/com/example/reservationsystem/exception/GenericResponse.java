package com.example.reservationsystem.exception;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "GenericResponse")
public class GenericResponse{
    private BadRequest BADREQUEST;

    @JsonGetter("BadRequest")
    public BadRequest getBADREQUEST() {
        return BADREQUEST;
    }


    public void setBADREQUEST(BadRequest bADREQUEST) {
        BADREQUEST = bADREQUEST;
    }


    public GenericResponse(int statusCode, String str) {
        this.BADREQUEST = new BadRequest(statusCode, str);
    }
}
