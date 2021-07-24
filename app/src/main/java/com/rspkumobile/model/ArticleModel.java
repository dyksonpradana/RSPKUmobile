package com.rspkumobile.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DK on 12/28/2017.
 */

public class ArticleModel {

    private int id;
    private String title;
    private String cover;
    private String content;
    private String date;
    private String popularity;

    public ArticleModel(int id, String title, String cover, String content, String date, String popularity) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.content = content;
        this.date = date;
        this.popularity = popularity;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}

    public void setTitle(String s){this.title = s;}
    public String getTitle() {
        return title;
    }

    public void setCover(String s){this.cover = s;}
    public String getCover() {
        return cover;
    }

    public void setContent(String s){this.content = s;}
    public String getContent() {return content;}

    public void setDate(String s){this.date = s;}
    public String getDate(){return date;}

    public void setPopularity(String s){this.popularity = s;}
    public String getPopularity(){return popularity;}

    // method to sort article array by popularity
    public int byPopularity(ArticleModel a , ArticleModel b){
        // if a is more popular return 1(true) else return -1(false) or 0 if a and b are the same
        return a.getPopularity().compareTo(b.getPopularity());
    }

    // method to sort article array by created date
    public int byHot(ArticleModel a, ArticleModel b){

        // prepate date format to convert date string to date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // converting
        Date date1 = null;
        try {
            date1 = sdf.parse(a.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = sdf.parse(b.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        System.out.println("date1 : " + sdf.format(date1));
//        System.out.println("date2 : " + sdf.format(date2));

//        if (date1.compareTo(date2) > 0) {
//            System.out.println("Date1 is after Date2");
//        } else if (date1.compareTo(date2) < 0) {
//            System.out.println("Date1 is before Date2");
//        } else if (date1.compareTo(date2) == 0) {
//            System.out.println("Date1 is equal to Date2");
//        } else {
//            System.out.println("How to get here?");
//        }

        // if date1 is the latest return 1(true) else return -1(false) or 0 if date1 and date2 are the same
        return date1.compareTo(date2);
    }

}
