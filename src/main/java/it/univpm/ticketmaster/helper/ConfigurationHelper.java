package it.univpm.ticketmaster.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;

public class ConfigurationHelper {
    private static Properties getConfiguration() throws ConfigurationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try {
            InputStream resourceStream = loader.getResourceAsStream("application.properties");
            properties.load(resourceStream);
            return properties;
        } catch (IOException ioException) {
            throw new ConfigurationException(ioException.getMessage());
        }
    }

    // Todo: Move
    public static String[] getCountryList() throws ConfigurationException {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.countryList").split(",");
    }

}
