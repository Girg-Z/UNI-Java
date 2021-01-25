package it.univpm.ticketmaster.exception;

import org.json.JSONObject;

public abstract class ApiException extends Exception {
    private int statusCode;


    public ApiException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }
    public String getErrorJson(){
        JSONObject jo = new JSONObject();
        jo.put("status","Error");
        jo.put("message",this.getMessage());
        String str = jo.toString();
        return str;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}