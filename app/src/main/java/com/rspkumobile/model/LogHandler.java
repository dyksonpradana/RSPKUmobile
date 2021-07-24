package com.rspkumobile.model;

/**
 * Created by DK on 1/12/2018.
 */

public class LogHandler{
    private String log,time;
    public LogHandler(){}
    public LogHandler(String log, String time){
        this.log=log;
        this.time=time;
    }
    public String getLog(){return log;}
//    private void setLog(String l) {this.log = l;}

    public String getTime(){return time;}
//    private void setTime(String t) {this.log = t;}
}