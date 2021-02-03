package it.univpm.ticketmaster.controller;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.ConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import it.univpm.ticketmaster.exception.FilterException;
import it.univpm.ticketmaster.helper.ConfigurationHelper;
import it.univpm.ticketmaster.model.Event;
import it.univpm.ticketmaster.model.EventRepository;

@RestController
public class EventController {
    private final EventRepository eventRepository;

    public EventController() {
        this.eventRepository = EventRepository.getInstance();
    }

    public String stats(String filter) throws FilterException, ConfigurationException {

        final List<Event> eventList = eventRepository.getAll();
        String[] countries = ConfigurationHelper.getCountryList();
        int period = 30;

        if (filter != null) {
            JSONObject parsedFilter = new JSONObject(filter);
            if (!parsedFilter.isNull("countries")) {
                countries = JSONArrayToStringArray(parsedFilter.getJSONArray("countries"));
            }
            if (!parsedFilter.isNull("period")) {
                try {
                    period = parsedFilter.getInt("period");
                    if (period <= 0 || period > 365) throw new FilterException(
                            "The period must be greater than 0 and less than 365");
                } catch (JSONException e) {
                    throw new FilterException("The period must be integer");
                }
            }
        }

        int[] countriesCounter = new int[countries.length];
        List<Map<String, Integer>> genreMapsList = new ArrayList<>();
        List<int[]> eventsByPeriod = new ArrayList<>();

        for (String ignored : countries) {
            genreMapsList.add(new HashMap<>());
            eventsByPeriod.add(new int[(getDatesArray().length / period) + 1]);
        }

        for (int i = 0; i < eventList.size(); i++) {
            final String genre = eventList.get(i).getKind();
            for (int j = 0; j < countries.length; j++) {
                if (eventList.get(i).getCountry().equals(countries[j])) {
                    countriesCounter[j]++;
                    genreMapsList.get(j).merge(genre, 1, Integer::sum);
                    int periodNumber = getEventPeriodNumber(eventList.get(i), period);
                    eventsByPeriod.get(j)[periodNumber]++;
                }
            }
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < countries.length; i++) {
            JSONObject jo = new JSONObject();
            int min = eventsByPeriod.get(i)[0];
            int max = eventsByPeriod.get(i)[0];
            for (int x = 0; x < eventsByPeriod.get(i).length; x++) {
                if (eventsByPeriod.get(i)[x] < min) {
                    min = eventsByPeriod.get(i)[x];
                }
                if (eventsByPeriod.get(i)[x] > max) {
                    max = eventsByPeriod.get(i)[x];
                }
            }
            jo.put("country", countries[i]);
            jo.put("minimumOfEvent", min);
            jo.put("numberOfPeriod", eventsByPeriod.get(i).length);
            jo.put("maximumOfEvent", max);
            jo.put("average", countriesCounter[i] / eventsByPeriod.get(i).length);
            jo.put("numberOfEvents", countriesCounter[i]);
            jo.put("eventsByGenre", genreMapsList.get(i));
            ja.put(jo);
        }
        String str = ja.toString();
        return str;
    }

    private Date[] getDatesArray() {
        // Find first and last date
        Date first = null;
        Date last = null;
        for (Event event : eventRepository.getAll()) {
            if (first == null) {
                first = event.getStartDateTime();
                last = event.getEndDateTime();
            } else {
                if (event.getStartDateTime().before(first)) {
                    first = event.getStartDateTime();
                }
                if (event.getStartDateTime().after(last)) {
                    last = event.getStartDateTime();
                }
            }
        }

        long dateDiff = last.getTime() - first.getTime();
        int daysNumber = (int) TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
        Date[] datesArray = new Date[daysNumber];

        datesArray[0] = first;
        for (int i = 1; i < daysNumber; i++) {
            datesArray[i] = Date.from(datesArray[i - 1].toInstant().plus(1, ChronoUnit.DAYS));
        }
        return datesArray;
    }

    private boolean compareDateWhitoutTime(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date1).equals(sdf.format(date2));
    }

    private int getEventPeriodNumber(Event event, int period) {
        Date[] datesArray = getDatesArray();
        for (int i = 0; i < datesArray.length; i++) {
            if (compareDateWhitoutTime(datesArray[i], event.getStartDateTime())) {
                return i / period;
            }
        }

        return 0; // TODO: Add runtime exception
    }

    // Todo: Move to jsonHelper
    private String[] JSONArrayToStringArray(JSONArray jsonArray) {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

    public String events(String filter) {
        List<Event> eventList = eventRepository.getAll();
        final JSONObject parsedFilter = new JSONObject(filter);
        eventList = resolveFilter(parsedFilter.toMap(), eventList);

        final JSONArray jsonArray = new JSONArray();
        for (Event event : eventList) {
            jsonArray.put(event.toJsonObject());
        }
        return jsonArray.toString();
    }

    private List<Event> resolveFilter(Map<String, Object> filter, List<Event> eventList) {
        final Map.Entry<String, Object> filterEntry = filter.entrySet().iterator().next();
        final String filterKey = filterEntry.getKey();
        final Object filterValue = filterEntry.getValue();

        if (filterValue instanceof String) { // If filterValue is a string than list need to be filtered by a simple field
            eventList = EventRepository.filterByField(filterKey, (String) filterValue, eventList);

        } else if (filterValue instanceof List) {// If filterValue is a list than is a complex filter like and | or

            if (((List<?>) filterValue).size() != 2) {
                // TODO: Throw exception
            } else {
                List<Event> eventList1 = resolveFilter((Map<String, Object>) ((List<?>) filterValue).get(0), eventList);
                List<Event> eventList2 = resolveFilter((Map<String, Object>) ((List<?>) filterValue).get(1), eventList);
                switch (filterKey){
                    case "$and":
                        eventList = listIntersection(eventList1, eventList2);
                        break;
                    case "$or":
                        eventList = listUnion(eventList1, eventList2);
                        break;
                    default:
                        // TODO: throw exception
                }
            }

        } else {
            // TODO: Add exception
        }
        return eventList;
    }


    // TODO: move to listHelper
    private List<Event> listIntersection (List<Event> list1, List<Event> list2){
        return list1.stream().filter(list2::contains).collect(Collectors.toList());
    }

    private List<Event> listUnion (List<Event> list1, List<Event> list2){
        return Stream.concat(list1.stream(), list2.stream()).distinct().collect(Collectors.toList());
    }

}
