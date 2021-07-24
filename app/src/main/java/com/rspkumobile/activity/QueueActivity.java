package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.fragment.SetBooking;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QueueActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    ArrayList<PemesanModel> antrian;
    String url,date;
    public ListView listAntrian;
    public TextView listKosong;
    public TextView header;
    ArrayList<String> list = new ArrayList<>();
    private DatabaseReference ref;

    private int year, month, day;
    int counter;

    public static TextView cldr,next,previous;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        counter=0;

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        final Calendar cal = Calendar.getInstance();
//        cal.set(year,month,day);
        cal.add(cal.DATE,2);
        Date dateTime =  cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        date=getIntent().getExtras().getString("header");
        Log.e("ANTRIANACTIVITY",date);
        header=(TextView)findViewById(R.id.date);
        header.setText("Antrian "+date);

        next = (TextView) findViewById(R.id.next);
        previous = (TextView) findViewById(R.id.previous);

        //calender controller
        int dateState = sdf.format(cal.getTime()).compareTo(date);

        counter = 1;
        if(dateState<0){
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else if(dateState==0){
            next.setVisibility(View.INVISIBLE);
            counter = 2;
        }else{
            cal.add(cal.DATE,-2);
            if(sdf.format(cal.getTime()).compareTo(date)==0) {
                previous.setVisibility(View.INVISIBLE);
                counter = 0;
            }
        }

        String[] time = date.split("-");
        cal.set(Integer.valueOf(time[2]),Integer.valueOf(time[1]),Integer.valueOf(time[0]));

//        int dateState = sdf.format(dateTime).compareTo(date);

//        previous.setVisibility(View.INVISIBLE);

        listAntrian=(ListView)findViewById(R.id.list_antrian);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter<2) {
//                    listAntrian.startAnimation(AnimationUtils.loadAnimation(AntrianActivity.this,R.anim.left_right));
                    counter += 1;
                    cal.add(Calendar.DATE, 1);
//                    header.startAnimation(AnimationUtils.loadAnimation(AntrianActivity.this,R.anim.right_left));
                    header.setText("Antrian "+sdf.format(cal.getTime()));
                    header.startAnimation(AnimationUtils.loadAnimation(QueueActivity.this,R.anim.left_right));
                    showRevList(sdf.format(cal.getTime()));


//                    this.overridePendingTransition(R.anim.left_right,R.anim.right_left);
                }
