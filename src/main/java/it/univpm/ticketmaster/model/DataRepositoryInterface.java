package it.univpm.ticketmaster.model;

import it.univpm.ticketmaster.exception.DataLoadingException;
import it.univpm.ticketmaster.exception.FilterException;

import javax.naming.ConfigurationException;
import java.util.List;

interface DataRepositoryInterface {
    public void loadData() throws DataLoadingException, ConfigurationException;
    public List<? extends EntityInterface> getAll();
    public List<? extends EntityInterface> filterByField(String field, String value, List<Event> eventList) throws FilterException;
}
