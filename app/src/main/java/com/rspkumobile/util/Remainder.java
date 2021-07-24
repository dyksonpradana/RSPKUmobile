package com.rspkumobile.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rspkumobile.app.Config;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by DK on 12/25/2017.
 */

public class Remainder {
//    public Remainder(Context context)
//    {this.ctx=context;}

    String serviceCode;


//    public static void setAlarm(Context ctx, String poliName, String[] d, String t, String type){
    public static void setAlarm(Context ctx, String service, String[] d, String t){

        Log.e("setting","alarm"+service+d[0]+t+d[1]);

        setAlarmBefore(ctx, service, d, t);
        setAlaramAtTime(ctx, service, d, t);
        setAlarmAfter(ctx, service, d, t);

//        Calendar calendar = Calendar.getInstance();

//        PemesanModel patient = SharedPrefManager.getInstance(this).getPemesan();
//        String[] date = patient.getAntriTanggal().split("-");
//        String[] time = patient.getAntriPukul().split(":");
//
//        Log.e(TAG,""+Integer.valueOf(date[0])+Integer.valueOf(date[1])+Integer.valueOf(date[2])
//                +Integer.valueOf(time[0])+Integer.valueOf(time[1]));
//
//        String[] date = d[1].split("-");
//        String[] time = t.split(":");
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.MONTH, Integer.valueOf(date[1])-1);
//        calendar.set(Calendar.YEAR, Integer.valueOf(date[2]));
//        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[0]));
//
//        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
//        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
//        calendar.set(Calendar.SECOND, 0);
//
//        //set notification for date --> 8th January 2015 at 9:06:00 PM
////        calendar.set(Calendar.MONTH, 11);
////        calendar.set(Calendar.YEAR, 2017);
////        calendar.set(Calendar.DAY_OF_MONTH, 25);
////
////        calendar.set(Calendar.HOUR_OF_DAY, 22);
////        calendar.set(Calendar.MINUTE, 45);
////        calendar.set(Calendar.SECOND, 0);
////        calendar.set(Calendar.AM_PM,Calendar.AM);
//
////        Log.e(TAG,calendar.getTime().toString());
//
////        Toast.makeText(this,"notif name booking"+poliName,Toast.LENGTH_LONG).show();
//
//        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
//
//        Intent myIntent = new Intent(ctx, MyReceiver.class);
//        myIntent.setAction("open");
//        myIntent.putExtra(Config.BOOKING_NOTIF_TITLE,String.valueOf(poliName));
//        myIntent.putExtra(Config.BOOKING_NOTIF_PAYLOAD,String.valueOf(d[0]+", "+d[1]+"_"+t));
////        myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[2]);
//
//        String msg = "booking";
//        int offset = 0;
//
////        if (type.equals(Config.BOOKING_STATUS[0])) {
//            msg = "15 menit lagi sebelum waktu booking anda";
//            offset = 15 * 60 * 1000;
//
//            myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
//            myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[0]);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
//                    Integer.valueOf(d[1]+t+Config.BOOKING_STATUS[0]),
//                    myIntent,
//                    0);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-(offset), pendingIntent);
//
////        }else if(type.equals(Config.BOOKING_STATUS[1])){
//            msg = "segera konfirmasi booking sebelum jadwal tutup";
//
//            myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
//            myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[4]);
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
//                    pendingIntent = PendingIntent.getBroadcast(ctx,
//                    Integer.valueOf(d[1]+t+Config.BOOKING_STATUS[4]),
//                    myIntent,
//                    0);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-(offset), pendingIntent);
//
////        } else if(type.equals(Config.BOOKING_STATUS[2])){
//
//            String day = d[0];
//            String[] closeTime = MainActivityDrawer.getCloseTime(ctx,day,poliName).split(":");
//
//            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(closeTime[0]));
//            calendar.set(Calendar.MINUTE, Integer.valueOf(closeTime[1]));
//
//            msg = "booking anda dibatalkan/hangus";
//
//            myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
//            myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[5]);
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
//                    pendingIntent = PendingIntent.getBroadcast(ctx,
//                    Integer.valueOf(d[1]+t+Config.BOOKING_STATUS[5]),
//                    myIntent,
//                    0);
////    Toast.makeText(ctx,calendar.getInstance().getTime().toString(),Toast.LENGTH_LONG).show();
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
////        }
    }

    public static void setAlarmAfter(Context ctx, String service, String[] d, String t) {
        String[] date = d[1].split("-");
        String[] time = t.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.valueOf(date[1])-1);
        calendar.set(Calendar.YEAR, Integer.valueOf(date[2]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[0]));

        String day = d[0];
