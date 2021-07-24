package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rspkumobile.R;
import com.rspkumobile.activity.QueueActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import java.util.HashMap;


public class HasBooked extends Fragment {

    FragmentManager fm;
    Fragment fragment;


    public HasBooked() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_has_booked, container, false);

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.container_booking);

        final PemesanModel pasien= SharedPrefManager.getInstance(getActivity()).getPemesan();

        TextView text=(TextView) view.findViewById(R.id.text);
        TextView link=(TextView) view.findViewById(R.id.link);
        TextView cancel=(TextView) view.findViewById(R.id.cancel);

        String sb="anda sedang mengantri atas nama "+pasien.getNamaPasien();
//                +" pada "+pasien.getAntriTanggal()+", pukul "+pasien.getAntriPukul();

        text.setText(sb);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),QueueActivity.class);
                intent.putExtra("header",pasien.getAntriTanggal());
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation();
            }
        });
        return view;
    }

    public void cancelReservation(){

        final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
        new AlertDialog.Builder(getActivity())
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


                        new AlertDialog.Builder(getActivity())
                                .setTitle("PERHATIAN")
                                .setMessage("antrian anda akan dihapus \napakah anda yakin?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                                            .child(detailPasien.getAntriTanggal())
//                                            .child(detailPasien.getAntriPukul())
                                                .child(detailPasien.getRef())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                value.put("note",detailPasien.getAntriTanggal()
//                                                        + "x" + detailPasien.getAntriPukul());
                                                        + "x" + detailPasien.getTinjau());
                                                value.put("key",ref.getKey());

                                                ref.setValue(value);


//                                                SharedPrefManager.getInstance(getActivity()).cancelBooking();
                                                Toast.makeText(getActivity(),"antrian anda berhasil dihapus",Toast.LENGTH_LONG).show();
//                                                ((MainActivityDrawer)getActivity()).setAlarmCancel();
;

                                                fm.beginTransaction()
                                                        .replace(R.id.container_booking, new SetBooking())
                                                        .setCustomAnimations(android.R.anim.fade_in,
                                                        android.R.anim.fade_out)
                                                        .commit();
                                            }
                                        });
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
