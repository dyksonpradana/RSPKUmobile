package com.rspkumobile.app;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DK on 9/26/2017.
 */

public class Config {
    // global topic to receive app wide push action_bar_notification

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID_CONSULTATION = 1;
    public static final int NOTIFICATION_ID_BOOKING = 2;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String ID_NOTIF_USER="id_notif";
    public static final String SENDER_TOKEN="sender_token";
    public static int BOOKING_HOUR_START = 0;
    public static int BOOKING_HOUR_END = 24;

    public static int TIME_PICKER_INTERVAL_MIN=10;
    public static List<Integer> Interval= Arrays.asList(0, 10, 20, 30, 40, 50);

    //phone tethering
//    public static final String IP = "192.168.43.190";
//    public static final String IP = "192.168.43.194";
    //phone CABLE tethering doesn't work
//    public static final String IP = "192.168.42.200";
    //LAN
//    public static final String IP = "169.254.34.171";
//    public static final String IP = "169.254.17.22";
    //for testing using virtual device
//    public static final String IP="10.0.2.2";
    //hosted network
    public static final String IP="192.168.137.1";

    public static final String ROOT_URL="http://"+IP+"/";

    public static final String ROOT=ROOT_URL+"rs_pku/car/api.php?apicall=";
//    Arrays.asList(0, 10, 20, 30, 40, 50);
//    public static final String ROOT = ROOT_URL+"rs_pku/api.php?apicall=";
    public static final String HOME = ROOT+"home";
    public static final String PREFETCH = ROOT+"prefetch";
    public static final String FEATURES = PREFETCH;
    public static final String NOTIFY = ROOT+"notify";
    public static final String DOCTOR_LOGIN = ROOT+"login";
    public static final String ISDB = ROOT+"isdb";
//    public static final String PESAN=ROOT+"pesan";
//    public static final String NOTIF=ROOT+"firebase_message";
//    public static final String FIREBASE_MESSAGE=ROOT+"firebase_message";
//    public static final String ANTRIAN=ROOT+"antrian";
//    public static final String BATALKAN_ANTRIAN=ROOT+"batal";
//    public static final String LIHAT_TANGGAL=ROOT+"tanggal";
    public static String WEB_ADDRESS="https://pkumuhammadiyahkutoarjo.blogspot.co.id";

    public static final String ON_SIGNIN_SUCCESS="signed_in";

    public static final String FURL="https://rs-pku-kutoarjo-fdb.firebaseio.com/";
    public static final String FURL_RESERVATION=FURL+"reservation/";
    public static final String FURL_CONSULTATION=FURL+"online-consultation/";
    public static final String FURL_USERS=FURL+"users/";
    public static final String FURL_DOCTORS=FURL+"doctors/";
    public static final String FURL_CONV_APPROVED =FURL_CONSULTATION+"topic/approved/";
    public static final String FURL_CONV_REQ=FURL_CONSULTATION + "topic/request/";
    public static String FURL_TIPS=FURL+"tips/";
    public static final String TOPIC_GLOBAL = "global";


    private static final String TAG = Config.class.getSimpleName();
    private static String uid;
    private static Context context;
    public static int FLIPPER_INTERVAL = 5000;
    public static String LOCATION = "-7.720009,109.912234";
    public static String TELEPHONE = "0275642439";

    public static String[] BOOKING_STATUS_TYPE_EX = new String[]{"booking","waiting","done","cencel","expired"};
    public static String BOOKING_NOTIF_TITLE = "title";
    public static String BOOKING_NOTIF_PAYLOAD = "payload";
    public static String BOOKING_NOTIF_MESSAGE = "message";
    public static String BOOKING_NOTIF_TYPE = "type";

    public static String[] BOOKING_STATUS = new String[]{"booking","waiting","on service","done","late","expired"};
//    public static String[] BOOKING_STATUS_TYPE_EX = BOOKING_STATUS;


    public Config(){
    }

    public Config(Context ctx){
        this.context = ctx;
    }

    public void setWebAddress(String s){
        this.WEB_ADDRESS = s;
    }

    public void setBookingHourStart(int i){
        this.BOOKING_HOUR_START = i;
    }

    public void setBookingHourEnd(int i){
        this.BOOKING_HOUR_END = i;
    }

    public void setLocation(String s){
        this.LOCATION = s;
    }

    public void setTelephone(String s){
        this.TELEPHONE = s;
    }

//    public static void updateCurrentUserDetail(){
//
//        Map userDetail = new HashMap();
//        Log.d(TAG,"uid"+FirebaseAuth.getInstance().getCurrentUser().getUid());
//        userDetail.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        userDetail.put("token", FirebaseInstanceId.getInstance().getToken());
//        userDetail.put("userDetail",FirebaseAuth.getInstance().getCurrentUser());
//
//        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
//            userDetail.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        }else userDetail.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//
//        FirebaseDatabase.getInstance().getReferenceFromUrl(FURL_USERS
//                +FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userDetail);
//
//    }
//
//    public static void deleteUserToken(String uid){
//        //using uid because we will lost uid since onComplite operation
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS
//                +uid
//                +"/token").removeValue();
//    }

}
