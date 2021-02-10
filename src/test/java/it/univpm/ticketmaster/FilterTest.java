package it.univpm.ticketmaster;

import org.junit.jupiter.api.Test;

import it.univpm.ticketmaster.controller.EventController;
import it.univpm.ticketmaster.exception.FilterException;
import it.univpm.ticketmaster.model.EventRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

public class FilterTest {
    private EventController eventController;

    @BeforeEach
    void setUp() throws Exception{
        System.out.println("Started events loading...");
		EventRepository.getInstance().loadData();
		System.out.println("Done! " + EventRepository.getInstance().getAll().size() + " events loaded");
        eventController = new EventController();
    }


    @Test
    @DisplayName("Corretta generazione della funzione STATS")
    void test1(){
        assertThrows(FilterException.class, () -> {
            eventController.stats("{\"countries\": [\"uk\", \"it\"],\"period\": 5}");
        });
    }
    @Test
    @DisplayName("Corretta generazione della funzione ")
    void test2(){

    }
}