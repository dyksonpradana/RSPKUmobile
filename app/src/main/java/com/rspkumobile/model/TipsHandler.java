package com.rspkumobile.model;

/**
 * Created by DK on 1/6/2018.
 */

public class TipsHandler {
    private String content;
    private String time;

    public TipsHandler(){}


    public TipsHandler(String h, String t){
        this.content=h;
        this.time=t;
    }

    public String getContent(){
        return content;
    }

    public String getTime() { return time;}
}
