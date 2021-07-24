package com.rspkumobile.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.drawer.Booking;
import com.rspkumobile.drawer.ConversationList;
import com.rspkumobile.drawer.Home;
import com.rspkumobile.drawer.Notification;
import com.rspkumobile.fragment.SetBooking;
import com.rspkumobile.model.ConvHandler;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;
import com.rspkumobile.other.CircleTransform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivityDrawer extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtHospitalName, txtUserName;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "";
    private static final String urlProfileImg = "";
//    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
//    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";


    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BOOKING = "booking";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_CONSULTATION = "consultation";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private static final String TAG = MainActivityDrawer.class.getSimpleName();
    private int hari;
    private String Uid;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int SIGN_IN_REQUEST_CODE = 001;
    private PendingIntent pendingIntent;
    private static AlarmManager alarmManager;
    public boolean connected, show = true;
    private boolean firstUp = false;
    private Snackbar snack;
    private boolean reqExist = false;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private ProgressDialog pDialog;
    private MenuItem loginBtn;
    private String requestTitle;
    private String requestMsg;
    private DatabaseReference requestRef;

//    private Context ctx = getApplicationContext();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

//        SharedPrefManager.getInstance(this).cancelBooking();

//        countQueue();

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loginBtn = navigationView.getMenu().findItem(R.id.login_btn);
//        loginBtn = navigationView.getMenu().getItem(5).getSubMenu().getItem(3);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtHospitalName = (TextView) navHeader.findViewById(R.id.hospitale_name);
        txtUserName = (TextView) navHeader.findViewById(R.id.user_name);
//        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.hide();
        checkRequest();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action_bar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                startActivity(new Intent(getApplication(), NewTopicReq.class));
//                if(User.isLogedIn())checkRequest();
//                else Helper.requireLogin(MainActivityDrawer.this);
                if(!User.isLogedIn()) Helper.requireLogin(MainActivityDrawer.this);
                else{
                    if(fab.isFocusable()){
                        requestDialog();
                    }else alertHasRequest(requestTitle,requestMsg,requestRef);
                }
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

//                Toast.makeText(getApplicationContext(), "Push notification: ", Toast.LENGTH_LONG).show();

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide action_bar_notification
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

//                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Log.d("notif Main",message);

//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

//                    txtMessage.setText(message);
                } else {
//                    Toast.makeText(getApplicationContext(), "notification: ", Toast.LENGTH_LONG).show();
                }
            }
        };
    // alarm test
