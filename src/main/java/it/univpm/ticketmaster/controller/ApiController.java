package it.univpm.ticketmaster.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.univpm.ticketmaster.model.Event;



@RestController
public class ApiController {

	@RequestMapping(value = "/metadata", method = RequestMethod.GET, headers = "Content-type=application/json")
	public String metadata() {
		return Event.getMetaData();
	}
}