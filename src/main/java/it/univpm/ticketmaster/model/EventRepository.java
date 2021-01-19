package it.univpm.ticketmaster.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import it.univpm.ticketmaster.helper.HttpHelper;
import org.json.JSONArray;
import org.json.JSONObject;

public class EventRepository {
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";
    private static final String API_KEY = "V3cp8w7Dn60dMykxGNFoAbOL6KtD8L07"; // Todo: Move this to a configuration or env file

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

    public void loadData() {
        String[] countryList = getCountryList();
        String url = BASE_URL + "?apikey=" + API_KEY + "&size=200";
        String jsonString = HttpHelper.get(url);
        JSONObject parsedData = new JSONObject(jsonString);
        JSONArray jsonEventList = parsedData.getJSONObject("_embedded").getJSONArray("events");

        for (int i = 0; i < jsonEventList.length(); i++) {
            JSONObject jsonEvent = jsonEventList.getJSONObject(i);

            // Convert ISO 8601 dateTime String to Date Object
            TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(
                    jsonEvent.getJSONObject("dates").getJSONObject("start").getString("dateTime")
            );
            Instant instant = Instant.from(temporalAccessor);
            Date startDate = Date.from(instant);

            Date endDate;
            if (jsonEvent.getJSONObject("dates").isNull("end")) { // If end date is null than endDate = StartDate
                endDate = startDate; // No need to clone
            } else {
                temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(
                        jsonEvent.getJSONObject("dates").getJSONObject("end").getString("dateTime")
                );
                instant = Instant.from(temporalAccessor);
                endDate = Date.from(instant);
            }

            Event event = new Event(
                    jsonEvent.getString("id"),
                    jsonEvent.getString("name"),
                    jsonEvent.getString("type"),
                    startDate,
                    endDate,
                    jsonEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"),
                    jsonEvent.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name")
            );
            this.eventList.add(event);
        }
    }

    public List<Event> getAll() {
        return eventList;
    }


    //Todo: Add Exceptions, maybe don't use this method at all
    public List<Event> filterByField(String field, String value) {

        List<Event> matchList = new ArrayList<Event>();
        try {
            PropertyDescriptor pd = new PropertyDescriptor(field, Event.class);
            Method getter = pd.getReadMethod();

            for (Event event : eventList) {
                String objectValue = (String) getter.invoke(event);
                if (value.equals(objectValue)){
                    matchList.add(event);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            e.printStackTrace();
        }
        return matchList;
    }

    // Todo: Move
    private Properties getConfiguration (){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try {
            InputStream resourceStream = loader.getResourceAsStream("application.properties");
            properties.load(resourceStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Todo: Move
    private String[] getCountryList() {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.countryList").split(",");
    }
}
