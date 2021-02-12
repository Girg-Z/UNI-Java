package it.univpm.ticketmaster.helper;

import it.univpm.ticketmaster.model.Event;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper for basic operations with Event List
 */
public class ListHelper
{
    /**
     * Perform lists intersections (and)
     * @param list1 first list
     * @param list2 second list
     * @return intersected list
     */
    public static List<Event> intersection(List<Event> list1, List<Event> list2) {
        return list1.stream().filter(list2::contains).collect(Collectors.toList());
    }

    /**
     * Perform list union (or)
     * @param list1 first list
     * @param list2 second list
     * @return list union
     */
    public static List<Event> union(List<Event> list1, List<Event> list2) {
        return Stream.concat(list1.stream(), list2.stream()).distinct().collect(Collectors.toList());
    }
}
