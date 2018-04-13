package com.example.reservationsystem.exception;

//import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.json.JSONObject;

@ResponseStatus(value = HttpStatus.NOT_FOUND)

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Object ID;//if -1 means no id involved
    private String errorMsg;
    private int statusCode;
    public ResourceNotFoundException(Object id, String str) {
        // TODO Auto-generated constructor stub
        super();
        this.setID(id);
        this.errorMsg = str;
    }
    public  ResourceNotFoundException( String str, int statusCode) {
        // TODO Auto-generated constructor stub
        super();

        this.errorMsg = str;
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public Object getID() {
        return ID;
    }
    public void setID(Object iD) {
        ID = iD;
    }

}
