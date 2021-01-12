package it.univpm.ticketmaster.model;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventRepository {
    private static EventRepository instance; // https://en.wikipedia.org/wiki/Singleton_pattern
    private static List<Event> eventList;

    private EventRepository() {
        // private to prevent anyone else from instantiating (Singleton pattern)
    }

    public static EventRepository getInstance(){
        if (instance == null){
            instance = new EventRepository();
        }
        return instance;
    }

    private void loadData(){
        //TODO: Move this
    }

    public static List<Event> getEventList() {
        return eventList;
    }
}
