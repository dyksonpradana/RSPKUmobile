package com.rspkumobile.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rspkumobile.R;
import com.rspkumobile.fragment.LoginDoctor;
import com.rspkumobile.fragment.SetDisplayName;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

public class SignInActivity extends AppCompatActivity {

    EditText etDisplayName;
    Button send;
    TextView etError;
    User userDetail;
    private String TAG = "customer";
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fm = this.getSupportFragmentManager();

        changeFrame(0);

    }

    public void changeFrame(int i){

        Fragment fragment = new SetDisplayName();
        TAG = "customer";
//
//        if (User.isBooking(getActivity())) {
//            fragment = new HasBooked();
//        } else fragment = new SetBooking();

        if (i==1) {
//            TAG = "customer";
//        }
//        else {
//        else if(i!=0&&!User.isBooking(getActivity())) fragment = new SetBooking();
            fragment = new LoginDoctor();
            TAG = "doctor";
        }


        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.container, fragment, TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(TAG.equals("customer")) {
            AuthUI.getInstance().signOut(SignInActivity.this)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            SharedPrefManager.getInstance(SignInActivity.this).doctorClear();

                            Toast.makeText(SignInActivity.this,
                                    R.string.login_canceled,
                                    Toast.LENGTH_LONG)
                                    .show();

                            User.deleteUserToken(User.getUid());


                        }

                    });
            super.onBackPressed();
        }else{
//            TAG = "customer";
            changeFrame(0);
        }
    }
}
