package com.rspkumobile.model;

/**
 * Created by DK on 10/15/2017.
 */
public class NotifHandler {

    private String header;
    private String note;
    private String type;
    private Boolean read;
    private String key;
    private String time;

    public NotifHandler(){}


    public NotifHandler(String header, String note, String type, Boolean read, String keyRef, String time){
        this.header = header;
        this.note= note;
        this.type= type;
        this.read= read;
        this.key= keyRef;
        this.time=time;
    }

    public String getHeader(){
        return header;
    }

    public void setHeader(String s){
        this.header = s;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String s){this.note=s;}

    public String getType(){
        return type;
    }

    public void setType(String s){
        this.type = s;
    }

    public Boolean getRead(){
        return read;
    }

    public void setRead(Boolean s){
        this.read = s;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String s){
        this.key = s;
    }

    public String getTime() {return time;}

    public void setTime(String s) { this.time = s;}
}
