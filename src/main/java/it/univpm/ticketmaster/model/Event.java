package it.univpm.ticketmaster.model;
import java.util.Date;

public class Event {
    private String id;
    private String name;
    private String type;
    private Date startDataTime;
    private Date endDataTime;
    private String segment;
    private String kind; // GENERE

    public Event(String id, String name, String type, Date startDataTime, Date endDataTime, String segment, String kind) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startDataTime = startDataTime;
        this.endDataTime = endDataTime;
        this.segment = segment;
        this.kind = kind;
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

    public String getType() {
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



   

}
