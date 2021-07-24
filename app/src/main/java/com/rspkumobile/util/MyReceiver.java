package com.rspkumobile.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by arif hasnat on 9/18/2016.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
//        NotificationUtils.remainderNotification(context, intent);

//        if(!Config.BOOKING_STATUS[2].equals(intent.getExtras().getString(Config.BOOKING_NOTIF_TYPE))) {
        if(User.isLogedIn()) {
//            if(false) {
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                    .child(User.getUid())
                    .child("booking")
                    .orderByChild("date_time").equalTo(intent.getExtras().getString(Config.BOOKING_NOTIF_PAYLOAD))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("generating alaram", "called receiver method");
                            if (dataSnapshot.getValue() == null) ;
                            else {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    if (data.child("status").getValue(String.class).equals(Config.BOOKING_STATUS_TYPE_EX[0])) {
                                    if (data.child("status").getValue(String.class).equals(Config.BOOKING_STATUS[0])) {
                                        //if not expired

                                        String[] dates = data.child("date").getValue(String.class).replace(" ", "").split(",");
                                        String day = dates[0];
                                        String date = dates[1];
                                        String time = data.child("time").getValue(String.class);
                                        String service = String.valueOf(data.getKey());

//                                        if (Helper.isExpired(context, day, date, time, service)) {
                                            if (Config.BOOKING_STATUS[5].equals(intent
                                                    .getExtras()
                                                    .getString(Config.BOOKING_NOTIF_TYPE))) {

                                                NotificationUtils.remainderNotification(context, intent);

                                                dismissBooking(intent, data);

                                            }else if (Config.BOOKING_STATUS[4].equals(intent
                                                    .getExtras()
                                                    .getString(Config.BOOKING_NOTIF_TYPE))) {
                                                if (!Helper.isExpired(context, day, date, time, service)) {
                                                    NotificationUtils.remainderNotification(context, intent);
                                                }

//                                            }
//                                        } else if (Helper.isLate(context, day, date, time, service)) {
//                                            if (Config.BOOKING_STATUS[4].equals(intent
//                                                    .getExtras()
//                                                    .getString(Config.BOOKING_NOTIF_TYPE))) {
////                                            if (Config.BOOKING_STATUS[4].equals(intent
////                                                    .getExtras()
////                                                    .getString(Config.BOOKING_NOTIF_TYPE))||Config.BOOKING_STATUS[5].equals(intent
////                                                    .getExtras()
////                                                    .getString(Config.BOOKING_NOTIF_TYPE))){
//
//                                                NotificationUtils.remainderNotification(context, intent);

//                                            }
                                            } else if (Config.BOOKING_STATUS[0].equals(intent
                                                        .getExtras()
                                                        .getString(Config.BOOKING_NOTIF_TYPE))) {
                                                if (!Helper.isLate(context, day, date, time, service)) {
                                                    NotificationUtils.remainderNotification(context, intent);
                                                    NotifHandler notifData = new NotifHandler(Config.BOOKING_STATUS[4],data.getKey().toString()+"_"+intent
                                                                .getExtras()
                                                                .getString(Config.BOOKING_NOTIF_PAYLOAD)
                                                            , "booking"
                                                            , false, " ", " ");
                                                    setNotification(notifData);
                                                }

                                            }else
                                                NotificationUtils.remainderNotification(context, intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//        }else{
//
//        }
        }
    }

    private void dismissBooking(Intent intent, DataSnapshot data) {
        //
//        removeBookingFromUser(intent
//                .getExtras()
//                .getString(Config.BOOKING_NOTIF_TITLE));
//
//        setDismissedInBookingList(intent
//                .getExtras()
//                .getString(Config.BOOKING_NOTIF_TITLE),
//                intent
//                .getExtras()
//                .getString(Config.BOOKING_NOTIF_PAYLOAD));

        String note = data.getKey().toString()+"_"+intent
                .getExtras()
                .getString(Config.BOOKING_NOTIF_PAYLOAD);

        NotifHandler notifData = new NotifHandler(Config.BOOKING_STATUS[5],note, "booking", false, " ", " ");
        setNotification(notifData);

        String[] dates = intent
                .getExtras()
                .getString(Config.BOOKING_NOTIF_PAYLOAD).split("_");

        new deleteFromInformationSystemDb(dates[1],dates[0],data.getKey().toString()).execute();
//        {"time":"06:00","date":"Senin, 21-05-2018","service":"POLI ANAK ","uid":"whxb0gvXAOM7vay37yR9sM1zwFs2"}
    }

    public class deleteFromInformationSystemDb extends AsyncTask<Void,Void,Void> {

        private String time,date,service;

        public deleteFromInformationSystemDb(String time, String date, String service) {
            this.time = time;
            this.date = date;
            this.service = service;
        }

        @Override
        protected Void doInBackground(Void... params) {

//            HashMap<String,String> value=new HashMap<>();
//            value.put("date",date);
//            value.put("service",service);
//            value.put("time",time);
//            value.put("uid",User.getUid());

            String value = "{\"date\":\""+date+"\",\"service\":\""+service+"\",\"time\":\""+time+"\",\"uid\":\""+User.getUid()+"\"}";

            Log.e("isdbValue",value);

            HashMap<String,String> param=new HashMap<>();
            param.put("rspku_mobile","clear expired booking");
            param.put("value",value);

            RequestHandler reqH=new RequestHandler();
            String response = reqH.sendPostRequest(Config.ISDB, param);

            Log.e("isdb",response);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    private void setDismissedInBookingList(String poliName, String dates_time) {
        String[] dates = dates_time.split("_");
        String time = dates[1];
        String[] date = dates[0].replace(" ","").split(",");

        //TODO balik date
        String[] dMy = date[1].split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poliName)
                .child(time)
                .child("status").setValue(Config.BOOKING_STATUS[5]);
    }

    private void removeBookingFromUser(String poliName) {

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .child(poliName)
                .removeValue();

    }

    private void setNotification(NotifHandler notification) {

        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(dateTime);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("notification")
                .push();

        notification.setKey(ref.getKey());
        notification.setTime(time);

        ref.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
}