//                Toast.makeText(AntrianActivity.this,String.valueOf(counter),Toast.LENGTH_LONG).show();
                if (counter==2){
                    next.setVisibility(View.INVISIBLE);
                } else {
                    next.setVisibility(View.VISIBLE);
                    previous.setVisibility(View.VISIBLE);
                }
                Toast.makeText(QueueActivity.this,String.valueOf(counter),Toast.LENGTH_LONG).show();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter>0) {
//                    listAntrian.startAnimation(AnimationUtils.loadAnimation(AntrianActivity.this,R.anim.right_left));
                    counter -= 1;
                    cal.add(Calendar.DATE, -1);
//                    header.startAnimation(AnimationUtils.loadAnimation(AntrianActivity.this,R.anim.left_right));
                    header.setText("Antrian "+sdf.format(cal.getTime()));
                    header.startAnimation(AnimationUtils.loadAnimation(QueueActivity.this,R.anim.right_left));
                    showRevList(sdf.format(cal.getTime()));

                }
                if (counter==0){
                    previous.setVisibility(View.INVISIBLE);
                } else {
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                }
                Toast.makeText(QueueActivity.this,String.valueOf(counter),Toast.LENGTH_LONG).show();
            }
        });

        showRevList(date);

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

    private void queue() {
        int range = 10;
        int limit = 60 / range;
        int h, m;
        int endHour = Config.BOOKING_HOUR_END;
        list = new ArrayList<>();
        for (h = Config.BOOKING_HOUR_START; h < endHour; h++) {
            m = 0;
            String time = "";
            for (m = 0; m < limit; m++) {
                if (Integer.toString(h).length() == 1) {
                    time += "0";
                }

                time += Integer.toString(h) + ":";

                if (Integer.toString(m * range).length() == 1) {
                    time += "0";
                }

                time += String.valueOf(m * range);
//                Log.e(TAG, "time :" + time);

                if (h != 12 && m < 30)
                    list.add(time);

                time = "";
            }
        }
    }

    public void showRevList(final String s){

        listAntrian.startAnimation(AnimationUtils.loadAnimation(QueueActivity.this,android.R.anim.fade_in));

        queue();

        ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION+s);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.time_list,R.id.time,list){
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final View row = super.getView(position, convertView, parent);

                ref.child(getItem(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        Log.e("shared", String.valueOf(SharedPrefManager.getInstance(QueueActivity.this).getPemesan()));
                        Log.e("snap", String.valueOf(dataSnapshot.getValue(PemesanModel.class)));

                        if(SharedPrefManager.getInstance(QueueActivity.this).getPemesan().equals(dataSnapshot.getValue(PemesanModel.class))){
                            row.setBackgroundColor(Color.GREEN);

                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.e("delete", "del,"+dataSnapshot.getRef());
//                                    if(getItem(position).equals(String.valueOf(SharedPrefManager.getInstance(QueueActivity.this).getPemesan().getAntriPukul())))
                                        cancelReservation(dataSnapshot.getRef());
                                }
                            });
                        }else if (dataSnapshot.getValue() != null) {
                            row.setBackgroundColor(Color.GRAY);
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(QueueActivity.this,"pasien "+dataSnapshot.child("antriStatus").getValue(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            row.setBackgroundResource(R.color.fui_transparent);

                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final Calendar cal = Calendar.getInstance();
                                    Date dateTime = cal.getTime();
                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                                    int expired = sdf.format(dateTime).compareTo(s + " " + getItem(position));

                                    Log.e("compare to", sdf.format(dateTime) + "," + s + " " + getItem(position) + "," + expired);

                                    if(!User.isBooking(QueueActivity.this)) {

                                        if (expired < 0) {
                                            SetBooking.timeView.setText(getItem(position));
                                            SetBooking.dateView.setText(s);
                                            finish();
                                        } else {
                                            Toast.makeText(QueueActivity.this, "silahkan click pada waktu yang berbeda", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                if(getItem(position).equals("10:00")){
//                    Log.e("equal","10 equal");
//                    row.setBackgroundResource(R.color.listHighLiht);
////                    holder.divider.setBackgroundColor(Color.parseColor("#dddddd"));
//                }
                return row;

            }
        };

        listAntrian.setAdapter(adapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                list.add(dataSnapshot.getKey().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                list.remove(dataSnapshot.getKey().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cancelReservation(final DatabaseReference ref){
        PemesanModel detailPasien = SharedPrefManager.getInstance(getApplicationContext()).getPemesan();
        new AlertDialog.Builder(QueueActivity.this)
                .setTitle("antrian anda ")
                .setMessage("nama : " + detailPasien.getNamaPasien()
                        + "\ntanggal : " + detailPasien.getAntriTanggal()
                        + "\nstatus : " + detailPasien.getAntriStatus())
//                        + "\nketerangan : " + detailPasien.getAntriNo())
                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        new AlertDialog.Builder(QueueActivity.this)
                                .setTitle("PERHATIAN")
                                .setMessage("antrian anda akan dihapus \napakah anda yakin?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        ref.removeValue();
                                        SharedPrefManager.getInstance(getApplicationContext()).cancelBooking();
                                        Toast.makeText(QueueActivity.this,"antrian anda berhasil dihapus",Toast.LENGTH_LONG).show();
//                                        ((MainActivityDrawer)getApplicationContext()).setAlarmCancel();
//                                        ((MainActivityDrawer)getApplicationContext()).myOnResume();
                                        finish();
                                    }
                                })
                                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                    }
                })
                .show();
    }


}
