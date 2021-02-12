package it.univpm.ticketmaster.controller;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

import it.univpm.ticketmaster.helper.JSONHelper;
import it.univpm.ticketmaster.helper.ListHelper;
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
                countries = JSONHelper.JSONArrayToStringArray(parsedFilter.getJSONArray("countries"));
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
            //jo.put("numberOfPeriod", eventsByPeriod.get(i).length);
            jo.put("maximumOfEvent", max);
            jo.put("average", countriesCounter[i] / eventsByPeriod.get(i).length);
            jo.put("numberOfEvents", countriesCounter[i]);
            jo.put("eventsByGenre", genreMapsList.get(i));
            ja.put(jo);
        }
        String str = ja.toString();
        return str;
    }

    private LocalDate[] getDatesArray() {
        // Find first and last date
        LocalDate first = null;
        LocalDate last = null;
        for (Event event : eventRepository.getAll()) {
            if (first == null) {
                first = event.getStartDate();
                last = event.getEndDate();
            } else {
                if (event.getStartDate().isBefore(first)) {
                    first = event.getStartDate();
                }
                if (event.getStartDate().isAfter(last)) {
                    last = event.getStartDate();
                }
            }
        }

        int daysNumber = (int) ChronoUnit.DAYS.between(first, last);
        LocalDate[] datesArray = new LocalDate[daysNumber];

        datesArray[0] = first;
        for (int i = 1; i < daysNumber; i++) {
            datesArray[i] = (datesArray[i - 1].plusDays(1));
        }
        return datesArray;
    }

    private int getEventPeriodNumber(Event event, int period) {
        LocalDate[] datesArray = getDatesArray();
        for (int i = 0; i < datesArray.length; i++) {
            if (event.getStartDate().equals(datesArray[i])) {
                return i / period;
            }
        }
        return 0;
    }

    public String events(String filter) throws FilterException {
        List<Event> eventList = eventRepository.getAll();
        if (filter != null) {
            final JSONObject parsedFilter;
            try {
                parsedFilter = new JSONObject(filter);
            } catch (JSONException jsonException) {
                throw new FilterException("Filter must be a valid json");
            }
            eventList = resolveFilter(parsedFilter.toMap(), eventList);
        }

        final JSONArray jsonArray = new JSONArray();
        final JSONObject jsonObject = new JSONObject();
        for (Event event : eventList) {
            jsonArray.put(event.toJsonObject());
        }
        jsonObject.put("events", jsonArray);
        jsonObject.put("totalEvents", eventList.size());
        return jsonObject.toString();
    }

    private List<Event> resolveFilter(Map<String, Object> filter, List<Event> eventList) throws FilterException {
        final Map.Entry<String, Object> filterEntry = filter.entrySet().iterator().next();
        final String filterKey = filterEntry.getKey();
        final Object filterValue = filterEntry.getValue();

        if (filterValue instanceof String) { // If filterValue is a string than list need to be filtered by a simple field
            eventList = eventRepository.filterByField(filterKey, (String) filterValue, eventList);

        } else if (filterValue instanceof List) {// If filterValue is a list than is a complex filter like and, or
            if (((List<?>) filterValue).size() != 2) {
                throw new FilterException("$and, $or filters must have 2 components");
            } else {
                List<Event> eventList1 = resolveFilter((Map<String, Object>) ((List<?>) filterValue).get(0), eventList);
                List<Event> eventList2 = resolveFilter((Map<String, Object>) ((List<?>) filterValue).get(1), eventList);
                switch (filterKey) {
                    case "$and":
                        eventList = ListHelper.intersection(eventList1, eventList2);
                        break;
                    case "$or":
                        eventList = ListHelper.union(eventList1, eventList2);
                        break;
                    default:
                        throw new FilterException(filterKey + " is not a valid filter type");
                }
            }

        } else if (filterValue instanceof Map) { // If filterValue is a map than is a conditional filter like gt, in
            final Map.Entry<String, Object> conditionalFilter = ((Map<String, Object>) filterValue).entrySet().iterator().next();
            final String conditionalFilterType = conditionalFilter.getKey();
            final Object conditionalFilterValue = conditionalFilter.getValue();

            final Predicate<Event> predicate = eventRepository.getConditionalFilterPredicate(filterKey, conditionalFilterValue, conditionalFilterType);
            eventList = eventList.stream().filter(predicate).collect(Collectors.toList());
        } else {
            throw new FilterException("Invalid filter structure");
        }
        return eventList;
    }
}