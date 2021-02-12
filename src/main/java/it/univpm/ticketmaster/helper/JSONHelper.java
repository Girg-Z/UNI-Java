package it.univpm.ticketmaster.helper;

import org.json.JSONArray;

/**
 * Helper for basic operations with org.json.JSONArray
 */
public class JSONHelper
{
    /**
     * @param jsonArray The JSONArray object to convert
     * @return jsonArray converted in string array
     */
    public static String[] JSONArrayToStringArray(JSONArray jsonArray) {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }
}
