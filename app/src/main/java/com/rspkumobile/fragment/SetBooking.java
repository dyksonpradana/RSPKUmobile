package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.activity.QueueActivity;
import com.rspkumobile.activity.SignInActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class SetBooking extends Fragment {



    private Calendar calendar;
    public static EditText dateView,timeView,nameView;
    public TextView antrian,jadwal;
    public Button btnPesan;
    private int year, month, day;
    String date;
    FragmentManager fm;
    Fragment fragment;
    private RelativeLayout barrier;
    private int num;

    public SetBooking() {
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
//                            Toast.makeText(getActivity(),"mohon nyalakan paket data",Toast.LENGTH_LONG).show();
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
                if (SetBooking.dateView.getText().toString().equals("")){
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

    private Integer setQueueNumber() {
        if(User.isBooking(getActivity())){
            num = 0;
            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();

            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION+date)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data:dataSnapshot.getChildren()){

                                if(!data.toString().equals(detailPasien.toString()))
                                    num += 1;
                                else {
                                    num += 1;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        return num;
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



        //storing the user in shared preferences

//        SharedPrefManager.getInstance(getActivity()).pesanAntrian(pemesan);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(tanggal)
                .push();

        final PemesanModel pemesan = new PemesanModel(nama,tanggal,ref.getKey().toString());
        Log.e("Ref",ref.getKey()+" "+pemesan.getRef());

        ref.setValue(pemesan)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    num=0;

//                    key.child("antriKet").setValue(num);
//                    pemesan.setKet(num);
                    Log.e("parentRef",ref.getParent().getRef().toString());
                    ref.getParent().getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("parent",num+dataSnapshot.getValue().toString());
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                Log.e("pesan",num+data.child("uid").getValue().toString()+"    "+User.getUid());

                                //fix
//                                if(data.child("uid").getValue().equals(User.getUid())) {
                                if(data.child("namaPasien").getValue().equals(pemesan.getNamaPasien())) {
                                    num += 1;
                                    data.child("antriNo").getRef().setValue(num).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pemesan.setAntriNo(num);
                                            SharedPrefManager.getInstance(getActivity()).pesanAntrian(pemesan);

                                        }
                                    });
                                    Log.e("noUrut ref",data.child("antriNo").getRef().toString());
                                    Log.e("noUrut baru",data.child("antriNo").getValue().toString());
                                    break;
                                }
                                else num +=1;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    HashMap<String, String> value = new HashMap<String, String>();

                    final DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Config.FURL_USERS)
                            .child(User.getUid())
                            .child("notification")
                            .push();
                    // app is in background, show the notification in notification tray

                    value.put("type", "booking");
                    value.put("head", "BOOKING ANTRIAN");
                    value.put("read", "false");
                    value.put("note", "anda sedang memesan antrian");
                    value.put("key", ref.getKey());

                    ref.setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            succesDialog(ref.getRef().toString().substring(ref.getRef().toString().length() - 5, ref.getRef().toString().length()));
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "error " + e.toString(), Toast.LENGTH_LONG).show();
                }
            });

//        result.getClass().getFields();
//
//        Log.e("result",result.getClass().getFields().toString());

    }

    private void changeFrame() {
        fm.beginTransaction()
//                .replace(R.id.container, new HasBooked())
                .setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .replace(R.id.container_booking, new QueueBooking())
                .commit();
    }

    private void succesDialog(final String s){
        new AlertDialog.Builder(getActivity())
                .setTitle("pemesanan sukses")
                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeFrame();
                    }
                })
                .show();
    }
}
