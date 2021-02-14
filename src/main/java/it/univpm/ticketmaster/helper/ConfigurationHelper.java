package it.univpm.ticketmaster.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;

/**
 * Class for getting data from application.properties file
 */
public class ConfigurationHelper {
    /**
     * @return Configuration object
     * @throws ConfigurationException if there is an error fetching the application configuration
     */
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

    /**
     * @return Country list
     * @throws ConfigurationException if there is an error fetching the application configuration
     */
    public static String[] getCountryList() throws ConfigurationException {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.countryList").split(",");
    }

    /**
     * @return Api key
     * @throws ConfigurationException if there is an error fetching the application configuration
     */
    public static String getApiKey() throws ConfigurationException {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.apiKey");
    }

    /**
     * @return Base API URL
     * @throws ConfigurationException if there is an error fetching the application configuration
     */
    public static String getApiUrl() throws ConfigurationException {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.apiUrl");
    }
}
