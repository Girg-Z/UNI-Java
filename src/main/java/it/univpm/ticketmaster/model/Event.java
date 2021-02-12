package it.univpm.ticketmaster.model;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class Event implements EntityInterface{
    private String id;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String segment;
    private String kind;
    private String country;

    public static final String[] COMPARABLE_FIELDS = new String[]{"startdate", "enddate"};

    public Event(String id, String name, String type, LocalDate startDate, LocalDate endDate, String segment, String kind,String country) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startDate =  startDate;
        this.endDate =  endDate;
        this.segment = segment;
        this.kind = kind;
        this.country=country;
    }

    public static String getMetaData(){
        Field[] fields = Event.class.getDeclaredFields();
        JSONArray ja = new JSONArray();
        for (int i=0;i<fields.length;i++){
            Field field = fields[i];
            String fieldName, fieldDescription="", fieldType;
            fieldName=field.getName();
            switch(i){
                case 0:
                fieldDescription = " codice identificazione ";
                break;
                case 1:
                fieldDescription = " nome evento ";
                break;
                case 2:
                fieldDescription = " tipo evento ";
                break;
                case 3:
                fieldDescription = " data inizio ";
                break;
                case 4:
                fieldDescription = " data fine ";
                break;
                case 5:
                fieldDescription = " segmento ";
                break;
                case 6:
                fieldDescription = " genere evento ";
                break;
                case 7:
                fieldDescription = " Stato ";
                break;
            };
            fieldType = (String) field.getType().getSimpleName();
            JSONObject jo = new JSONObject();
            jo.put("Alias", fieldName);
            jo.put("Sourcefield", fieldDescription);
            jo.put("Type", fieldType);

            ja.put(jo);
          
        }
      //  var myJSON = JSON.stringify(ja);
        String str = ja.toString();
        return str;
    
        
    }

    public JSONObject toJsonObject(){
        JSONObject jo = new JSONObject();
        jo.put("country", country);
        jo.put("id", id);
        jo.put("name", name);
        jo.put("Type", type);
        jo.put("startDate", startDate);
        jo.put("endDate", endDate);
        jo.put("segment", segment);
        jo.put("kind", kind);
        jo.put("country", country);

        

        return jo;
    }

    public String getType() {
        return this.type;
    }


    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getSegment() {
        return this.segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public static boolean isFieldComparable(String field){
        field = field.toLowerCase();
        return Arrays.asList(COMPARABLE_FIELDS).contains(field);
    }
}
