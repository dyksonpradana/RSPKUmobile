package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.activity.QueueActivity;
import com.rspkumobile.activity.SignInActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class SetBookingFix extends Fragment {



    private Calendar calendar;
    public static EditText dateView,timeView,nameView;
    public TextView antrian,jadwal;
    public Button btnPesan;
    private int year, month, day;
    String date;
    FragmentManager fm;
    Fragment fragment;
    private RelativeLayout barrier;

    public SetBookingFix() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_set_booking, container, false);

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.container_booking);

        barrier = (RelativeLayout)view.findViewById(R.id.barrier);

        //notify when connection lost
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.getValue(Boolean.class)){
                            barrier.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(),"mohon nyalakan paket data",Toast.LENGTH_LONG).show();
                        }else barrier.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(),"Error " + databaseError,Toast.LENGTH_LONG).show();
                    }
                });

        dateView = (EditText) view.findViewById(R.id.formTanggal);
        timeView = (EditText) view.findViewById(R.id.formPukul);
        nameView = (EditText) view.findViewById(R.id.formNama);
        antrian =(TextView) view.findViewById(R.id.lihatAntrian);
        jadwal=(TextView)view.findViewById(R.id.jadwal);
        btnPesan=(Button) view.findViewById(R.id.btnPesan);

        dateView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable s) {
                if (dateView.getText().toString().equals("")){
                    timeView.setHintTextColor(Color.parseColor("#FFC1C1C1"));
                    timeView.setEnabled(false);
//                    antrian.setTextColor(Color.parseColor("#FFC1C1C1"));
                }else{
                    timeView.setHintTextColor(Color.parseColor("#8a000000"));
                    timeView.setEnabled(true);
//                    antrian.setTextColor(Color.parseColor("#0000FF"));


                }

            }
        });
        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    pesan();
                }
                else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Perhatian")
                            .setMessage("anda perlu Log in untuk menggunakan fitur ini")
                            .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), SignInActivity.class));
                                }
                            })
                            .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });
        jadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), JadwalActivity.class));
            }
        });
        antrian.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if (SetBookingFix.dateView.getText().toString().equals("")){
                    Intent i=new Intent(getActivity(),QueueActivity.class);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    i.putExtra("header",String.valueOf(sdf.format(Calendar.getInstance().getTime())));
                    startActivity(i);
                }
                else{
                    Intent i=new Intent(getActivity(),QueueActivity.class);
                    i.putExtra("header",String.valueOf(dateView.getText()));
                    startActivity(i);}
            }
        });

        return view;
    }


    public void pesan(){

        final String tanggal,pukul,nama,inputData;

        tanggal=dateView.getText().toString();
        pukul=timeView.getText().toString();
        nama=nameView.getText().toString();

        if(TextUtils.isEmpty(tanggal)){
            dateView.setError("silahkan pilih tanggal yang di inginkan");
            dateView.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(pukul)){
            timeView.setError("silahkan pilih waktu yang di inginkan");
            timeView.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(nama)){
            nameView.setError("silahkan masukan nama pasien");
            nameView.requestFocus();
            return;
        }


        final PemesanModel pemesan = new PemesanModel(nama,tanggal,"REFKOSONG");

        //storing the user in shared preferences


        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(tanggal)
                .child(pukul)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {

                            SharedPrefManager.getInstance(getActivity()).pesanAntrian(pemesan);
//                            ((MainActivityDrawer)getActivity()).setAlarm();

                            FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl(Config.FURL_RESERVATION)
                                    .child(tanggal)
                                    .child(pukul)
                                    .setValue(pemesan)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            HashMap<String, String> value = new HashMap<String, String>();

                                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                                    .getReferenceFromUrl(Config.FURL_USERS)
                                                    .child(User.getUid())
                                                    .child("notification")
                                                    .push();
                                            // app is in background, show the notification in notification tray

                                            value.put("type","booking");
                                            value.put("head","BOOKING ANTRIAN");
                                            value.put("read","false");
                                            value.put("note",tanggal+"="+pukul);
                                            value.put("key",ref.getKey());

                                            ref.setValue(value);

                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("pemesanan sukses")
                                                    .setMessage("terima kasih telah menggunakan layanan kami")
                                                    .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            ((MainActivityDrawer)getActivity()).myOnResume(0);
//                                                            Toast.makeText(getActivity(), String.valueOf(dataSnapshot.getRef()),Toast.LENGTH_LONG).show();

                                                            changeFrame();
                                                        }
                                                    })
                                                    .show();
//                                            dateView.setText("");
//                                            timeView.setText("");
//                                            nameView.setText("");
                                        }
                                    });

                        }else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("pemesanan gagal")
                                    .setMessage("waktu yang anda pilih sudah dipesan orang lain \n" +
                                            "silahkan pilih waktu lain")
                                    .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((MainActivityDrawer)getActivity()).setTime(timeView);
                                        }
                                    })
                                    .show();


                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//                        finish();

    }

    private void changeFrame() {
        fm.beginTransaction()
//                .replace(R.id.container, new HasBooked())
                .setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .replace(R.id.container_booking, new QueueBooking())
                .commit();
    }
}
