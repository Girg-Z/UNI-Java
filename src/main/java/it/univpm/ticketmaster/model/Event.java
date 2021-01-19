package it.univpm.ticketmaster.model;
import java.lang.reflect.Field;
import java.util.Date;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Event {
    private String id;
    private String name;
    private String type;
    private Date startDataTime;
    private Date endDataTime;
    private String segment;
    private String kind; // GENERE

    public Event(String id, String name, String type, Date startdate, Date startdate2, String segment, String kind) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startDataTime =  startdate;
        this.endDataTime =  startdate2;
        this.segment = segment;
        this.kind = kind;
    }

    public static String getMetaData(){
        Field[] fields = Event.class.getDeclaredFields();
        JSONArray ja = new JSONArray();
        for (int i=0;i<fields.length;i++){
            Field field = fields[i];
            String attributo1, attributo2="", attributo3;
            attributo1=field.getName();
            switch(i){
                case 0:
                attributo2 = " codice identificazione ";
                break;
                case 1:
                attributo2 = " nome evento ";
                break;
                case 2:
                attributo2 = " tipo evento ";
                break;
                case 3:
                attributo2 = " data inizio ";
                break;
                case 4:
                attributo2 = " data fine ";
                break;
                case 5:
                attributo2 = " segmento ";
                break;
                case 6:
                attributo2 = " genere evento ";
                break;
            };
            attributo3 = (String) field.getType().getSimpleName(); 
        //    array[i] = new MetaData(attributo1,attributo2,attributo3);
            JSONObject jo = new JSONObject();
            jo.put("Alias", attributo1);
            jo.put("Sourcefield", attributo2);
            jo.put("Type", attributo3);

            ja.put(jo);
          
        }
      //  var myJSON = JSON.stringify(ja);
        String str = ja.toString();
        return str;
        
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

    public String Type() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDataTime() {
        return this.startDataTime;
    }

    public void setStartDataTime(Date startDataTime) {
        this.startDataTime = startDataTime;
    }

    public Date getEndDataTime() {
        return this.endDataTime;
    }

    public void setEndDataTime(Date endDataTime) {
        this.endDataTime = endDataTime;
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


	public static String getDeclaretField() {
		return null;
	}


	public static Field[] getDeclaredFields() {
		return null;
	}



   

}