//        setAlarm();
//        Log.e(TAG,"0"+String.valueOf("09:00").compareTo("06:00"));
//        Log.e(TAG,"1"+String.valueOf("10:00").compareTo("7:00"));
//        Log.e(TAG,"1"+String.valueOf("10:00").compareTo("8:00"));
//        Log.e(TAG,"2"+String.valueOf("10:00").compareTo("06:00"));
//        Log.e(TAG,"3"+String.valueOf("10:00").compareTo("09:00"));
//        Log.e(TAG,"4"+String.valueOf("10:00").compareTo("11:00"));
//        Log.e(TAG,"5"+String.valueOf("10:00").compareTo("12:00"));
//        Log.e(TAG,"6"+String.valueOf("09:00").compareTo("06:00"));
//
//        Log.e(TAG,"0"+String.valueOf("09:00").compareTo("06:10"));
//        Log.e(TAG,"1"+String.valueOf("10:00").compareTo("7:10"));
//        Log.e(TAG,"1"+String.valueOf("10:00").compareTo("8:30"));
//        Log.e(TAG,"2"+String.valueOf("10:00").compareTo("06:10"));
//        Log.e(TAG,"3"+String.valueOf("10:00").compareTo("09:30"));
//        Log.e(TAG,"4"+String.valueOf("10:00").compareTo("11:10"));
//        Log.e(TAG,"5"+String.valueOf("10:00").compareTo("12:30"));
//        Log.e(TAG,"6"+String.valueOf("09:00").compareTo("06:30"));
//
//        Log.e(TAG,"0"+String.valueOf("09:10").compareTo("06:10"));
//        Log.e(TAG,"1"+String.valueOf("10:20").compareTo("7:10"));
//        Log.e(TAG,"1"+String.valueOf("10:20").compareTo("8:30"));
//        Log.e(TAG,"2"+String.valueOf("10:30").compareTo("06:10"));
//        Log.e(TAG,"3"+String.valueOf("10:10").compareTo("09:30"));
//        Log.e(TAG,"4"+String.valueOf("10:10").compareTo("11:10"));
//        Log.e(TAG,"5"+String.valueOf("10:30").compareTo("12:30"));
//        Log.e(TAG,"6"+String.valueOf("09:30").compareTo("06:10"));

    }

    public void signIn(){
        Helper.signIn(MainActivityDrawer.this);

//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setLogo(R.drawable.logo192x192)
//                        .setAvailableProviders(
//                                Arrays.asList(
//                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
//                                        ,new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//                                ))
//                        .build(),
//                SIGN_IN_REQUEST_CODE
//        );
    }

    @Override
    protected void onStop() {
        super.onStop();
//        firstUp = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Helper.onActivityResult(MainActivityDrawer.this,requestCode,resultCode,data);

//        if(requestCode == SIGN_IN_REQUEST_CODE) {
//
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if(resultCode == RESULT_OK) {
//
//                SharedPrefManager.getInstance(MainActivityDrawer.this).turnNotificationOn(true);
//                SharedPrefManager.getInstance(MainActivityDrawer.this).turnSoundOn(true);
//
////                Toast.makeText(this,
////                        response.getProviderType(),
////                        Toast.LENGTH_LONG)
////                        .show();
//
//                Log.e("Sing IN", "Firebase reg id: " + FirebaseInstanceId.getInstance().getToken());
////                startActivity(new Intent(this, MainActivityDrawer.class)
////                        .putExtra(Config.ON_SIGNIN_SUCCESS, FirebaseInstanceId.getInstance().getToken()));
//
////                ((MainActivityDrawer)getApplicationContext()).myOnResume();
////                setTitle(R.string.layout_sign_in_display_name);
//                User.updateCurrentUserDetail();
//
////                if(response.getProviderType().equals("phone")) {
////                    Toast.makeText(this,
////                            response.getProviderType(),
////                            Toast.LENGTH_LONG)
////                            .show();
//////                    finish();
////                    startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//////                    startActivity(new Intent(MainActivityDrawer.this,AboutUsActivity.class));
////                }
//
//                //is doctor
//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
//                        .child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.getValue()==null) {
//                            recreate();
//                            startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//                        }
//                        else {
//
//                            String name = dataSnapshot.child("name").getValue(String.class);
//                            String nip = dataSnapshot.child("nip").getValue(String.class);
//
//                            Log.e("login doctor",dataSnapshot.getValue().toString());
//                            Log.e("login doctor",name + nip);
//
//                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(name)
//                                    .build();
//
//                            SharedPrefManager.getInstance(MainActivityDrawer.this).setAsDoctor(nip);
//
//                            User.setDisplayName(profileUpdate);
//
//                            User.updateCurrentUserDetail();
//
//                            Toast.makeText(MainActivityDrawer.this,"selamat datang Dr. "+name,Toast.LENGTH_LONG).show();
//
//                            recreate();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
////                return;
//
//            } else {
//                // Close the app
////                finish();
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Toast.makeText(this,
//                            R.string.login_canceled,
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//
//                }
//
//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(this,
//                            "no network",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    Toast.makeText(this,
//                            "error",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//            }
////            Toast.makeText(this,
////                    "error",
////                    Toast.LENGTH_LONG)
////                    .show();
//////            finish();
////            return;
//        }

    }

//    public void setAlarm(Context ctx, String poliName, String[] d, String t, String type){
//
//        String msg = "booking";
//
//        if (type.equals(Config.BOOKING_STATUS[0])){
//            msg = "15 menit menuju antrian anda";
//        } else if(type.equals(Config.BOOKING_STATUS[1])){
//            msg = "booking anda dibatalkan";
//        }
//
//        Calendar calendar = Calendar.getInstance();
//
////        PemesanModel patient = SharedPrefManager.getInstance(this).getPemesan();
////        String[] date = patient.getAntriTanggal().split("-");
////        String[] time = patient.getAntriPukul().split(":");
////
////        Log.e(TAG,""+Integer.valueOf(date[0])+Integer.valueOf(date[1])+Integer.valueOf(date[2])
////                +Integer.valueOf(time[0])+Integer.valueOf(time[1]));
////
//        String[] date = d[1].split("-");
//        String[] time = t.split(":");
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
//        Log.e(TAG,calendar.getTime().toString());
//
////        Toast.makeText(this,"notif name booking"+poliName,Toast.LENGTH_LONG).show();
//
//        Intent myIntent = new Intent(this, MyReceiver.class);
//        myIntent.putExtra(Config.BOOKING_NOTIF_TITLE,String.valueOf(poliName));
//        myIntent.putExtra(Config.BOOKING_NOTIF_PAYLOAD,String.valueOf(d[0]+", "+d[1]+"_"+t));
//        myIntent.putExtra(Config.BOOKING_NOTIF_MESSAGE,String.valueOf(msg));
//        pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(date[0]+time[0]+time[1]), myIntent,0);
//
//        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-(15*60*1000), pendingIntent);
//    }
//
//    public void setAlarmCancel(String d, String t){
//        String[] dates = d.replace(" ","").split(",");
//        String[] date = dates[1].split("-");
//        String[] time = t.split(":");
//
//        Intent myIntent = new Intent(this, MyReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(date[0]+time[0]+time[1]), myIntent,0);
//        boolean alarmUp = (PendingIntent.getBroadcast(this,
//                Integer.valueOf(date[0]+time[0]+time[1]),
//                myIntent,
//                PendingIntent.FLAG_NO_CREATE)!=null);
//        if(alarmUp) {
//            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//            alarmManager.cancel(pendingIntent);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(User.isLogedIn()) clearExpiredBooking(this);
        if(User.isLogedIn()) loginBtn.setTitle("Keluar");
        else loginBtn.setTitle("Masuk");

        // load nav menu header data
//        loadNavHeader();

        // updating content data
        new updateContent().execute();
//        new getServiceData().execute();
//        new getHomeContent().execute();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());

        //notify when connection lost
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(Boolean.class);
                        if(!connected&&!firstUp){
//                            Toast.makeText(getApplicationContext(),"koneksi internet terputus",Toast.LENGTH_LONG).show();
                        }else if(connected)firstUp = true;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error " + databaseError,Toast.LENGTH_LONG).show();
                    }
                });

        showNotificationIcon();

    }

    public static void clearExpiredBooking(final Context ctx) {

//        JSONObject schedule = null;

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot children : dataSnapshot.getChildren()){
//                            if(children.child("status").getValue(String.class).equals(Config.BOOKING_STATUS_TYPE_EX[0])) {
                            if(children.child("status").getValue(String.class).equals(Config.BOOKING_STATUS[0])) {
                                final String[] dates = children.child("date").getValue().toString().replace(" ","").split(",");
                                String time = children.child("time").getValue().toString();

                                String day = dates[0];
                                String date = dates[1];
                                String poliName = children.getKey();

                                String closeTime = Helper.getCloseTime(ctx,day,poliName);

//                                Log.e("clear expiredbooking", bookingTimeInMilis +" "+System.currentTimeMillis());

                                if (isExpired(ctx,date,time,closeTime)) {
                                    children.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
//                                    children.getRef().child("status").setValue("dismissed").addOnCompleteListener(new OnCompleteListener<Void>() {Ì
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ctx,"clear expired",Toast.LENGTH_LONG).show();

                                            clearBookingList(ctx,dates, children.getKey(), children.child("time").getValue(String.class));
                                        }
                                    });
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        ArrayList<String> polis = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//        JSONObject poli = null;
//        try {
//            poli = new JSONObject(SharedPrefManager.getInstance(MainActivityDrawer.this)
//                    .getServicesSchedule());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if(poli!=null) {
//            Iterator poliKey = poli.keys();
//            while (poliKey.hasNext()) {
//                try {
////                    polis.add(poli.getString((String) poliKey.next()));
//                    String bookingDate = SharedPrefManager.getInstance(MainActivityDrawer.this)
//                            .getBookingDate(poli.getString((String) poliKey.next()));
//
//                    if(bookingDate !=null) {
//                        try {
//                            Date date = sdf.parse(bookingDate);
//                            if (System.currentTimeMillis() > date.getTime() + 24 * 60 * 60 * 1000) {
//                                SharedPrefManager.getInstance(MainActivityDrawer.this)
//                                        .clearBooking(poli.getString((String) poliKey.next()));
//                            }
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
////                    Log.e("MILIES", date.getTime() + " " + System.currentTimeMillis());
//                    //if greater than 1 hour (minute*second*mili)
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private static boolean isExpired(Context ctx, String date, String time, String closeTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");

        long bookingTimeInMilis = 0;
        try {
            bookingTimeInMilis = sdf.parse(date+" "+time).getTime();
//                                    bookingTimeInMilis += 1000 * 60 * 60 * 24;
            //
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long closeTimeInMilis = 0;
        long chace = 0;


        try {
            chace = sdf2.parse(date).getTime() + (1000*60*60*24);
            closeTimeInMilis = sdf.parse(date+" "+closeTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Toast.makeText(ctx,bookingTimeInMilis+">"+closeTimeInMilis,Toast.LENGTH_LONG).show();

        Log.e("close time",date+" "+time+" "+bookingTimeInMilis+">"+closeTimeInMilis+" "+date+" "+closeTime);

//        if(bookingTimeInMilis>closeTimeInMilis) return true;
//        if(bookingTimeInMilis<System.currentTimeMillis()) return true;
        if(System.currentTimeMillis()>closeTimeInMilis) return true;
        return false;
    }

    public static String getCloseTime(Context ctx, String day, String poliName) {
        String close = "23:59";
        try {

            JSONObject dayOfWeek = new JSONObject(SharedPrefManager
                    .getInstance(ctx)
                    .getDayData());

            Iterator dayKeys =dayOfWeek.keys();
            String relatedDayId = null;
            while(dayKeys.hasNext()) {
                String dayId = (String) dayKeys.next();
                String dayName = dayOfWeek.getString(dayId);

                if(day.equals(dayName)){
                    relatedDayId = dayId;
                    break;
                }

            }

            JSONObject schedule = new JSONObject(SharedPrefManager.getInstance(ctx).getServicesSchedule());

            JSONArray shifts = schedule.getJSONObject(poliName).getJSONArray(relatedDayId);

            close = shifts.getJSONObject(shifts.length() - 1).getString("tutup");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return close;
    }

    public void myRecreate(){
        recreate();
    }

    private static void clearBookingList(final Context ctx, final String[] date, final String poliName, final String bookingTime) {
        String[] dMy = date[1].split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poliName)
                .child(bookingTime)
//                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
                .child("status").setValue("dismissed")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    Calendar cal = Calendar.getInstance();
                    Date dateTime = cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    final String time = sdf.format(dateTime);

                    final DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Config.FURL_USERS)
                            .child(User.getUid())
                            .child("notification")
                            .push();

                    NotifHandler notifData = new NotifHandler("dismissed",
                            poliName+"_"+date[0]+", "+date[1]+"_"+bookingTime,
                            "booking", false, ref.getKey(), time);

                    ref.setValue(notifData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ctx,"clearbookingLIst",Toast.LENGTH_LONG).show();
    //                        Remainder.setAlarm(ctx,poliName,date,bookingTime,Config.BOOKING_STATUS[1]);
    //                        setAlarm(ctx,poliName,date,bookingTime,Config.BOOKING_STATUS[1]);
                        }
                    });

                    }
            });
    }

    public void showNotificationIcon(){
        if(User.isLogedIn()) {
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                    .child(User.getUid())
                    .child("notification")
                    .orderByChild("read").equalTo(false)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e(TAG,dataSnapshot.getValue().toString());
                            if (dataSnapshot.getValue() != null) {
                                Log.e(TAG,"unread notificaton ->"+dataSnapshot.getValue().toString());
                                navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
                            }else navigationView.getMenu().getItem(3).setActionView(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }else navigationView.getMenu().getItem(3).setActionView(null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setDisplayNameTextView();

        setToolbarTitle();

//        Helper.hideKeyboard(this);

        //save firebase data localy when offline
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        new getHomeContent().execute();
//        new getServicesSchedule().execute();
    }

    private void setDisplayNameTextView() {
        if(User.isLogedIn()){
            String nickname= "";
            if (User.isDoctor(MainActivityDrawer.this))
                nickname = "Dr. ";
            else nickname = "Sdr/i ";

            nickname += User.getDisplayName();
            txtUserName.setText(nickname);
        }else txtUserName.clearComposingText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefManager.getInstance(MainActivityDrawer.this).clearBooking("Poli Anak");
        SharedPrefManager.getInstance(MainActivityDrawer.this).clearBooking("Poli Umum");
        SharedPrefManager.getInstance(MainActivityDrawer.this).clearBooking("Poli Gigi");
    }

    private void redirectSignIn(){
        new AlertDialog.Builder(this)
                .setTitle("Perhatian")
                .setMessage("anda harus Log In untuk menggunakan fasilitas ini"
                        + "\nLog In sekarang?"
                )
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.signIn(MainActivityDrawer.this);
//                        signIn();

                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    // this class same as prefetch data on SplashScreenActivity
    // but there is only the background process, there will not be alertdialog or anything
    // in case there content update while you already running the application
    public class updateContent extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            HashMap<String,String> param=new HashMap<>();
            // "rspku_mobile" is the variable of php POST method to fetch particular data
            // following by the php POST method value "prefetch data"
            param.put("rspku_mobile","fetch data");

            // create a new class to handle request to API
            RequestHandler reqH=new RequestHandler();
            // send request to the server through API and catch the response
            // "Config.HOME is the link server with particular php POST value to reach the API
            String response = reqH.sendPostRequest(Config.FEATURES, param);

            Log.e("fetching", String.valueOf(response));

            JSONObject json = null;
            String galery = null;
            String article = null;
            String services = null;

            try {
                // generate json object from string stored in variable response
                json = new JSONObject(String.valueOf(response));

                // get and store object's value of article in shared preferences variable
                article = json.getJSONArray("article").toString().replace("localhost",Config.IP);
                if(article!=null)
                    SharedPrefManager.getInstance(getApplicationContext()).storeArticle(article);
                Log.e("fetchArticle",TAG+SharedPrefManager.getInstance(getApplicationContext()).getArticle());

                // get and store object's value of gallery in shared preferences variable
                galery = json.getJSONArray("gallery").toString().replace("localhost",Config.IP);
                if(galery!=null)
                    SharedPrefManager.getInstance(getApplicationContext()).storeGallery(galery);
                Log.e("fetchGallery",TAG+SharedPrefManager.getInstance(getApplicationContext()).getGallery());

                services = json.getJSONObject("features").toString().replace("localhost",Config.IP);
                if(services!=null)
                    SharedPrefManager.getInstance(getApplicationContext()).storeFeaturesData(services);
                Log.e("service",TAG+SharedPrefManager.getInstance(getApplicationContext()).getServices());
                Log.e("doctor",TAG+SharedPrefManager.getInstance(getApplicationContext()).getDoctorList());
                Log.e("schedule",TAG+SharedPrefManager.getInstance(getApplicationContext()).getServicesSchedule());
                Log.e("dataHari",TAG+SharedPrefManager.getInstance(getApplicationContext()).getDayData());
//                if(!homeContent.isEmpty())

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("prefetchError","error");
//                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    public class getHomeContent extends AsyncTask<Void,String,String> {
//        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
//            pDialog = new ProgressDialog(getApplicationContext());
//            pDialog.setMessage("Memuat...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
            Log.e("masuk_x","masuk home");
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler reqH=new RequestHandler();

            HashMap<String,String>params=new HashMap<>();
            params.put("rspku_mobile","home content");
//            params.put("payload", String.valueOf(payload.sendNotif()));
//            params.put("topic",topicKey);
//            params.put("type","conversation");

//            params.put("antriTanggal", pemesan.getAntriTanggal());
//            params.put("antriJam", pemesan.getAntriPukul());

            String x = reqH.sendPostRequest(Config.HOME, params);

            try {
                x = new JSONObject(x).getJSONObject("response").toString();
            } catch (JSONException e) {
                x = "";
                e.printStackTrace();
            }

            Log.e("paramsx", String.valueOf(x));

//            return reqH.sendPostRequest(Config.HOME,params);
            if(!x.isEmpty())
                SharedPrefManager.getInstance(getApplicationContext()).storeHomeContent(x.toString().replace("localhost",Config.IP));
            return x;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            pDialog.dismiss();
            Log.d("responsex",s);
//            Toast.makeText(getApplicationContext(),s.toString(),Toast.LENGTH_LONG).show();
//            SharedPrefManager.getInstance(getApplicationContext()).storeHomeContent(s.toString());


        }
    }

    public class getServiceData extends AsyncTask<Void,String,String> {
//        private ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            Log.e("masuk_x","masuk poli");
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler reqH=new RequestHandler();

            HashMap<String,String>params=new HashMap<>();
            params.put("rspku_mobile","services data");
//            params.put("payload", String.valueOf(payload.sendNotif()));
//            params.put("topic",topicKey);
//            params.put("type","conversation");

//            params.put("antriTanggal", pemesan.getAntriTanggal());
//            params.put("antriJam", pemesan.getAntriPukul());
            String x = reqH.sendPostRequest(Config.FEATURES, params);
            if(!x.isEmpty())
                SharedPrefManager.getInstance(getApplicationContext()).storeFeaturesData(x.toString());

            Log.e("paramsx", String.valueOf(x));
            return x;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("responsex",s);
        }
    }
//    public class loading extends AsyncTask<Void,Boolean,Boolean>{
//    public class getHomeContent extends AsyncTask<Void,Boolean,Boolean>{
//
//    private ProgressDialog pDialog;
//    boolean check;
//
//    @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(getApplication());
//            pDialog.setMessage("memuat...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
////            pDialog.show();
//        Toast.makeText(getApplicationContext(),"pre123", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            check = true;
//            StringRequest stringRequest = new StringRequest(Request.Method.POST,Config.HOME,
//                    new Response.Listener<String>() {
//                        JSONObject json = null;
//                        @Override
//                        public void onResponse(String response) {
//                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
//                            try {
//                                json = new JSONObject(response);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Log.i("res123",response+"-"+json);
//                            Toast.makeText(getApplicationContext(),"res123", Toast.LENGTH_SHORT).show();
//                            if(json.length()<1)check=false;
////                            else drawSchedule(json);
////                        text.setText(response);
//                        }
//                    },
//                    new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    });
//            RequestQueue req= Volley.newRequestQueue(getApplicationContext());
//            req.add(stringRequest);
//
//            Toast.makeText(getApplicationContext(),"back123", Toast.LENGTH_SHORT).show();
//
//            return check;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            Toast.makeText(getApplicationContext(),"pos123", Toast.LENGTH_SHORT).show();
////            if(s)Toast.makeText(JadwalActivity.this, "menampilkan sukses", Toast.LENGTH_SHORT).show();
////            else Toast.makeText(JadwalActivity.this, "terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        firstUp = true;
    }

    public void myOnResume(int i){

        if (i==0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }else if(i==1){
            navItemIndex = 1;
            CURRENT_TAG = TAG_BOOKING;
        }else if(i==2){
            navItemIndex = 2;
            CURRENT_TAG = TAG_CONSULTATION;
        }else if(i==3){
//            navItemIndex = 0;
//            CURRENT_TAG = TAG_HOME;
//            loadHomeFragment();
            navItemIndex = 3;
            CURRENT_TAG = TAG_NOTIFICATIONS;
        }
        loadHomeFragment();

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(1);
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view){
        showDialog(2);
    }

    @Override
    public Dialog onCreateDialog(int id) {
        if (id == 1) {
            return new DatePickerDialog(this,
                    myDateListener,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }else if (id == 2){
//            return new CustomTimePickerDialog(this,AlertDialog.THEME_TRADITIONAL,myTimeListener,hour,minute,true);
            return new MainActivityDrawer.CustomTimePickerDialog(this,
                    AlertDialog.THEME_HOLO_LIGHT,
                    myTimeListener,
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // arg1 = year
                    // arg2+1 = month -- return index 0 to 11, so we add 1 instead
                    // arg3 = day

//                    SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy");
//                    Log.e("tanggal arg0", dateOnly.format(arg0));
                    showDate(arg1, arg2+1, arg3);
                }

            };

    public class CustomTimePickerDialog extends TimePickerDialog {

        private TimePicker mTimePicker;

        public CustomTimePickerDialog(Context context, int themeResId, OnTimeSetListener callBack, int hourOfDay, int minute,
                                      boolean is24HourView) {
            super(context, themeResId, callBack, hourOfDay, minute, is24HourView);
        }

        @Override
        public void onAttachedToWindow(){
            super.onAttachedToWindow();
            try {
                Class classForid=Class.forName("com.android.internal.R$id");
                Field timePickerField=classForid.getField("timePicker");
                this.mTimePicker=(TimePicker) findViewById(timePickerField.getInt(null));
                Field field=classForid.getField("minute");
                Field field2=classForid.getField("hour");

                NumberPicker minuteSpinner=(NumberPicker) mTimePicker
                        .findViewById(field.getInt(null));
                NumberPicker hourSpinner=(NumberPicker) mTimePicker
                        .findViewById(field2.getInt(null));

                hourSpinner.setMinValue(Config.BOOKING_HOUR_START);
                hourSpinner.setMaxValue(Config.BOOKING_HOUR_END-1);
                minuteSpinner.setMinValue(0);
                minuteSpinner.setMaxValue((60/ Config.TIME_PICKER_INTERVAL_MIN)-1);
                List<String> displayedValues=new ArrayList<>();
                for(int i=0;i<60;i+=Config.TIME_PICKER_INTERVAL_MIN){
                    displayedValues.add(String.format("%02d",i));
                }
                minuteSpinner.setDisplayedValues(displayedValues
                        .toArray(new String[displayedValues.size()]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CustomTimePickerDialog.OnTimeSetListener myTimeListener = new
            CustomTimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showTime(hourOfDay, minute);
                }

            };

    private void showDate(int year, int month, int day) {

        hari=day;

        Boolean expired=false,outOfRange=true;

        //check apakah tanggal yang dipilih kadaluarsa atau tidak(expired)
        if(year<Calendar.getInstance().get(Calendar.YEAR)){
            expired=true;
        }else if(year==Calendar.getInstance().get(Calendar.YEAR)&&
                month<Calendar.getInstance().get(Calendar.MONTH)+1){
            expired=true;
        }else if(year==Calendar.getInstance().get(Calendar.YEAR)&&
                month==Calendar.getInstance().get(Calendar.MONTH)+1&&
                day<Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            expired=true;
        }

//        Log.i("tahun",Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
//        Log.i("year",Integer.toString(year));
//        Log.i("bulan",Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1));
//        Log.i("month",Integer.toString(month));
//        Log.i("hari",Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
//        Log.i("day",Integer.toString(day));

        //check apakah tanggal yg dipilih melebihi sebulan mendatang atau tidak(outOfRange)
        float timer=(float)(Math.abs(year-Calendar.getInstance().get(Calendar.YEAR)))*12
                +(float)(Math.abs(month-(Calendar.getInstance().get(Calendar.MONTH)+1)))
                -(Math.abs(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)/(float)31-day/(float)31));
        Log.i("timer",String.valueOf(timer)+"="+
                String.valueOf((float)(Math.abs(year-Calendar.getInstance().get(Calendar.YEAR))))+"*12"+"+"+
                String.valueOf((float)(Math.abs(month-(Calendar.getInstance().get(Calendar.MONTH)+1))))+"-"+
                String.valueOf(Math.abs(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)/(float)31-day/(float)31))+" "+
                String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)/(float)31-day/(float)31));
//                +"--"+String.valueOf((float)(Math.abs(month-(Calendar.getInstance().get(Calendar.MONTH)+1)))));
        if(timer<1){
            outOfRange=false;
        }

        if(!expired && !outOfRange){
            //jika tidak expired dan outOfRange maka...
            String mMonth = Integer.toString(month),mDay=Integer.toString(day);
            if (mMonth.length() == 1) {
                mMonth = "0" + mMonth;
            }
            if(mDay.length() == 1) {
                mDay = "0" + mDay;
            }
            SetBooking.dateView.setText(new StringBuilder().append(mDay).append("-")
                    .append(mMonth).append("-").append(year));
            SetBooking.dateView.setError(null);
            setTime(findViewById(R.id.formPukul));
        }else if (outOfRange){
            //jika tanggal yg dipilih melebihi sebulan mendatang (outOfRange) maka...
            SetBooking.dateView.setError("rentang waktu melebihi 30 hari");
            SetBooking.dateView.setText("");

            new AlertDialog.Builder(MainActivityDrawer.this)
                    .setTitle("Perhatian")
                    .setMessage("rentang waktu terlalu lama \n" +
                            "silahkan pilih tanggal kurang dari 1 bulan mendatang")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else if(expired){
            //jika tanggal yang dipilih kadaluarsa (expired) maka...
            SetBooking.dateView.setError("tanggal sudah kadaluarsa");
            SetBooking.dateView.requestFocus();
            SetBooking.dateView.setText("");

            new AlertDialog.Builder(MainActivityDrawer.this)
                    .setTitle("Perhatian")
                    .setMessage("tanggal yang anda pilih sudah kadaluarsa \n" +
                            "silahkan pilih waktu dengan benar")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    private void showTime(int hour,int minute){


        SetBooking.timeView.setText("");
        //because in the categoryPoli window display only pick the index of the categoryPoli's numbers
        //so I use the index to get a related number in an array in which contain customized number
        //as shown by the categoryPoli

        // karena value menit yg diperoleh dari spiner adalah index maka..
        // menit berfungsi untuk mengambil nilai sejati pada cuztemized array yang di generate secara khusus
        int mMinute = Config.Interval.get(minute);
        StringBuilder sb = new StringBuilder();
        Boolean expired=false;


//        Log.i("jam",Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
//        Log.i("hourOfDay",Integer.toString(hour));
//        Log.i("menit",Integer.toString(Calendar.getInstance().get(Calendar.MINUTE)));
//        Log.i("minute",Integer.toString(mMinute));

        // check whether the date is out to date or not
        if(hour<Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                && hari<=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            expired=true;
        }else if (hour==Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                && mMinute<Calendar.getInstance().get(Calendar.MINUTE)
                && hari<=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            expired=true;
        }

        if(expired) {
            SetBooking.timeView.setError("jam sudah kadaluarsa");
            SetBooking.timeView.requestFocus();

            new AlertDialog.Builder(MainActivityDrawer.this)
                    .setTitle("Perhatian")
                    .setMessage("waktu yang anda pilih sudah kadaluarsa \n" +
                            "silahkan pilih waktu dengan benar")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else {
            if (Integer.toString(hour).length() == 1) sb.append("0").append(hour);
            else sb.append(hour);

            sb.append(":");

            if (Integer.toString(mMinute).length() == 1) sb.append("0").append(mMinute);
            else sb.append(mMinute);

            SetBooking.timeView.setError(null);
        }

        SetBooking.timeView.setText(sb);

    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action_bar view (dot)
     */
    private void loadNavHeader() {

        txtHospitalName.setText("PKU Muhammadiyah Kutoarjo");
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.e("onchildchange", dataSnapshot.toString() +"/"+ s +"/"+ dataSnapshot.getKey());
                        setDisplayNameTextView();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//        if(User.isLogedIn()) {
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                    .child(User.getUid())
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            String nickname = " ";
//                                            // name, website
//
//                                            if (User.isDoctor(MainActivityDrawer.this))
//                                                nickname = "Dr. ";
//                                            else nickname = "Sdr/i ";
//
//                                            nickname += User.getDisplayName();
//
//                                            txtUserName.setText(nickname);
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//        }else txtUserName.clearComposingText();

        // loading header background image
//        Glide.with(this).load(urlNavHeaderBg)
//                .placeholder(R.drawable.foto_rs)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
//        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);

//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                .child(User.getUid())
//                .child("notification")
//                .child("read")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e(TAG,dataSnapshot.getValue().toString());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };



        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

//        if(!getSupportFragmentManager().findFragmentByTag(CURRENT_TAG).equals(TAG_BOOKING))
//            snack.dismiss(); show=false;

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();


//        if(navItemIndex==1){
//            toggleSnack();
//        }
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
//                Home homeFragment = new Home();
                return new Home();
//                return new HomeContentTips();
            case 1:
                // photos

//                Booking reservation = new Booking();
//                HasBooked reservation2= new HasBooked();
//
//                if (User.isBooking(MainActivityDrawer.this)) {
//
//                    NotificationUtils.clearNotifications(getApplicationContext(),Config.NOTIFICATION_ID_BOOKING);
//
//                    return reservation2;
//                }else return reservation;

                return new Booking();

            case 2:
                // movies fragment
//                ConversationList CL = new ConversationList();
                return new ConversationList();
            case 3:
                // notifications fragment
                return new Notification();

            default:
                return new Home();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action_bar
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_booking:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_BOOKING;
                        break;
                    case R.id.nav_consultation:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CONSULTATION;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;

                    case R.id.setting:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivityDrawer.this, Setting.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.about:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivityDrawer.this, AboutUsActivity.class));
//                        startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//                        startActivity(new Intent(MainActivityDrawer.this, QueueActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.login_btn:
                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));

                        //TODO
                        if(User.isLogedIn()) Helper.signOut(MainActivityDrawer.this);
                        else Helper.signIn(MainActivityDrawer.this);

                        drawer.closeDrawers();
                        return true;

                    default:
                        navItemIndex = 0;
                }


//                if(navItemIndex!=1){
//                    show=false;
//                    costumeSnackContent();
//                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void hideKeyboard(){
        View v= this.getCurrentFocus();
        if(v!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

    @Override
    public void onBackPressed() {


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action_bar bar if it is present.

//        // show menu only when home fragment is selected
//        if (navItemIndex == 0) {
//            getMenuInflater().inflate(R.menu.action_bar, menu);
//        }
//
//        // when fragment is notifications, load the menu created for notifications
//        if (navItemIndex == 3) {
//            getMenuInflater().inflate(R.menu.action_bar, menu);
//        }

        if(navItemIndex == 1){
//            getMenuInflater().inflate(R.menu.action_bar_booking, menu);
//        }else if(navItemIndex == 2) {
////            getMenuInflater().inflate(R.menu.action_bar, menu);
//
////            SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
////            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
////            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//
////            SearchManager searchm = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
////            MenuItem searchMenuItem = menu.findItem(R.id.search);
////            SearchView searchView = (SearchView) searchMenuItem.getActionView();
////            searchView.setSearchableInfo(searchm.
////                    getSearchableInfo(getComponentName()));
//
////            searchView.setSubmitButtonEnabled(true);
////            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////                @Override
////                public boolean onQueryTextSubmit(String query) {
////                    return false;
////                }
////
////                @Override
////                public boolean onQueryTextChange(String newText) {
////                    return false;
////                }
////            });
        }else if(navItemIndex == 2){
            getMenuInflater().inflate(R.menu.action_bar, menu);
            menu.findItem(R.id.search).setVisible(true);
        }else if(navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.action_bar_notification, menu);
        }




        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action_bar bar item clicks here. The action_bar bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        // user is in notifications fragment
//        // and selected 'Mark all as Read'
//        if (id == R.id.action_mark_all_read) {
//            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
//        }
//
//        // user is in notifications fragment
//        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
        if (id == R.id.masuk) {
            if(item.getTitle().equals("Masuk")){
//                startActivityForResult(
//                        AuthUI.getInstance()
//                                .createSignInIntentBuilder()
//                                .setLogo(R.drawable.logo192x192)
//                                .setAvailableProviders(
//                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
//                                .build(),
//                        SIGN_IN_REQUEST_CODE
//
//                );

                signIn();
                if(User.isLogedIn())item.setTitle("Keluar");
//                item.setTitle("Keluar");
//                startActivity(new Intent(MainActivityDrawer.this, SignInActivity.class));
            }else{
                myOnResume(0);
                Helper.signOut(MainActivityDrawer.this);

//                pDialog = new ProgressDialog(MainActivityDrawer.this);
//                pDialog.setMessage("mohon tunggu...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                final Context ctx = MainActivityDrawer.this;
//
//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                        .child(User.getUid())
//                        .child("booking")
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(final DataSnapshot dataSnapshot) {
//
////                                if(dataSnapshot.getValue()!=null){
//
//                                    //sign out function
//                                    Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    AuthUI.getInstance().signOut(MainActivityDrawer.this)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                    myOnResume(0);
//
//                                                    SharedPrefManager.getInstance(MainActivityDrawer.this).doctorClear();
//
//                                                    pDialog.hide();
//
//                                                    getIntent().removeExtra(Config.ON_SIGNIN_SUCCESS);
//
//                                                    Toast.makeText(MainActivityDrawer.this,
//                                                            R.string.sign_out_done,
//                                                            Toast.LENGTH_LONG)
//                                                            .show();
//
//                                                    User.deleteUserToken(Uid);
//
//                                                    if(dataSnapshot.getValue()!=null) {
//
//                                                        //cancle alarm
//                                                        for (DataSnapshot children : dataSnapshot.getChildren()) {
//                                                            Remainder.setAlarmCancel(ctx,children.child("date").getValue(String.class), children.child("time").getValue(String.class));
////                                                            setAlarmCancel(children.child("date").getValue(String.class), children.child("time").getValue(String.class));
//                                                        }
//                                                    }
//
//
//
//                                                    recreate();
//
//                                                }
//
//                                            });
////                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

//                Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                AuthUI.getInstance().signOut(this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                                getIntent().removeExtra(Config.ON_SIGNIN_SUCCESS);
//
//                                Toast.makeText(MainActivityDrawer.this,
//                                        R.string.sign_out_done,
//                                        Toast.LENGTH_LONG)
//                                        .show();
//
//                                User.deleteUserToken(Uid);
//
//                            }
//
//                        });
            }
//            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in action_bar_notification fragment
        // and selected 'Mark all as Read'
//        if (id == R.id.action_mark_all_read) {
////            Toast.makeText(getApplicationContext(), "All action_bar_notification marked as read!", Toast.LENGTH_LONG).show();
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                    .child(User.getUid())
//                    .child("notification")
//                    .orderByChild("read").equalTo("false")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.getValue()!=null){
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                                    FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                                            .child(User.getUid())
//                                            .child("notification")
//                                            .child(data.getKey())
//                                            .child("read").setValue("true");
//
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//        }
//
//        // user is in action_bar_notification fragment
//        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
////            Toast.makeText(getApplicationContext(), "Clear all action_bar_notification!", Toast.LENGTH_LONG).show();
//
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                    .child(User.getUid())
//                    .child("notification")
//                    .removeValue();
//
//            myOnResume(3);
//
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        final MenuItem actionLogin = menu.findItem(R.id.masuk);
//        if (!User.isLogedIn()) actionLogin.setTitle("Masuk");
//        else actionLogin.setTitle("Keluar");

        if(navItemIndex==1) {
//            final MenuItem actionBooking = menu.findItem(R.id.action_set_booking);
//            if (User.isBooking(this)) actionBooking.setTitle("batal");
        }else if(navItemIndex == 2) {
//            menu.findItem(R.id.search).setVisible(true);
//            getMenuInflater().inflate(R.menu.action_bar, menu);

//            SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
//            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


//            SearchManager searchm = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
//            searchMenuItem = menu.findItem(R.id.search);
//            searchView = (SearchView) searchMenuItem.getActionView();
//            searchView.setSearchableInfo(searchm.getSearchableInfo(getComponentName()));
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    return false;
//                }
//            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 2) {
            fab.show();
        }
        else
            fab.hide();
    }

//    public void countQueue(){
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                .child(User.getUid())
//                .child("notification")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        Log.e(s+"childAdd",dataSnapshot.getValue().toString());
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Log.e(s+"cchildChagned",dataSnapshot.getValue().toString());
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        Log.e("childRemoved",dataSnapshot.getValue().toString());
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        Log.e(s +"childMoved",dataSnapshot.getValue().toString());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }

    // show or hide snack
//    public void toggleSnack(){
//        snack = Snackbar.make(getHomeFragment().getView().getRootView()
//                ,"pesan antrian disini",
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction("PESAN", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!User.isBooking(getApplicationContext())) {
//                            myOnResume(1);
//                            FragmentManager fm = getSupportFragmentManager();
//                            Fragment fragment = fm.findFragmentById(R.id.container_booking);
//                            fm.beginTransaction()
//                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                                    .replace(R.id.container_booking, new SetBooking())
//                                    .commit();
//                        }
//                    }
//                });
//
//        if (navItemIndex == 1){
////            costumeSnackContent();
//        }else snack.dismiss();
//    }

//    public void costumeSnackContent() {
//
//        if(User.isBooking(this)){
//
//            final PemesanModel detailPasien = SharedPrefManager.getInstance(this).getPemesan();
//            snack = Snackbar.make(getHomeFragment().getView().getRootView(),"",Snackbar.LENGTH_INDEFINITE)
//                    .setAction("No. Anda :"+detailPasien.getAntriNo() + ", Kode :" +
//                            detailPasien.getRef().substring(detailPasien.getRef().toString().length() - 5,
//                                    detailPasien.getRef().toString().length()), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            snack.dismiss();
//                            show=true;
//                        }
//                    });
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
//                    .child(detailPasien.getAntriTanggal()).child("urutan")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            Log.e("urutan",dataSnapshot.toString());
//
//                            String urutan="1";
//                            if (dataSnapshot.getValue()!=null) urutan = dataSnapshot.getValue().toString();
//                            snack.setText(urutan);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
////            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
////            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
////                    .child(detailPasien.getAntriTanggal())
////                    .addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            String urutan="1";
////                            if(dataSnapshot.child("urutan")!=null){
////                                urutan = String.valueOf(dataSnapshot.getValue());
////                            }
////                            Log.e("uid test",urutan+dataSnapshot.child(detailPasien.getRef()).child("uid").getValue().toString());
////                            if(dataSnapshot.child(detailPasien.getRef()).child("uid").getValue().equals(detailPasien.getUid()))
////                                snack = Snackbar.make(getActivity().getCurrentFocus()
////                                        ,detailPasien.getAntriNo() + " " +
////                                                detailPasien.getRef()
////                                                        .indexOf(detailPasien.getRef().length()-4,
////                                                                detailPasien.getRef().length()),
////                                        Snackbar.LENGTH_INDEFINITE)
////                                        .setAction(urutan, null);
////
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//
//        }else{
//            snack = Snackbar.make(getHomeFragment().getView().getRootView()
//                    ,"pesan antrian disini",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction("PESAN", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if(!User.isBooking(getApplicationContext())) {
//                                myOnResume(1);
//                                FragmentManager fm = getSupportFragmentManager();
//                                Fragment fragment = fm.findFragmentById(R.id.container_booking);
//                                fm.beginTransaction()
//                                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                                        .replace(R.id.container_booking, new SetBooking())
//                                        .commit();
//                            }
//                        }
//                    });
//        }
//        if(navItemIndex==1)
//            if (show) snack.show();
//        else snack.dismiss();
//        show = false;
////        else snack.dismiss();
//    }

    private void checkRequest(){

        if(User.isLogedIn()) {
            FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Config.FURL_CONV_REQ)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.child("topicRef").getValue(String.class).equals("") &&
                                    dataSnapshot.child("senderUid").getValue(String.class).equals(User.getUid())) {
//
//                                Log.e(TAG,"checkRequest()");

                                requestTitle = dataSnapshot.child("topicTitle").getValue(String.class);
                                requestMsg = dataSnapshot.child("payload").child("messageText").getValue(String.class);
                                requestRef = dataSnapshot.getRef();

//                                text.setText(String.valueOf(data.child("payload").child("messageText").getValue()));
//                                title.setText(String.valueOf(data.child("topicTitle").getValue()));
//                                reqExist=true;
//
//                                Log.e(TAG, "reqExist dalam"+String.valueOf(reqExist));

                                fab.setFocusable(false);

                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child("topicRef").getValue(String.class).equals("") &&
                                    dataSnapshot.child("senderUid").getValue(String.class).equals(User.getUid())) {
                                fab.setFocusable(true);
                            }

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                .orderByChild("senderUid")
//                .equalTo(User.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        Log.e(TAG,"checkRequest()"+dataSnapshot.getValue());
//
//                        String title = null,content = null;
//                        DatabaseReference ref = null;
//
//
//                        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                            if (data.child("topicRef").getValue().equals("")&&data.child("senderUid").getValue().equals(User.getUid())) {
//
////                                Log.e(TAG,"checkRequest()");
//
//                                title = String.valueOf(data.child("topicTitle").getValue());
//                                content = String.valueOf(data.child("payload").child("messageText").getValue());
//                                ref = data.getRef();
//
////                                text.setText(String.valueOf(data.child("payload").child("messageText").getValue()));
////                                title.setText(String.valueOf(data.child("topicTitle").getValue()));
//                                reqExist=true;
//
//                                Log.e(TAG, "reqExist dalam"+String.valueOf(reqExist));
////                                reqRef=String.valueOf(data.getRef());
//                                break;
//                            }
//                        }
//                        Log.e(TAG, "reqExist luar"+String.valueOf(reqExist));
//                        if(reqExist==true){
//                            alertHasRequest(title,content,ref);
//                        }else{
//                            requestDialog();
//                        }
//                        reqExist=false;
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
        }
    }

    private void alertHasRequest(String title, String content, final DatabaseReference ref) {
        new AlertDialog.Builder(this)
                .setTitle("perhatian")
                .setMessage(
                        "anda sudah mengirim satu pertanyaan baru\n\n" +
                                "judul:\n" + title +
                                "\nisi:\n" +content +
                                "\n\n\n mengganti pertanyaan akan menghapus pertanyaan sebelumnya")
                .setPositiveButton("ganti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ref!=null)ref.removeValue();
                        requestDialog();
                    }
                })
                .setNegativeButton("kembali",null)
                .show();
    }

    public void requestDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.prompt_request,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setView(promptView);

        final EditText topic = (EditText)promptView.findViewById(R.id.et_topic);
        final EditText content = (EditText)promptView.findViewById(R.id.et_question);

        dialogBuilder
