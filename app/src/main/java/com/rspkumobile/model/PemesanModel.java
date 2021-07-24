package com.rspkumobile.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by DK on 9/8/2017.
 */

public class PemesanModel {

    private String namaPasien,antriTanggal,antriStatus,uid,ref;
    private int antriNo,skip;
    private boolean tinjau;

    public PemesanModel(){}

    public PemesanModel(String nama,String tanggal, String ref){
        this.namaPasien=nama;
        this.antriTanggal=tanggal;
        this.antriStatus="antri";
        this.tinjau=true;
        this.antriNo=0;
        this.uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.skip=2;
        this.ref=ref;
    }

//    public PemesanModel(String nama, String tanggal, String status, int no, String ref){
//        this.namaPasien=nama;
//        this.antriTanggal=tanggal;
//        this.antriStatus=status;
//        this.tinjau=true;
//        this.antriNo=no;
//        this.uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
//        this.skip=2;
//        this.ref=ref;
//
//    }

    public PemesanModel(String nama, String tanggal, String status, boolean tinjau, int no, String uid, int skip, String ref){
        this.namaPasien=nama;
        this.antriTanggal=tanggal;
        this.antriStatus=status;
        this.tinjau=tinjau;
        this.antriNo=no;
        this.uid=uid;
        this.skip=skip;
        this.ref=ref;
    }

//    public Object getAllDetail(){
//        HashMap all = new HashMap();
//        all.put("namaPasien",namaPasien);
//        all.put("antriTanggal",antriTanggal);
//        all.put("antriPukul",antriPukul);
//        all.put("antriStatus",antriStatus);
//        all.put("antriNo",antriNo);
//        all.put("token", uid);
//        all.put("token", token);
//        all.put("ref", ref);
//        return all;
//    }

    public String getNamaPasien(){return namaPasien;}
    public String getAntriTanggal(){return antriTanggal;}
    public boolean getTinjau(){return tinjau;}
    public String getAntriStatus(){return antriStatus;}
    public Integer getAntriNo(){return antriNo;}
    public String getUid(){return uid;}
    public Integer getSkip(){return skip;}
    public String getRef(){return ref;}
    public void setNamaPasien(String s){this.namaPasien=s;}
    public void setAntriTanggal(String s){this.antriTanggal=s;}
    public void setTinjau(boolean b){this.tinjau=b;}
    public void setAntriStatus(String s){this.antriStatus=s;}
    public void setAntriNo(int i){this.antriNo=i;}
    public void setUid(String s){this.uid=s;}
    public void setSkip(int i){this.skip=i;}
    public void setRef(String s){this.ref=s;}

}
