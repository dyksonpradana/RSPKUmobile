package com.rspkumobile.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by DK on 9/8/2017.
 */

public class SharedPrefManager {

    private final String ANTRIAN ="antrian";
    private final String NAMA_PASIEN="namaPasien";
    private final String ANTRI_TANGGAL="antriTanggal";
    private final String TINJAU="tinjau";
    private final String ANTRI_STATUS="antristatus";
    private final String ANTRI_NO="antrino";

    private final String HOME_CONTENT = "home";
    private static final String CONTENT_SLIDE = "slide" ;
    private static final String CONTENT_ARTICLE = "article";
    private static final String CONTENT_SERVICE = "service";

    private static final String GALLERY = "gallery";
    private static final String ARTICLE = "article";
    private static final String CONTENT = "content";

    private static SharedPrefManager mInstance;
    private static Context mCtx;
    final private String UID="uid";
    final private String SKIP="skip";
    final private String REF ="referensi" ;
    private String DATA_POLI = "poli";
    private String SCHEDULE_POLI = "schedule";
    private String DAY_CODE = "day code";
    private static final String POLI_CODE = "poli code";
    private String EXPANDED_SECOND_ROW = "second row expanded";
    private String EXPANDED_FIRST_ROW = "first row expanded";
    private String EXPAND_FIRST_ROW = "expand1";
    private String EXPAND_SECOND_ROW = "expand2";
    private String EXPANDED = "expanded";
    private static final String FIRST_ROW_TO_EXPAND = "first";
    private static final String SECOND_ROW_TO_EXPAND = "second";
    private String POLI_NAME = "nama";
    private String BOOKING_DATE = "booking date";
    private String BOOKING_TIME = "booking time";
    private String BOOKING_NAME = "booking name";
    private String BOOKING_LIST_POSITION = "booking position";
    private String DOCTOR = "doctor";
    private String DOCTOR_NIP = "doctor nip";
    private String SWITCH_NOTIFICATION = "switch notification";
    private String NOTIFICATION = "notification";
    private String SOUND = "sound";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void pesanAntrian(PemesanModel pemesan){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(NAMA_PASIEN,pemesan.getNamaPasien());
        editor.putString(ANTRI_TANGGAL,pemesan.getAntriTanggal());
        editor.putBoolean(TINJAU,pemesan.getTinjau());
        editor.putString(ANTRI_STATUS,pemesan.getAntriStatus());
        editor.putInt(ANTRI_NO,pemesan.getAntriNo());
        editor.putString(UID,User.getUid());
        editor.putInt(SKIP,pemesan.getSkip());
        editor.putString(REF,pemesan.getRef());
        editor.apply();
    }

    public void updatePesanAntrianStatus(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(ANTRI_STATUS,s);
        editor.apply();
    }

    public void updateNomerAntrian(int i){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(ANTRI_NO,i);
        editor.apply();
    }

    public void setAsDoctor(String nip){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DOCTOR,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(DOCTOR,true);
        editor.putString(DOCTOR_NIP,nip);
        editor.apply();
    }

