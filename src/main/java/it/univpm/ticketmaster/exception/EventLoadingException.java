package it.univpm.ticketmaster.exception;

public class EventLoadingException extends Exception {
    private String msg;
    
    public  EventLoadingException(String msg) {
        super(msg);
    }



    
}
