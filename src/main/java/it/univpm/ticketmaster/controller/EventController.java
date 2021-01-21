package it.univpm.ticketmaster.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
            getDatesArray();

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

    private Date[] getDatesArray() {
        // Find first and last date
        Date first = null;
        Date last = null; 
        for (Event event : eventRepository.getAll()) {
            if (first == null){
                first = event.getStartDateTime();
                last = event.getEndDateTime();
            }
            else {
                if (event.getStartDateTime().before(first)){
                    first = event.getStartDateTime();
                }
                if (event.getStartDateTime().after(last)){
                    last = event.getStartDateTime();
                }
            }
        }

        long dateDiff = last.getTime() - first.getTime();
        int daysNumber = (int) TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
        Date[] datesArray = new Date[daysNumber];
        datesArray[0] = first;
        for (int i = 1; i < daysNumber; i++) {
            datesArray[i] = Date.from(datesArray[i-1].toInstant().plus(1, ChronoUnit.DAYS));
        }
        System.out.println(Arrays.toString(datesArray));
        return null;        
    }
    
}
