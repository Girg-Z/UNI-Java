package it.univpm.ticketmaster.controller;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import it.univpm.ticketmaster.model.Event;
import it.univpm.ticketmaster.model.EventRepository;

@RestController
public class EventController {
    private final EventRepository eventRepository;


    public EventController() {
        this.eventRepository = EventRepository.getInstance();
    }
    
        
    
        @GetMapping("/stats")
        public ResponseEntity<String> stats() {
            final HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            List <Event> eventList = eventRepository.getAll();
            String[] countries = eventRepository.getCountryList();
            int[] countriesCounter = new int [countries.length];
            List<Map<String,Integer>> genreMapsList = new ArrayList<>();

            for (String ignored : countries) {
                genreMapsList.add(new HashMap<>());
            }

            for(int i=0; i<eventList.size() ;i++){
                final String genre = eventList.get(i).getKind();
                for(int j=0;j<countries.length;j++){
                    if(eventList.get(i).getCountry().equals(countries[j])) {
                        countriesCounter[j]++;
                        genreMapsList.get(j).merge(genre, 1, Integer::sum);
                    }
                }
            }

            JSONArray ja = new JSONArray();
            for(int i=0;i<countries.length;i++){
                JSONObject jo = new JSONObject();
                jo.put("country", countries[i]);
                jo.put("numberOfEvents", countriesCounter[i]);
                jo.put("eventsByGenre", genreMapsList.get(i));
                ja.put(jo);
            }
            String str = ja.toString();
            return new ResponseEntity<>(str, httpHeaders, HttpStatus.OK);
        }
}
