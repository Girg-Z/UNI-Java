package it.univpm.ticketmaster.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationHelper {
      // Todo: Move
    private static Properties getConfiguration() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try {
            InputStream resourceStream = loader.getResourceAsStream("application.properties");
            properties.load(resourceStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Todo: Move
    public static String[] getCountryList() {
        Properties properties = getConfiguration();
        return properties.getProperty("ticketmaster.countryList").split(",");
    }

}
