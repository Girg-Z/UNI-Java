package it.univpm.ticketmaster.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import it.univpm.ticketmaster.model.Event;

@RestController
public class ApiController {

    @GetMapping("/metadata")
    public ResponseEntity<String> metadata() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(Event.getMetaData(),httpHeaders, HttpStatus.OK);
    }
}