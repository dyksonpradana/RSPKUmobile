package com.rspkumobile.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rspkumobile.activity.Conversation;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.other.User;
import com.rspkumobile.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by DK on 9/26/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private Intent resultIntent;
    public String title = "RS PKU KUTOARJO";
    private String message = "-";
    private String timestamp = "-";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            Log.e(TAG, "MESSAGE" + message);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        JSONObject data = null;
        boolean isBackground = false;
        String imageUrl = "-";
        String notifType = "-";
        String payload = "-";
        try {
            data = json.getJSONObject("data");


            title = data.getString("title");
            Log.e(TAG, "title: " + title);

            message = data.getString("message");
            Log.e(TAG, "message: " + message);

            isBackground = data.getBoolean("is_background");
            Log.e(TAG, "isBackground: " + isBackground);

            imageUrl = data.getString("image");
            Log.e(TAG, "imageUrl: " + imageUrl);

            timestamp = data.getString("timestamp");
            Log.e(TAG, "timestamp: " + timestamp);

            notifType = data.getString("notifType");
            Log.e(TAG, "notifType: " + notifType);


            if (notifType.equals("conversation")) {
                payload = data.getJSONObject("payload").toString();
            } else if (!notifType.equals("denied")) {
                payload = data.getString("payload");
            }
            Log.e(TAG, "payload: " + payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }




//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {

            HashMap<String, String> value = new HashMap<String, String>();
            // app is in background, show the notification in notification tray
            if (notifType.equals("conversation")) {
//                value.put("type","booking");
//                value.put("head","BOOKING ANTRIAN");
//                value.put("message",message);
//                value.put("note","booking");
//                value.put("key",);
//                Intent pushNotification = new Intent(title);
//                pushNotification.putExtra("topicKey", title);
//                Log.e(TAG,"topicKey"+title);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                final MyFirebaseMessagingService context = this;

                //when notification is clicked, open conversation activity with topic as topicKey value
                resultIntent = new Intent(getApplicationContext(), Conversation.class);
                resultIntent.putExtra("topicKey", title);
                resultIntent.putExtra("notifType", "conversation");

                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_APPROVED + title)
                        .child("title")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                            title[0] = dataSnapshot.getValue().toString();
                                title = dataSnapshot.getValue().toString();

                                Intent pushNotification = new Intent(title);
                                pushNotification.putExtra("topicKey", title);
                                Log.e(TAG,"topicKey"+title);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);

                                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            } else if(notifType.equals("booking")){

//                try {
//                    payload = data.getJSONObject("payload").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                String msg = "";

                if(User.isDoctor(getApplicationContext())){
                    msg = message;
                    resultIntent = new Intent(getApplicationContext(), MainActivityDrawer.class);
                    resultIntent.putExtra("drawer", "booking");
                    resultIntent.putExtra("notifType", "booking");
                    Log.e(TAG,"terimaNotifDokter"+title);
                }else {
                    String time = message;

//                HashMap<String, String> val = new HashMap<String, String>();

                    final DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Config.FURL_USERS)
                            .child(User.getUid())
                            .child("notification")
                            .push();

//                val.put("type", "booking");
//                val.put("time",message);

//                if(payload.equals("skipped")){
//                    SharedPrefManager.getInstance(this).updatePesanAntrianStatus("dilewati");
//                    val.put("head", "skipped");
//                    msg = "antrian anda dilewati";
//                    val.put("note", msg);
//                }else if(payload.equals("canceled")){
////                    SharedPrefManager.getInstance(this).updatePesanAntrianStatus("dibatalkan");
//                    SharedPrefManager.getInstance(getApplicationContext()).cancelBooking();
//                    val.put("head", "canceled");
//                    msg = "antrian anda dibatalkan";
//                    val.put("note", msg);
//                }else if(payload.equals("done")){
////                    SharedPrefManager.getInstance(this).updatePesanAntrianStatus("selesai");
//                    msg = "antrian sukses dilakukan";
//                    // app is in background, show the notification in notification tray
//                    val.put("head", "done");
//                    val.put("note", "giliran anda telah selesai");
//                    SharedPrefManager.getInstance(getApplicationContext()).cancelBooking();
//                }else if(payload.equals("arrived")){
//                    msg = "anda telah mengkonfirmasi antrian";
//                    val.put("head", "arrived");
//                    val.put("note", msg);
//                }else if(payload.equals("notify")){
//                    msg = "sebentar lagi giliran anda, mohon menuju resepsionis";
//                    val.put("head", "notify");
//                    val.put("note", msg);
//                }else{
//                    val.put("head", "notify");
//                    val.put("note", "-");
//                }
//                val.put("read", "false");
//                val.put("key", ref.getKey());

                    NotifHandler notificationLog = new NotifHandler();

                    if (payload.equals(Config.BOOKING_STATUS[1])) {
//                    notificationLog.setHeader(Config.BOOKING_STATUS[1]);
//                    notificationLog.setType("booking");
//                    notificationLog.setNote("anda telah mengkonfirmasi antrian");
//                    notificationLog.setRead(false);
//                    notificationLog.setKey(ref.getKey());
//                    notificationLog.setTime(time);
                        msg = "konfirmasi berhasil dilakukan";
                        notificationLog = new NotifHandler(Config.BOOKING_STATUS[1],
                                title, "booking", false, ref.getKey(), time);

                    } else if (payload.equals(Config.BOOKING_STATUS[3])) {
                        msg = "terima kasih anda telah menggunakan layanan kami";
                        notificationLog = new NotifHandler(Config.BOOKING_STATUS[3],
                                title, "booking", false, ref.getKey(), time);
                    }
                    //notificationlog move to php file for phone number option
//                    ref.setValue(notificationLog);

                    //when notification is clicked, open main activity
                    resultIntent = new Intent(getApplicationContext(), MainActivityDrawer.class);
                    resultIntent.putExtra("drawer", "booking");
                    resultIntent.putExtra("notifType", "booking");
                }

                showNotificationMessage(getApplicationContext(), title, msg, timestamp, resultIntent);

                //denied will be here
            } else {
                resultIntent = new Intent(getApplicationContext(), MainActivityDrawer.class);
                resultIntent.putExtra("drawer", "notification");
                resultIntent.putExtra("notifType", "conversation");
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            }

//            final String[] title = new String[1];
//            if (notifType.equals("denied")){
//                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//            }
//            else

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