    public String getDoctorNip(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DOCTOR,Context.MODE_PRIVATE);
        return sharedPreferences.getString(DOCTOR_NIP,null);
    }

    public boolean isDoctor(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DOCTOR,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DOCTOR,false);
    }

    public void doctorClear(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DOCTOR,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isBooking(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(dateTime);

//        Log.e("compareTo"+sharedPreferences.getString(ANTRI_TANGGAL,null)+" "+date, String.valueOf((sharedPreferences.getString(ANTRI_TANGGAL,null).compareTo(date)<0)));

//        Date dateTime1;

        if(sharedPreferences.getString(ANTRI_TANGGAL, null)!=null) {

            try {
                Date dateTime1 = sdf.parse(sharedPreferences.getString(ANTRI_TANGGAL, null));
//            Log.e("compareTo"+sharedPreferences.getString(ANTRI_TANGGAL,null)+" "+date, String.valueOf(sdf.parse(sharedPreferences.getString(ANTRI_TANGGAL,null)).getTime())+" "+dateTime.getTime());
                Log.e("compareTo", String.valueOf(dateTime1.getTime() < dateTime.getTime()));
                if (dateTime1.getTime() < dateTime.getTime()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


//        Log.e("status",(sharedPreferences.getString(ANTRI_STATUS,null)!=null)+sharedPreferences.getString(ANTRI_STATUS,null).toString());
        }

        return sharedPreferences.getString(ANTRI_STATUS,null)!=null;
    }

    //this method will give the logged in user
    public PemesanModel getPemesan(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);
        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(dateTime);
        if(!date.equals(sharedPreferences.getString(ANTRI_TANGGAL,null))) {

            sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(dateTime);

            JSONObject value = new JSONObject();
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Config.FURL_USERS)
                    .child(User.getUid())
                    .child("notification")
                    .push();
            // app is in background, show the notification in notification tray


            try {
                value.put("type", "booking");
                value.put("head", "expired");
                value.put("read", false);
                value.put("note", "antrian anda kadaluarsa karena hari");
                value.put("key", ref.getKey());
                value.put("time",time);

                ref.setValue(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cancelBooking();
        }

        return new PemesanModel(
                sharedPreferences.getString(NAMA_PASIEN,null),
                sharedPreferences.getString(ANTRI_TANGGAL,null),
                sharedPreferences.getString(ANTRI_STATUS,null),
                sharedPreferences.getBoolean(TINJAU,true),
                sharedPreferences.getInt(ANTRI_NO,-1),
                sharedPreferences.getString(UID,null),
                sharedPreferences.getInt(SKIP,-1),
                sharedPreferences.getString(REF,null)
        );
    }


    //this method will logout the user
    public void cancelBooking(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

//        ((MainActivityDrawer)mCtx).setAlarmCancel();
    }

    //canceled by user
    private void setCanceledQueueNotification(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ANTRIAN,Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(dateTime);

        HashMap<String, String> val = new HashMap<String, String>();
        val.put("log","antrian "+sharedPreferences.getInt(ANTRI_NO, -1)+" membatalkan antrian");
        val.put("time",time);

        String[] dMy = sharedPreferences.getString(ANTRI_TANGGAL, null).split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd);
        ref.child("antrian").child(sharedPreferences.getString(REF, null)).child("antriStatus").setValue("batal");
        ref.child("antrian").child(sharedPreferences.getString(REF, null)).child("uid").setValue("-");
        ref.child("log").push().setValue(val);

        HashMap<String, String> value = new HashMap<String, String>();

        final DatabaseReference ref2 = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("notification")
                .push();
        // app is in background, show the notification in notification tray

        value.put("type", "booking");
        value.put("head", "BOOKING ANTRIAN");
        value.put("read", "false");
        value.put("note", "anda membatalkan antrian");
        value.put("key", ref2.getKey());

        ref2.setValue(value);
    }

    //canceled form server
    private void CanceledQueueNotification(){

    }

    // store given data of gallery (from API)
    public void storeGallery(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(GALLERY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTENT,s);
        editor.apply();
    }

    // get stored data of gallery
    public String getGallery(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(GALLERY,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTENT,null);
    }

    // store given data gallery (from API)
    public void storeArticle(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ARTICLE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTENT,s);
        editor.apply();
    }

    // get stored data of gallery
    public String getArticle(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(ARTICLE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTENT,null);
    }

    public void storeHomeContent(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(HOME_CONTENT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
        try {
            JSONObject json = new JSONObject(s);
            editor.clear();
            editor.apply();
            editor.putString(CONTENT_SLIDE,String.valueOf(json.getJSONArray("slide")));
            editor.putString(CONTENT_ARTICLE,String.valueOf(json.getJSONArray("berita")));
            editor.putString(CONTENT_SERVICE,String.valueOf(json.getJSONArray("pelayanan")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public void storeFeaturesData(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DATA_POLI,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
        try {
            JSONObject schedule = new JSONObject(s).getJSONObject("jadwal");
            JSONObject day = new JSONObject(s).getJSONObject("hari");
            JSONObject service = new JSONObject(s).getJSONObject("poli");
            JSONObject doctor = new JSONObject(s).getJSONObject("dokter");
            
//            editor.clear();
//            editor.apply();
            editor.putString(SCHEDULE_POLI,String.valueOf(schedule));
            editor.putString(DAY_CODE,String.valueOf(day));
            editor.putString(POLI_CODE,String.valueOf(service));
            editor.putString(DOCTOR,String.valueOf(doctor));
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        editor.apply();
    }

    public String getServicesSchedule(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DATA_POLI,Context.MODE_PRIVATE);
        return sharedPreferences.getString(SCHEDULE_POLI,null);
    }

    public String getDayData(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DATA_POLI,Context.MODE_PRIVATE);
        return sharedPreferences.getString(DAY_CODE,null);
    }

    public String getServices(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DATA_POLI,Context.MODE_PRIVATE);
        return sharedPreferences.getString(POLI_CODE,null);
    }

    public String getDoctorList(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(DATA_POLI,Context.MODE_PRIVATE);
        return sharedPreferences.getString(DOCTOR,null);
    }

//    public void storeBooking(String s, String d, String t, String name, String position){
//        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.clear();
////        editor.apply();
//        editor.putString(POLI_NAME,s);
//        editor.putString(BOOKING_DATE,d);
//        editor.putString(BOOKING_TIME,t);
//        editor.putString(BOOKING_NAME,name);
//        editor.putString(BOOKING_LIST_POSITION,position);
//
//        editor.apply();
//    }

    //firstRowName = service name
    //secondRowName = "day, DD-MM-YYYY"
    public void setToExpand(String firstRowName){
        setToExpand(firstRowName,null);
    }

    public void setToExpand(String firstRowName,String secondRowName){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_ROW_TO_EXPAND, firstRowName);
        editor.putString(SECOND_ROW_TO_EXPAND, secondRowName);
        editor.apply();
    }

    public String getFirstRowNameToExpand(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIRST_ROW_TO_EXPAND,null);
    }

    public String getSecondRowNameToExpand(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        return sharedPreferences.getString(SECOND_ROW_TO_EXPAND,null);
    }

    public void expandFirstRowAt(int i){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();

            editor.putInt(EXPAND_FIRST_ROW, i);
            editor.apply();

    }

    public void expandSecondRowAt(int i){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();

            editor.putInt(EXPAND_SECOND_ROW, i);
            editor.apply();

    }

    public int getIndexFirstRowToExpand(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(EXPAND_FIRST_ROW,-1);
    }

    public int getIndexSecondRowToExpand(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(EXPAND_SECOND_ROW,-1);
    }

    public void clearToExpand(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(EXPANDED,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void cancleBooking(String s){
        // TO-DO
        // set cancel notification
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void clearBooking(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getBookingPoliName(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        return sharedPreferences.getString(POLI_NAME,null);
    }

    public String getBookingDate(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        return sharedPreferences.getString(BOOKING_DATE,null);
    }

    public String getBookingTime(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        return sharedPreferences.getString(BOOKING_TIME,null);
    }

    public String getBookingName(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        return sharedPreferences.getString(BOOKING_NAME,null);
    }

    public String getBookingListPosition(String s){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(s,Context.MODE_PRIVATE);
        return sharedPreferences.getString(BOOKING_LIST_POSITION,null);
    }

    public String getContentSlide(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(HOME_CONTENT,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTENT_SLIDE,null);
    }
    public String getContentArticle(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(HOME_CONTENT,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTENT_ARTICLE,null);
    }
    public String getContentService(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(HOME_CONTENT,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTENT_SERVICE,null);
    }

    public boolean isNotificationTurnedOn() {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SWITCH_NOTIFICATION,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NOTIFICATION,true);
    }

    public boolean isSoundTurnedOn() {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SWITCH_NOTIFICATION,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SOUND,true);
    }

    public void turnNotificationOn(boolean io){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SWITCH_NOTIFICATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION,io);
        editor.apply();
    }

    public void turnSoundOn(boolean io){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SWITCH_NOTIFICATION,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(SOUND,io);
        editor.apply();
    }
}
