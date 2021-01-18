package it.univpm.ticketmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import it.univpm.ticketmaster.model.Event;


import java.util.*;
import java.lang.reflect.*;

@SpringBootApplication
public class TicketmasterApplication {

	private static final String Z = null;
	private static Object startdate;

	public static void main(String[] args) {
		Event e;
		// Convert ISO 8601 dateTime String to Date Object
			TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(
				"2021-01-24T23:40:00Z"
            );
            Instant instant = Instant.from(temporalAccessor);
            Date startDate = Date.from(instant);
		e = new Event  ("12we","evento1","rock",startDate,startDate,"vffvfv","fvbgg");
	//	String a= e.getMetaData();
	//	System.out.println(e.getMetaData());

		System.out.println(e.getMetaData());
		
		SpringApplication.run(TicketmasterApplication.class, args);
	}
}


