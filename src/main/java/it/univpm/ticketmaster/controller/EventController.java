package it.univpm.ticketmaster.controller;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        public ResponseEntity<String> stats(@RequestParam(required = false) String filter) {
            final HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            final List <Event> eventList = eventRepository.getAll();
            String[] countries = eventRepository.getCountryList();
            int period = 30;

            if (filter != null){
                JSONObject parsedFilter = new JSONObject(filter);
                if(!parsedFilter.isNull("countries")){
                    countries = JSONArrayToStringArray(parsedFilter.getJSONArray("countries"));
                }
                if(!parsedFilter.isNull("period")){
                    period = parsedFilter.getInt("period");
                }
            }

            int[] countriesCounter = new int [countries.length];
            List<Map<String,Integer>> genreMapsList = new ArrayList<>();
            List<int[]> eventsByPeriod = new ArrayList<>();

            for (String ignored : countries) {
                genreMapsList.add(new HashMap<>());
                eventsByPeriod.add(new int[(getDatesArray().length / period) + 1]);
            }

            for(int i=0; i<eventList.size() ;i++){
                final String genre = eventList.get(i).getKind();
                for(int j=0;j<countries.length;j++){
                    if(eventList.get(i).getCountry().equals(countries[j])) {
                        countriesCounter[j]++;
                        genreMapsList.get(j).merge(genre, 1, Integer::sum);
                        int periodNumber = getEventPeriodNumber(eventList.get(i), period);
                        eventsByPeriod.get(j)[periodNumber]++;
                    }
                }
            }
            JSONArray ja = new JSONArray();
            for(int i=0;i<countries.length;i++){
                JSONObject jo = new JSONObject();
                int min = eventsByPeriod.get(i)[0];
                int max = eventsByPeriod.get(i)[0];
//                int minPeriod=0,maxPeriod=0;
                for(int x=0;x<eventsByPeriod.get(i).length ;x++){
                    if(eventsByPeriod.get(i)[x]<min){
                        min= eventsByPeriod.get(i)[x];
//                        minPeriod=x;
                    }
                    if(eventsByPeriod.get(i)[x]>max){
                        max= eventsByPeriod.get(i)[x];
//                        maxPeriod=x;
                    }
                }
                jo.put("country", countries[i]);
                jo.put("minimumOfEvent", min);
              //  jo.put("min period", minPeriod);
              //  jo.put("max period", maxPeriod);
                jo.put("numberOfPeriod", eventsByPeriod.get(i).length);
                jo.put("maximumOfEvent", max);
                jo.put("average",countriesCounter[i]/ eventsByPeriod.get(i).length);
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
        return datesArray;
    }

    private boolean compareDateWhitoutTime(Date date1, Date date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date1).equals(sdf.format(date2));
    }

    private int getEventPeriodNumber(Event event, int period){
        Date[] datesArray = getDatesArray();
        for (int i = 0; i < datesArray.length; i++) {
            if (compareDateWhitoutTime(datesArray[i], event.getStartDateTime())){
                return i / period;
            }
        }

        return 0; // Todo: Add runtime exception
    }


    @GetMapping("/events")
    public ResponseEntity<String> events() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        List <Event> eventList = eventRepository.getAll();
        JSONArray ja= new JSONArray();
        for(int i=0; i<eventList.size() ;i++){
            ja.put(eventList.get(i).toJsonObject());
        }

        String str=ja.toString();
        return new ResponseEntity<>(str, httpHeaders, HttpStatus.OK);

    }

    // Todo: Move to jsonHelper
    private String[] JSONArrayToStringArray (JSONArray jsonArray){
        String[] array = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++){
            array[i] = jsonArray.getString(i);
        }
        return  array;
    }

}
