package com.rspkumobile.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.rspkumobile.R;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by arif hasnat on 9/18/2016.
 */
public class NotificationUtils {

    public static NotificationManager mManager;

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    private String title;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        Log.e(TAG,"showNotificationMessage"+title);
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");


        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, intent.getExtras().getString("notifType"));
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, intent.getExtras().getString("notifType"));
//            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, String nType) {
        Log.e(TAG,"showNotificationMessage2"+title);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

//        Notification notification;
//        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
//                .setAutoCancel(true)
//                .setContentTitle(title)
//                .setContentIntent(resultPendingIntent)
////                .setSound(alarmSound)
//                .setStyle(inboxStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
//                .setContentText(message)
//                .build();

        android.support.v4.app.NotificationCompat.Builder notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
//                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message);

//        Toast.makeText(mContext,"notif "+ SharedPrefManager.getInstance(mContext).isNotificationTurnedOn()+
//                "sound "+ SharedPrefManager.getInstance(mContext).isSoundTurnedOn(),Toast.LENGTH_LONG).show();

        if(SharedPrefManager.getInstance(mContext).isSoundTurnedOn())
            notification.setSound(alarmSound);

        if(SharedPrefManager.getInstance(mContext).isNotificationTurnedOn()) {

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (nType.equals("conversation")) {
//                notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification.build());
//            } else if (nType.equals("booking")) {
                notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification.build());
//            }
        }
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
//        Notification notification;
//        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
//                .setAutoCancel(true)
//                .setContentTitle(title)
//                .setContentIntent(resultPendingIntent)
//                .setSound(alarmSound)
//                .setStyle(bigPictureStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
//                .setContentText(message)
//                .build();


        android.support.v4.app.NotificationCompat.Builder notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message);

//        Toast.makeText(mContext,"notif "+ SharedPrefManager.getInstance(mContext).isNotificationTurnedOn()+
//                "sound "+ SharedPrefManager.getInstance(mContext).isSoundTurnedOn(),Toast.LENGTH_LONG).show();

        if(SharedPrefManager.getInstance(mContext).isSoundTurnedOn())
            notification.setSound(alarmSound);

        if(SharedPrefManager.getInstance(mContext).isNotificationTurnedOn()) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification.build());
        }
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public static void playNotificationSound(Context ctx) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + ctx.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(ctx, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context, int i) {
//        if(title.equals(this.title)) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (i == 0) {
            notificationManager.cancelAll();
        } else if(i == 1){

        }
//        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void remainderSound(Context context){

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        NotificationCompat.Builder nb= new NotificationCompat.Builder(context);
//        nb.setSmallIcon(R.mipmap.ic_launcher);
//        nb.setContentTitle("Booking antrian");
//        nb.setContentText("15 menit menuju antrian anda");
//        nb.setTicker("Take a look");
//
//        nb.setAutoCancel(true);
//
//
//
//        //get the bitmap to show in notification bar
////        Bitmap bitmap_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
////        android.support.v7.app.NotificationCompat.BigPictureStyle s = new android.support.v7.app.NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
////        s.setSummaryText("Brother , Have your read hadish today ?");
////        nb.setStyle(s);
//
//
//
//        Intent resultIntent = new Intent(context, MainActivityDrawer.class);
//        TaskStackBuilder TSB = TaskStackBuilder.create(context);
//        TSB.addParentStack(MainActivityDrawer.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        TSB.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                TSB.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        nb.setContentIntent(resultPendingIntent);
//        nb.setAutoCancel(true);
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // mId allows you to update the notification later on.
//
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(11221, nb.build());


    }


    @SuppressWarnings("static-access")
    public static void remainderNotification(Context context, Intent intent){

        android.support.v7.app.NotificationCompat.Builder nb= new android.support.v7.app.NotificationCompat.Builder(context);
        nb.setSmallIcon(R.mipmap.ic_launcher);
//        nb.setContentTitle("15 Menit Booking");
//        nb.setContentText("diharapkan segera hadir datang di RS");
        nb.setContentTitle("Booking "+ intent.getExtras().getString(Config.BOOKING_NOTIF_TITLE));
        nb.setContentText(intent.getExtras().getString(Config.BOOKING_NOTIF_MESSAGE));
        nb.setTicker("Take a look");

        Log.e("Alaram ringing", "called receiver method");

//        nb.setAutoCancel(true);



        //get the bitmap to show in notification bar
//        Bitmap bitmap_image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        android.support.v7.app.NotificationCompat.BigPictureStyle s = new android.support.v7.app.NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
//        s.setSummaryText("Brother , Have your read hadish today ?");
//        nb.setStyle(s);

        Intent resultIntent = new Intent(context, MainActivityDrawer.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(context);
        TSB.addParentStack(MainActivityDrawer.class);
        // Adds the Intent that starts the Activity to the top of the stack
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                TSB.getPendingIntent(
                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
                        PendingIntent.FLAG_ONE_SHOT
                );

        nb.setContentIntent(resultPendingIntent);
        nb.setAutoCancel(true);
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "/raw/notification");

//        Toast.makeText(context,"notif "+ SharedPrefManager.getInstance(context).isNotificationTurnedOn()+
//                "sound "+ SharedPrefManager.getInstance(context).isSoundTurnedOn(),Toast.LENGTH_LONG).show();

        if(SharedPrefManager.getInstance(context).isSoundTurnedOn())
            nb.setSound(alarmSound);

        if(SharedPrefManager.getInstance(context).isNotificationTurnedOn()) {

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        PemesanModel patient = SharedPrefManager.getInstance(context).getPemesan();

//        if(patient.getAntriStatus()=="antri") {
//            remainderSound(context);
//            notificationManager.notify(Config.NOTIFICATION_ID_BOOKING, nb.build());
            notificationManager.notify((int) System.currentTimeMillis(), nb.build());
//        }
        }
    }

}