package com.example.reservationsystem.exception;


import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@ControllerAdvice
public class ResourceNotFoundController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @RequestMapping(produces = "application/json")
    public Object handleDeleteError(ResourceNotFoundException e){
        GenericResponse badRequestReturn = null;
        if(e.getID()!=null){
            badRequestReturn = new GenericResponse(HttpServletResponse.SC_NOT_FOUND,e.getErrorMsg()
                    +" "+e.getID()+" does not exist ");
        }else{
            badRequestReturn = new GenericResponse(e.getStatusCode(),e.getErrorMsg());
        }

        return 	badRequestReturn;
    }
}