//        String[] closeTime = MainActivityDrawer.getCloseTime(ctx,day,service).split(":");
        String[] closeTime = Helper.getCloseTime(ctx,day,service).split(":");

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(closeTime[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(closeTime[1]));
        calendar.set(Calendar.SECOND, 0);

        //set notification for date --> 8th January 2015 at 9:06:00 PM
//        calendar.set(Calendar.MONTH, 11);
//        calendar.set(Calendar.YEAR, 2017);
//        calendar.set(Calendar.DAY_OF_MONTH, 25);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 22);
//        calendar.set(Calendar.MINUTE, 45);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.AM_PM,Calendar.AM);

//        Log.e(TAG,calendar.getTime().toString());

//        Toast.makeText(this,"notif name booking"+poliName,Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);

        String msg = "booking anda dibatalkan/hangus";

        Intent myIntent = new Intent(ctx, MyReceiver.class);
        myIntent.setAction("open");
        myIntent.putExtra(Config.BOOKING_NOTIF_TITLE,String.valueOf(service));
        myIntent.putExtra(Config.BOOKING_NOTIF_PAYLOAD,String.valueOf(d[0]+", "+d[1]+"_"+t));
        myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
        myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[5]);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
        //identifier must be an integer
        String identifier = getServiceCode(ctx,service)+date[0]+time[0]+time[1]+5;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                Integer.valueOf(identifier),
                myIntent,
                0);
//    Toast.makeText(ctx,calendar.getInstance().getTime().toString(),Toast.LENGTH_LONG).show();
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void setAlaramAtTime(Context ctx, String service, String[] d, String t) {
        String[] date = d[1].split("-");
        String[] time = t.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.valueOf(date[1])-1);
        calendar.set(Calendar.YEAR, Integer.valueOf(date[2]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[0]));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);

        String msg = "segera konfirmasi booking sebelum jadwal tutup";

        Intent myIntent = new Intent(ctx, MyReceiver.class);
        myIntent.setAction("open");
        myIntent.putExtra(Config.BOOKING_NOTIF_TITLE,String.valueOf(service));
        myIntent.putExtra(Config.BOOKING_NOTIF_PAYLOAD,String.valueOf(d[0]+", "+d[1]+"_"+t));
        myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
        myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[4]);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
        //identifier must be an integer
        String identifier = getServiceCode(ctx,service)+date[0]+time[0]+time[1]+4;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                Integer.valueOf(identifier),
                myIntent,
                0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void setAlarmBefore(Context ctx, String service, String[] d, String t) {
        String[] date = d[1].split("-");
        String[] time = t.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.valueOf(date[1])-1);
        calendar.set(Calendar.YEAR, Integer.valueOf(date[2]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[0]));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.SECOND, 0);

        //set notification for date --> 8th January 2015 at 9:06:00 PM
//        calendar.set(Calendar.MONTH, 11);
//        calendar.set(Calendar.YEAR, 2017);
//        calendar.set(Calendar.DAY_OF_MONTH, 25);
//
//        calendar.set(Calendar.HOUR_OF_DAY, 22);
//        calendar.set(Calendar.MINUTE, 45);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.AM_PM,Calendar.AM);

//        Log.e(TAG,calendar.getTime().toString());

//        Toast.makeText(this,"notif name booking"+poliName,Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(ctx, MyReceiver.class);
        myIntent.setAction("open");
        myIntent.putExtra(Config.BOOKING_NOTIF_TITLE,String.valueOf(service));
        myIntent.putExtra(Config.BOOKING_NOTIF_PAYLOAD,String.valueOf(d[0]+", "+d[1]+"_"+t));
//        myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[2]);

        String msg = "booking";
        int offset = 0;

//        if (type.equals(Config.BOOKING_STATUS[0])) {
        msg = "15 menit lagi sebelum waktu booking anda";
        offset = 15 * 60 * 1000;

        myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
        myIntent.putExtra(Config.BOOKING_NOTIF_TYPE,Config.BOOKING_STATUS[0]);
        //identifier must be an integer
        String identifier = getServiceCode(ctx,service)+date[0]+time[0]+time[1]+0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                Integer.valueOf(identifier),
                myIntent,
                0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-(offset), pendingIntent);
    }

    public static void setAlarmCancel(Context ctx, String service, String d, String t){
        String[] dates = d.replace(" ","").split(",");
        String[] date = dates[1].split("-");
        String[] time = t.split(":");

        Intent myIntent = new Intent(ctx, MyReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, Integer.valueOf(getServiceCode(ctx,service)+date[0] + time[0] + time[1]), myIntent, 0);
//        PendingIntent.getBroadcast(ctx,
//                Integer.valueOf(date[0] + time[0] + time[1]+i),
//                myIntent,
//                PendingIntent.FLAG_NO_CREATE);

        for(int i = 0; i<Config.BOOKING_STATUS.length; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, Integer.valueOf(getServiceCode(ctx,service) + date[0] + time[0] + time[1]+i), myIntent, 0);
            boolean alarmUp = (pendingIntent != null);

            if (alarmUp) {
                AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    public static String getServiceCode(Context ctx, String service){
        JSONObject services = null;
        try {
            services = new JSONObject(SharedPrefManager
                    .getInstance(ctx)
                    .getServices());
            Log.e("serviceCode",SharedPrefManager
                    .getInstance(ctx)
                    .getServices());
            Iterator dayKeys = services.keys();
            while(dayKeys.hasNext()) {
                String serviceId = (String) dayKeys.next();
                String serviceName = services.getString(serviceId);

                Log.e("serviceCodeId "+service,serviceId+" "+serviceName);

                if(service.equals(serviceName)){
                    return serviceId;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
