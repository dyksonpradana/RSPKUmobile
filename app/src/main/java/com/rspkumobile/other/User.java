package com.rspkumobile.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rspkumobile.app.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DK on 12/25/2017.
 */

public class User {

    private static boolean auth;

    public static String getUid(){
        if(isLogedIn())
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        else return null;
    }

    public static String getDisplayName(){
        if(isLogedIn())
            return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        else return "";
    }

    public static void setDisplayName(UserProfileChangeRequest data){
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("updateProfile","profile berhasil di update");
                            updateCurrentUserDetail();
                        }
                    }
                });
    }

    public static void updateDisplayName(String name){

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(name))
                .build();

        setDisplayName(profileUpdate);
    }


    public static boolean isLogedIn(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            return true;
        else return false;
    }

    public static boolean isDoctor(Context ctx){
        return SharedPrefManager.getInstance(ctx).isDoctor();
    }

    public static void updateCurrentUserDetail(){

//        Map userDetail = new HashMap();
//        Log.d(TAG,"uid"+FirebaseAuth.getInstance().getCurrentUser().getUid());

//        userDetail.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        userDetail.put("token", FirebaseInstanceId.getInstance().getToken());
//        userDetail.put("userDetail",FirebaseAuth.getInstance().getCurrentUser());

//        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
//            userDetail.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        }else userDetail.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS
                + FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
        ref.child("userDetail").setValue(FirebaseAuth.getInstance().getCurrentUser());

        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
            ref.child("phone").setValue( FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }else ref.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    public static void deleteUserToken(String uid){
        //using uid because we will lost uid since onComplite operation
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS
                +uid
                +"/token").setValue("");
    }

    public static boolean isBooking(Context ctx){
        return SharedPrefManager.getInstance(ctx).isBooking();
    }

    public static boolean isAuth(){
        auth = false;
        if(isLogedIn()) {
            FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Config.FURL_USERS + FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("auth").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(Boolean.class)!=null) {
                        Log.e("auth", dataSnapshot.getValue(Boolean.class).toString());
                        if (dataSnapshot.getValue(Boolean.class) == true)
                            auth = true;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return auth;
    }

}
