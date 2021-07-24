package com.rspkumobile.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.User;
import com.rspkumobile.util.Remainder;

public class SignInActivityEx extends AppCompatActivity {

    EditText etDisplayName;
    Button send;
    TextView etError;
    User userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_ex);

        userDetail = new User();

        etDisplayName = (EditText)findViewById(R.id.et_display_name);
        etError = (TextView) findViewById(R.id.tv_invalid_et);
        send = (Button)findViewById(R.id.btn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivityDrawer.clearExpiredBooking(SignInActivityEx.this);

                setAlarms();

                setDisplayName();
            }
        });



//        if(User.isLogedIn()){
//            finish();
//        }
    }

    private void setAlarms() {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
//                .orderByChild("status").equalTo(Config.BOOKING_STATUS_TYPE_EX[0])
                .orderByChild("status").equalTo(Config.BOOKING_STATUS[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()!=null){

                            //cancle alarm
                            for (DataSnapshot children : dataSnapshot.getChildren()){
                                String[] date = children.child("date").getValue(String.class).replace(" ","").split(",");

                                Remainder.setAlarm(SignInActivityEx.this,
                                        children.getKey(),
                                        date,
                                        children.child("time").getValue(String.class)
//                                        ,
//                                        Config.BOOKING_STATUS[0]
                                );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void signOut(){
        AuthUI.getInstance().signOut(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Perhatian")
                .setMessage("anda akan membatalkan proses Log in")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                        finish();
                    }
                })
                .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void setDisplayName(){

        if(TextUtils.isEmpty(etDisplayName.getText())){
            etError.setVisibility(View.VISIBLE);
        }else{
//            FirebaseDatabase.getInstance().getReference().child("users")
//                    .child(userDetail.getUid())
//                    .child("displayName")
//                    .setValue(etDisplayName.getText());
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(String.valueOf(etDisplayName.getText()))
                    .build();

//            userDetail = new User();

//            userDetail.setDisplayName(profileUpdate);
            User.setDisplayName(profileUpdate);

//            userDetail.updateCurrentUserDetail();
            User.updateCurrentUserDetail();

            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(send.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            Toast.makeText(this,R.string.sign_in_done,Toast.LENGTH_LONG).show();

            finish();
        }
    }
}
