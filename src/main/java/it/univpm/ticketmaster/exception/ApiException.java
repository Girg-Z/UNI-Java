package it.univpm.ticketmaster.exception;

import org.json.JSONObject;

/**
 * Main class of all the Api exception
 */
public abstract class ApiException extends Exception {
    private final int statusCode;


    public ApiException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    /**
     * @return The error message in Json format
     */
    public String getErrorJson(){
        JSONObject jo = new JSONObject();
        jo.put("status","Error");
        jo.put("message",this.getMessage());
        String str = jo.toString();
        return str;
    }

    /**
     * @return The Http status code
     */
    public int getStatusCode() {
        return this.statusCode;
    }

}