//                .setTitle("Nama Pemesan")
//                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(TextUtils.isEmpty(topic.getText().toString())){
                            topic.setError("masukan judul konsultasi disini");
                            topic.requestFocus();

                        }
                        else if(TextUtils.isEmpty(content.getText().toString())){
                            content.setError("masukan komentar anda disini");
                            content.requestFocus();

                        }else
                            sendRequest(topic.getText().toString(),content.getText().toString());

                        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(topic.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        inputManager.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void sendRequest(final String topic, String Content){
//        EditText text = (EditText) findViewById(R.id.text_val);
//        EditText title = (EditText) findViewById(R.id.title_val);

        reqExist = true;

        Map quote = new HashMap<String, String>();
        quote.put("text", "");
        quote.put("uidFrom", "");

        String displayName;
//        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")){
//            displayName=String.valueOf(
//                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//            ).substring(0,
//                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().length()-3)+"XXX";
//        }else
            displayName=User.getDisplayName();

//        boolean auth = false;
//        if(!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
//            auth = true;
//        }

        ConvHandler payload = new ConvHandler(Content,
                displayName,
//                FirebaseAuth.getInstance().getCurrentUser().getUid(), quote,auth);
                User.getUid(), quote);

//        final DatabaseReference ref = FirebaseDatabase.getInstance()
//                .getReferenceFromUrl(url);
//        ref.child("conversation").push()
////                .setValue(new ConvHandler(inputan.getText().toString(),
////                        displayName,
////                        FirebaseAuth.getInstance().getCurrentUser().getUid(),quote))
//                .setValue(payload)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        ref.child("time").setValue(System.currentTimeMillis());


        if(User.isDoctor(this)){
//            convRef.child("listener").push(dataListener);
//            convRef.child("conversation").push(payload);
//            convRef.child("time").set(new Date().getTime());
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Config.FURL_CONV_APPROVED)
                    .push();
            ref.child("conversation")
                    .push().setValue(payload)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialogRequestSuccess();
                            ref.child("listener").push().child("uid").setValue(User.getUid());
                            ref.child("time").setValue(System.currentTimeMillis());
                            ref.child("title").setValue(topic);
                            myOnResume(2);
                        }
                    });
        }else {

            Log.e("quote", String.valueOf(quote));
            Log.e("payload", String.valueOf(payload));

            Map req = new HashMap();
            req.put("topicRef", "");
            req.put("topicTitle", topic);
            req.put("senderUid", User.getUid());
            req.put("payload", payload);

            FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Config.FURL_CONV_REQ)
                    .push()
                    .setValue(req)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialogRequestSuccess();
//                        Toast.makeText(getApplication(),"permintaas sudah dikirim silahkan tunggu konfirmasi",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void dialogRequestSuccess() {
        new AlertDialog.Builder(this)
                .setMessage(User.isDoctor(this)?"pembuatan judul baru berhsil dilakukan":"permintaan berhasil dikirim, mohon tunggu persetujuan admin untuk menampilkan pertanyaan anda")
                .show();
    }

}