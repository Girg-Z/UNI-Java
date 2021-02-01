package it.univpm.ticketmaster;

import it.univpm.ticketmaster.exception.EventLoadingException;
import it.univpm.ticketmaster.exception.HttpException;
import it.univpm.ticketmaster.model.EventRepository;

import javax.naming.ConfigurationException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicketmasterApplication {

	public static void main(String[] args) throws ConfigurationException {
		System.out.println("Started events loading...");
		try{
		EventRepository.getInstance().loadData();
		} catch(EventLoadingException eventLoadingException){
			System.out.println(eventLoadingException.getMessage());
		}
		System.out.println("Done! " + EventRepository.getInstance().getAll().size() + " events loaded");
		SpringApplication.run(TicketmasterApplication.class, args);
	}
}


