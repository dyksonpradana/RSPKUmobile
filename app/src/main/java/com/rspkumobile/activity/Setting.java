package com.rspkumobile.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.rspkumobile.R;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

public class Setting extends AppCompatActivity {

    Switch notification,sound;
    LinearLayout containerSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notification = (Switch)findViewById(R.id.switch_notification);
        notification.setChecked(SharedPrefManager.getInstance(this).isNotificationTurnedOn());
        sound = (Switch)findViewById(R.id.switch_sound);
        sound.setChecked(SharedPrefManager.getInstance(this).isSoundTurnedOn());

        setNotification(true);

        containerSound = (LinearLayout)findViewById(R.id.container_sound);
        if (!User.isDoctor(this)) containerSound.setVisibility(View.GONE);

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNotification(isChecked);
//                SharedPrefManager.getInstance(Setting.this).turnNotificationOn(isChecked);
            }
        });

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefManager.getInstance(Setting.this).turnSoundOn(isChecked);
                Toast.makeText(Setting.this,"sound "+SharedPrefManager.getInstance(Setting.this).isSoundTurnedOn(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setNotification(boolean isTurnedOn){
        SharedPrefManager.getInstance(Setting.this).turnNotificationOn(isTurnedOn);
        if(!isTurnedOn){
//            notification.setChecked(false);
            setSound(false);
        }else{
//            notification.setChecked(true);
            setSound(true);
        }
    }

    private void setSound(boolean isNotificationOn){
        if (containerSound!=null){
//        else {

            sound.setClickable(isNotificationOn);
            sound.setChecked(isNotificationOn);

            if (!isNotificationOn) {
                sound.setTextColor(Color.GRAY);
//            SharedPrefManager.getInstance(this).turnSoundOn(false);
//        }else {
//            if (!User.isDoctor(this)) containerSound.setVisibility(View.GONE);
//            SharedPrefManager.getInstance(this).turnSoundOnOff(true);
//            if (User.isDoctor(this)) {
//                if (!SharedPrefManager.getInstance(this).turnSound())
//                    sound.setChecked(false);
//                else sound.setChecked(true);
//            } else containerSound.setVisibility(View.GONE);
            } else {
                sound.setTextColor(Color.BLACK);
            }
//            SharedPrefManager.getInstance(this).turnSoundOn(true);
            Toast.makeText(this,"notif "+isNotificationOn+" "+"sound "+SharedPrefManager.getInstance(Setting.this).isSoundTurnedOn(),
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action_bar bar item clicks here. The action_bar bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
