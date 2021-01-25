package it.univpm.ticketmaster.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.univpm.ticketmaster.exception.FilterException;
import it.univpm.ticketmaster.model.Event;

@RestController
public class ApiController {
    private EventController eventController;

	public ApiController(){
        this.eventController=new EventController();
    }

    @GetMapping("/metadata")
    public ResponseEntity<String> metadata() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(Event.getMetaData(),httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<String> stats(@RequestParam(required = false) String filter) throws FilterException {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try{
            return new ResponseEntity<String>(eventController.stats(filter), httpHeaders, HttpStatus.OK);
        } catch (FilterException filterException){
            return new ResponseEntity<String>(filterException.getErrorJson(), httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    
    @GetMapping("/events")
    public ResponseEntity<String> events() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(eventController.events(), httpHeaders, HttpStatus.OK);
    }

   
}