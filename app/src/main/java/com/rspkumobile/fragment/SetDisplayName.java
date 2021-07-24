package com.rspkumobile.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rspkumobile.activity.SignInActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.User;
import com.rspkumobile.util.Remainder;

/**
 * Created by DK on 3/12/2018.
 */

public class SetDisplayName extends Fragment {
    private User userDetail;
    private EditText etDisplayName;
    private TextView etError;
    private Button send;
    private TextView loginAsDoctor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_display_name, container, false);

        userDetail = new User();

        etDisplayName = (EditText)view.findViewById(R.id.et_display_name);
        etError = (TextView) view.findViewById(R.id.tv_invalid_et);
        loginAsDoctor = (TextView)view.findViewById(R.id.login_doctor);
        loginAsDoctor.setPaintFlags(loginAsDoctor.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        send = (Button)view.findViewById(R.id.btn);

        loginAsDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignInActivity)getActivity()).changeFrame(1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDisplayName();
//                setAlarms();

                Helper.setAlarms(getActivity());

//                MainActivityDrawer.clearExpiredBooking(getActivity());
            }
        });

//        if(User.isLogedIn()){
//            finish();
//        }
        return view;
    }

//    private void setAlarms() {
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                .child(User.getUid())
//                .child("booking")
////                .orderByChild("status").equalTo(Config.BOOKING_STATUS_TYPE_EX[0])
//                .orderByChild("status").equalTo(Config.BOOKING_STATUS[0])
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(final DataSnapshot dataSnapshot) {
//
//                        if(dataSnapshot.getValue()==null)getActivity().finish();
//                        else{
//
//                            //cancle alarm
//                            for (DataSnapshot children : dataSnapshot.getChildren()){
//                                String[] date = children.child("date").getValue(String.class).replace(" ","").split(",");
//
//                                Remainder.setAlarm(getActivity(),
//                                        String.valueOf(children.getKey()),
//                                        date,
//                                        children.child("time").getValue(String.class)
////                                        ,
////                                        Config.BOOKING_STATUS[0]
//                                );
//                            }
//                            getActivity().finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }

    private void signOut(){
        AuthUI.getInstance().signOut(getActivity());
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        new AlertDialog.Builder(getActivity())
//                .setTitle("Perhatian")
//                .setMessage("anda akan membatalkan proses Log in")
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton("batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        signOut();
//                        getActivity().finish();
//                    }
//                })
//                .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .show();
//    }


    private void setDisplayName(){

        if(TextUtils.isEmpty(etDisplayName.getText())){
            etError.setVisibility(View.VISIBLE);
        }else{
//            FirebaseDatabase.getInstance().getReference().child("users")
//                    .child(userDetail.getUid())
//                    .child("displayName")
//                    .setValue(etDisplayName.getText());

//            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(String.valueOf(etDisplayName.getText()))
//                    .build();
//            User.setDisplayName(profileUpdate);

            User.updateDisplayName(etDisplayName.getText().toString());

//            userDetail.updateCurrentUserDetail();

//            User.updateCurrentUserDetail();

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(send.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            Toast.makeText(getActivity(),R.string.sign_in_done,Toast.LENGTH_LONG).show();
        }
    }
}
