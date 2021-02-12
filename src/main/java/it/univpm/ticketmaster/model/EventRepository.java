package it.univpm.ticketmaster.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.naming.ConfigurationException;

import it.univpm.ticketmaster.exception.FilterException;
import org.json.JSONArray;
import org.json.JSONObject;

import it.univpm.ticketmaster.exception.EventLoadingException;
import it.univpm.ticketmaster.exception.HttpException;
import it.univpm.ticketmaster.helper.ConfigurationHelper;
import it.univpm.ticketmaster.helper.HttpHelper;

public class EventRepository {
    private static EventRepository instance;
    private final List<Event> eventList = new ArrayList<>();

    private EventRepository() {
        // private to prevent anyone else from instantiating (Singleton pattern)
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public void loadData() throws EventLoadingException, ConfigurationException {
        String[] countryList = ConfigurationHelper.getCountryList();
        for (String country : countryList) {
            String url = ConfigurationHelper.getApiUrl() + "?apikey=" + ConfigurationHelper.getApiKey() + "&size=200";
            url += "&countryCode=" + country;
            loadDataFromPages(url, country, 0, true);
        }
    }

    private void loadDataFromPages(String url, String country, int pageNumber, boolean iterate) throws EventLoadingException {
        try {
            String jsonString;

            jsonString = HttpHelper.get(url + "&page=" + pageNumber);

            JSONObject parsedData = new JSONObject(jsonString);

            if (!parsedData.isNull("_embedded")) {
                JSONArray jsonEventList = parsedData.getJSONObject("_embedded").getJSONArray("events");
                for (int i = 0; i < jsonEventList.length(); i++) {

                    JSONObject jsonEvent = jsonEventList.getJSONObject(i);

                    LocalDate startDate = LocalDate.parse(jsonEvent.getJSONObject("dates").getJSONObject("start").getString("localDate"));
                    LocalDate endDate;

                    if (jsonEvent.getJSONObject("dates").isNull("end")) { // If end date is null than endDate = StartDate
                        endDate = startDate; // No need to clone
                    } else {
                        endDate = startDate = LocalDate.parse(jsonEvent.getJSONObject("dates").getJSONObject("end").getString("localDate"));
                    }

                    Event event = new Event(
                            jsonEvent.getString("id"),
                            jsonEvent.getString("name"),
                            jsonEvent.getString("type"),
                            startDate,
                            endDate,
                            jsonEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"),
                            jsonEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name"),
                            country
                    );
                    this.eventList.add(event);
                }
            }

            if (iterate) {
                if (pageNumber < parsedData.getJSONObject("page").getInt("totalPages") && pageNumber < 4) { // API Limits: Max paging depth (page * size) must be less than 1000
                    loadDataFromPages(url, country, pageNumber + 1, true);
                }
            }
        } catch (HttpException httpException) {
            throw new EventLoadingException(httpException.getMessage());
        }
    }

    public List<Event> getAll() {
        return eventList;
    }

    public List<Event> filterByField(String field, String value, List<Event> eventList) throws FilterException {
        List<Event> matchList = new ArrayList<>();
        try {
            PropertyDescriptor pd = new PropertyDescriptor(field, Event.class);
            Method getter = pd.getReadMethod();

            for (Event event : eventList) {
                Object objectValue = getter.invoke(event);
                if (objectValue.toString().equals(value)) {
                    matchList.add(event);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new FilterException("The field `" + field + "` doesn't exist or isn't filterable");
        }
        return matchList;
    }

    public Predicate<Event> getConditionalFilterPredicate(String field, Object value, String conditionalFilterType) throws FilterException {
        try {
            final PropertyDescriptor pd = new PropertyDescriptor(field, Event.class);
            final Method getter = pd.getReadMethod();
            LocalDate date;

            final List<String> filterValues;

            switch (conditionalFilterType) {
                case "$gt":
                    checkComparableField(field);
                    date = getDateFromObject(value);
                    return (event) -> {
                        try {
                            return ((LocalDate) getter.invoke(event)).isAfter(date);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$gte":
                    checkComparableField(field);
                    date = getDateFromObject(value);
                    return (event) -> {
                        try {
                            final LocalDate eventDate = (LocalDate) getter.invoke(event);
                            return (eventDate.isAfter(date) || eventDate.equals(date));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$lt":
                    checkComparableField(field);
                    date = getDateFromObject(value);
                    return (event) -> {
                        try {
                            return ((LocalDate) getter.invoke(event)).isBefore(date);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$lte":
                    checkComparableField(field);
                    date = getDateFromObject(value);
                    return (event) -> {
                        try {
                            final LocalDate eventDate = (LocalDate) getter.invoke(event);
                            return (eventDate.isBefore(date) || eventDate.equals(date));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$bt":
                    List<String> betweenDates = null;
                    if (value instanceof List) {
                        betweenDates = (List<String>) value;
                        if (betweenDates.size() != 2) {
                            throw new FilterException("The value provided in the $bt filter is incorrect: the dates provided must be 2");
                        }
                    }
                    final List<String> finalBetweenDates = betweenDates; // Lambda function need final variable
                    return (event) -> {
                        try {
                            LocalDate dateBefore = LocalDate.parse(finalBetweenDates.get(0));
                            LocalDate dateAfter = LocalDate.parse(finalBetweenDates.get(1));
                            final LocalDate eventDate = (LocalDate) getter.invoke(event);
                            return (eventDate.isAfter(dateBefore) && eventDate.isBefore(dateAfter) || eventDate.equals(dateBefore) || eventDate.equals(dateAfter));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$not":
                    if (!(value instanceof String)){
                        throw new FilterException("The value provided in the $not filter is incorrect");
                    }
                    return (event) -> {
                        try {
                            final Object eventAttribute = getter.invoke(event);
                            return (!eventAttribute.toString().equals(value));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$in":
                    if (!(value instanceof List)){
                        throw new FilterException("The value provided in the $in filter is incorrect");
                    }
                    try {
                        filterValues = (List<String>) value; // check if it's a list of string
                    } catch (ClassCastException  e) {
                        throw new FilterException("The value provided in the $in filter is incorrect: must be a list of strings");
                    }
                    return (event) -> {
                        try {
                            final Object eventAttribute = getter.invoke(event);
                            for (Object filterValue : filterValues)
                            {
                                if (eventAttribute.toString().equals(filterValue)){
                                    return true;
                                }
                            }
                            return false;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return false;
                        }
                    };
                case "$nin":
                    if (!(value instanceof List)){
                        throw new FilterException("The value provided in the $in filter is incorrect");
                    }
                    try {
                        filterValues = (List<String>) value; // check if it's a list of string
                    } catch (ClassCastException  e) {
                        throw new FilterException("The value provided in the $in filter is incorrect: must be a list of strings");
                    }
                    return (event) -> {
                        try {
                            final Object eventAttribute = getter.invoke(event);
                            for (Object filterValue : filterValues)
                            {
                                if (eventAttribute.toString().equals(filterValue)){
                                    return false;
                                }
                            }
                            return true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return true;
                        }
                    };
                default:
                    throw new FilterException(conditionalFilterType + " is not a valid filter type");
            }
        } catch (IntrospectionException e) {
            throw new FilterException("The field `" + field + "` doesn't exist or isn't filterable");
        }
    }

    private LocalDate getDateFromObject(Object object) throws FilterException {
        if (object instanceof String) {
            try {
                return LocalDate.parse((String) object);
            } catch (DateTimeParseException e){
                throw new FilterException(object + " is not a valid date");
            }
        } else {
            throw new FilterException("Value of filter cannot be converted to date, only dates can be used with `$gt, $gte, $lt, $lte, $bt`");
        }
    }

    private void checkComparableField(String field) throws FilterException {
        if (!Event.isFieldComparable(field)){
            throw new FilterException("Only dates can be used with `$gt, $gte, $lt, $lte, $bt`");
        }
    }
}
