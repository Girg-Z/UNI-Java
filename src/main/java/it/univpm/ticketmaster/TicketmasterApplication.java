package it.univpm.ticketmaster;

import it.univpm.ticketmaster.model.EventRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicketmasterApplication {

	public static void main(String[] args) {
		System.out.println("Started events loading...");
		EventRepository.getInstance().loadData();
		System.out.println("Done! " + EventRepository.getInstance().getAll().size() + " events loaded");
		SpringApplication.run(TicketmasterApplication.class, args);
	}
}


