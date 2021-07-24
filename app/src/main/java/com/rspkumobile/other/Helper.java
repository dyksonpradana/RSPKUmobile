package com.rspkumobile.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rspkumobile.R;
import com.rspkumobile.activity.SignInActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.util.Remainder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DK on 12/25/2017.
 */

public class Helper {

    private static int SIGN_IN_REQUEST_CODE = 001;

    public static String getCloseTime(Context ctx, String day, String service){
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
            JSONArray shifts = schedule.getJSONObject(service).getJSONArray(relatedDayId);
            close = shifts.getJSONObject(shifts.length() - 1).getString("tutup");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return close;
    }

    public static Boolean isExpired(Context ctx, String day, String date, String time, String service){
        String closeTime = getCloseTime(ctx, day, service);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");

        long bookingTimeInMilis = 0;
        try {
            bookingTimeInMilis = sdf.parse(date+" "+time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long closeTimeInMilis = 0;

        try {
            closeTimeInMilis = sdf.parse(date+" "+closeTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Toast.makeText(ctx,bookingTimeInMilis+">"+closeTimeInMilis,Toast.LENGTH_LONG).show();
        Log.e("isexpired",date+" "+time+" "+bookingTimeInMilis+">"+closeTimeInMilis+" "+date+" "+closeTime);

//        if(bookingTimeInMilis>closeTimeInMilis) return true;
//        if(bookingTimeInMilis<System.currentTimeMillis()) return true;
        if(System.currentTimeMillis()+(10000*60*3)>=closeTimeInMilis) return true;
        return false;
    }

    public static Boolean isLate(Context ctx, String day, String date, String time, String service){
        String closeTime = getCloseTime(ctx, day, service);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");

        long bookingTimeInMilis = 0;
        try {
            bookingTimeInMilis = sdf.parse(date+" "+time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long closeTimeInMilis = 0;

        try {
            closeTimeInMilis = sdf.parse(date+" "+closeTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("isLate",date+" "+time+" "+bookingTimeInMilis+">"+closeTimeInMilis+" "+date+" "+closeTime);

//        if(bookingTimeInMilis>closeTimeInMilis) return true;
        if(bookingTimeInMilis<=System.currentTimeMillis()+(10000*60*3)) return true;
//        if(System.currentTimeMillis()>closeTimeInMilis) return true;
        return false;
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if(view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void signOut(final Activity activity){
        final ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("mohon tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

//        final Context ctx = MainActivityDrawer.this;

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

//                                if(dataSnapshot.getValue()!=null){

                        //sign out function
                        AuthUI.getInstance().signOut((FragmentActivity) activity)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

//                                        myOnResume(0);

                                        SharedPrefManager.getInstance(activity).doctorClear();

                                        pDialog.hide();

                                        activity.getIntent().removeExtra(Config.ON_SIGNIN_SUCCESS);

                                        Toast.makeText(activity,
                                                R.string.sign_out_done,
                                                Toast.LENGTH_LONG)
                                                .show();

                                        User.deleteUserToken(uid);

                                        if(dataSnapshot.getValue()!=null) {

                                            //cancle alarm
                                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                Remainder.setAlarmCancel(activity,children.getKey(),children.child("date").getValue(String.class), children.child("time").getValue(String.class));
//                                                            setAlarmCancel(children.child("date").getValue(String.class), children.child("time").getValue(String.class));
                                            }
                                        }



                                        activity.recreate();

                                    }

                                });
//                                }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
        if(requestCode == SIGN_IN_REQUEST_CODE) {

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK) {

                SharedPrefManager.getInstance(activity).turnNotificationOn(true);
                SharedPrefManager.getInstance(activity).turnSoundOn(true);

//                Toast.makeText(this,
//                        response.getProviderType(),
//                        Toast.LENGTH_LONG)
//                        .show();

                Log.e("Sing IN", "Firebase reg id: " + FirebaseInstanceId.getInstance().getToken());
//                startActivity(new Intent(this, MainActivityDrawer.class)
//                        .putExtra(Config.ON_SIGNIN_SUCCESS, FirebaseInstanceId.getInstance().getToken()));

//                ((MainActivityDrawer)getApplicationContext()).myOnResume();
//                setTitle(R.string.layout_sign_in_display_name);
                User.updateCurrentUserDetail();

//                if(response.getProviderType().equals("phone")) {
//                    Toast.makeText(this,
//                            response.getProviderType(),
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
////                    startActivity(new Intent(MainActivityDrawer.this,AboutUsActivity.class));
//                }

                //is doctor
                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
                        .child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null) {
                            activity.recreate();
                            activity.startActivity(new Intent(activity,SignInActivity.class));
                        }
                        else {

                            Helper.hideKeyboard(activity);

                            String name = dataSnapshot.child("name").getValue(String.class);
                            String unip = dataSnapshot.child("unip").getValue(String.class);

                            Log.e("login doctor",dataSnapshot.getValue().toString());
                            Log.e("login doctor",name + unip);

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            SharedPrefManager.getInstance(activity).setAsDoctor(unip);

                            User.setDisplayName(profileUpdate);

                            User.updateCurrentUserDetail();

                            Toast.makeText(activity,"selamat datang Dr. "+name,Toast.LENGTH_LONG).show();

                            activity.recreate();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                return;

            } else {
                // Close the app
//                finish();
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(activity,
                            R.string.login_canceled,
                            Toast.LENGTH_LONG)
                            .show();
//                    finish();
                    return;

                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(activity,
                            "no network",
                            Toast.LENGTH_LONG)
                            .show();
//                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(activity,
                            "error",
                            Toast.LENGTH_LONG)
                            .show();
//                    finish();
                    return;
                }
            }
//            Toast.makeText(this,
//                    "error",
//                    Toast.LENGTH_LONG)
//                    .show();
////            finish();
//            return;
        }
    }

    public static void requireLogin(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Perhatian")
                .setMessage("anda harus Log In untuk menggunakan fasilitas ini"
                        + "\nLog In sekarang?"
                )
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signIn(activity);
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static void signIn(Activity activity) {
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.logo192x192)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
//                                        ,new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                ))
                        .build(),
                SIGN_IN_REQUEST_CODE
        );
    }

    public static void setAlarms(final Activity activity) {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
//                .orderByChild("status").equalTo(Config.BOOKING_STATUS_TYPE_EX[0])
                .orderByChild("status").equalTo(Config.BOOKING_STATUS[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()==null)activity.finish();
                        else{

                            //cancle alarm
                            for (DataSnapshot children : dataSnapshot.getChildren()){
                                String[] date = children.child("date").getValue(String.class).replace(" ","").split(",");

                                Log.e("setting","alarmx"+children.getKey()+date[0]+children.child("time").getValue(String.class)+date[1]+children.child("date").getValue(String.class));

                                Remainder.setAlarm(activity,
                                        String.valueOf(children.getKey()),
                                        date,
                                        children.child("time").getValue(String.class)
//                                        ,
//                                        Config.BOOKING_STATUS[0]
                                );
                            }
                            activity.finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void setFullscreen(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

//    public static String getUid(){
//        if(isLogedIn())
//            return FirebaseAuth.getInstance().getCurrentUser().getUid();
//        else return "";
//    }
//
//    public String getDisplayName(){
//        if(isLogedIn())
//            return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        else return "";
//    }
//
//    public static void setDisplayName(UserProfileChangeRequest data){
//        FirebaseAuth.getInstance().getCurrentUser().updateProfile(data)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            Log.e("updateProfile","profile berhasil di update");
//                            updateCurrentUserDetail();
//                        }
//                    }
//                });
//    }
//
//
//    public static boolean isLogedIn(){
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
//            return true;
//        else return false;
//    }
//
//    public static void updateCurrentUserDetail(){
//
////        Map userDetail = new HashMap();
////        Log.d(TAG,"uid"+FirebaseAuth.getInstance().getCurrentUser().getUid());
//
////        userDetail.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
////        userDetail.put("token", FirebaseInstanceId.getInstance().getToken());
////        userDetail.put("userDetail",FirebaseAuth.getInstance().getCurrentUser());
//
////        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
////            userDetail.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
////        }else userDetail.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS
//                + FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        ref.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
//        ref.child("userDetail").setValue(FirebaseAuth.getInstance().getCurrentUser());
//
//        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
//            ref.child("phone").setValue( FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        }else ref.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//
//    }
//
//    public static void deleteUserToken(String uid){
//        //using uid because we will lost uid since onComplite operation
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS
//                +uid
//                +"/token").removeValue();
//    }
//
//    public static boolean isBooking(Context ctx){
//        return SharedPrefManager.getInstance(ctx).isBooking();
//    }

//    public static boolean isAuth(){
//        auth = false;
//        if(isLogedIn()) {
//            FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl(Config.FURL_USERS + FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child("auth").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue(Boolean.class)!=null) {
//                        Log.e("auth", dataSnapshot.getValue(Boolean.class).toString());
//                        if (dataSnapshot.getValue(Boolean.class) == true)
//                            auth = true;
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//        return auth;
//    }

}
