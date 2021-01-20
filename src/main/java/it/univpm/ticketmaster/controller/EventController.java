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
    private  EventRepository eventRepository;


    public EventController() {
        this.eventRepository = EventRepository.getInstance();
        this.eventRepository.loadData();
    }
    
        
    
        @GetMapping("/stats")
        public ResponseEntity<String> numeroeventi() {
            final HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            List <Event> eventList = eventRepository.getAll();
            String[] countries = eventRepository.getCountryList();
            int[] coutriesCouter = new int [countries.length];
            boolean countryAlreadyInArray = false;
            int coutryIndex = 0;

            List<Map<String,Integer>> genreMapsList = new ArrayList<Map<String,Integer>>();

            for(int i=0; i<eventList.size() ;i++){
                final String genre = eventList.get(i).getKind();
                for(int j=0;j<countries.length;j++){
                    if(eventList.get(i).getCountry().equals(countries[j])) {
                        countryAlreadyInArray = true;
                        coutryIndex = j;
                    }
                   

                }
                if (countryAlreadyInArray){
                    coutriesCouter[coutryIndex]++;
                } else{
                    int x=0;
                    while(countries[x] != null){
                        x++;
                    }
                    coutryIndex = x;
                    countries[x]=eventList.get(i).getCountry();
                    coutriesCouter[x]++;
                    genreMapsList.add(new HashMap<String, Integer>());
                }
                countryAlreadyInArray = false;

                genreMapsList.get(coutryIndex).merge(genre, 1, Integer::sum);
            }

            JSONArray ja = new JSONArray();
            for(int i=0;i<countries.length;i++){
                JSONObject jo = new JSONObject();
                jo.put("country", countries[i]);
                jo.put("numerOfEvents", coutriesCouter[i]);
                jo.put("test", genreMapsList.get(i));
                ja.put(jo);
            }
            String str = ja.toString();
            return new ResponseEntity<String>(str,httpHeaders, HttpStatus.OK);
        } 

/*
        @GetMapping("/stats")
        public ResponseEntity<String> numeroeventi() {
            final HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            List <Event> eventList = eventRepository.getAll();
            String[] countries= eventRepository.getCountryList();
            int[] coutriesCouter=new int [countries.length];
            int countryAlreadyInArray=0;
            int controller1=0;
            for(int i=0; i<eventList.size() ;i++){
                for(int j=0;j<countries.length;j++){
                    if(eventList.get(i).getCountry().equals(countries[j])) {
                        countryAlreadyInArray=1;
                        controller1=j;
                    }
                   

                }
                if (countryAlreadyInArray==1){
                    coutriesCouter[controller1]++;
                } else{
                    int x=0;
                    while(countries[x] != null){
                        x++;
                    }
                    countries[x]=eventList.get(i).getCountry();
                    coutriesCouter[x]++;
                }
                countryAlreadyInArray=0;
            }
            JSONArray ja = new JSONArray();
            for(int i=0;i<countries.length;i++){
                JSONObject jo = new JSONObject();
                jo.put("country", countries[i]);
                jo.put("numerOfEvents", coutriesCouter[i]);
                ja.put(jo);
            }
            String str = ja.toString();
            return new ResponseEntity<String>(str,httpHeaders, HttpStatus.OK);
        } 

        @GetMapping("/stats1")
        public void reportkinds(){
            final HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            List <Event> eventList = eventRepository.getAll();
            List<Map<String,Integer>> genreMapsList = new ArrayList<Map<String,Integer>>();

            String[] countries= eventRepository.getCountryList();
            int[] coutriesCouterGenere=new int [countries.length];
            boolean countryAlreadyInArray = false;
            int controller1=0;            
            for(int i=0; i<eventList.size() ;i++){
                final String genre = eventList.get(i).getKind();
                for(int j=0;j<countries.length;j++){
                    if(eventList.get(i).getCountry().equals(countries[j])) {
                        countryAlreadyInArray = true;
                        controller1=j;
                    }
                    

                }
                if (countryAlreadyInArray){
                    coutriesCouterGenere[controller1]++;
                } else{
                    int x=0;
                    while(countries[x] != null){
                        x++;
                    }
                    countries[x]=eventList.get(i).getCountry();
                    coutriesCouterGenere[x]++;

                    genreMapsList.add(new HashMap<String, Integer>());

                }
                countryAlreadyInArray = false;

                if(genreMapsList.get())

            }
        }
*/
 }
