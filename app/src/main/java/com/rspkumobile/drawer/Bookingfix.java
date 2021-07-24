package com.rspkumobile.drawer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.app.Config;
import com.rspkumobile.fragment.QueueBooking;
import com.rspkumobile.fragment.SetBooking;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;


public class Bookingfix extends Fragment {

    FragmentManager fm;
    Fragment fragment;
    private int num;

    public Bookingfix() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.drawer_bookingfix, container, false);

        setHasOptionsMenu(true);

        fm = getActivity().getSupportFragmentManager();

        changeFrame(0);

        return view;
    }

    public void changeFrame(int i){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_booking);
//
//        if (User.isBooking(getActivity())) {
//            fragment = new HasBooked();
//        } else fragment = new SetBooking();

        if (i==0) {
            fragment = new QueueBooking();
        } else if(i!=0&&!User.isBooking(getActivity())) fragment = new SetBooking();


        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.container_booking, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

//        setQueueNumber();

        PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();;

        Log.e("nomer saya",detailPasien.getAntriNo()+"");

//        showSnackBar();
    }

//    private void getQueueNumber() {
//        if(User.isBooking(getActivity())){
//            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
//
//            FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl(Config.FURL_RESERVATION+detailPasien.getAntriTanggal().toString())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for(DataSnapshot data:dataSnapshot.getChildren()){
//                                if(!data.toString().equals(detailPasien.getAllDetail().toString()))
//                                    num += 1;
//                                else {
//                                    num += 1;
//                                    break;
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//        }
//    }

    private void setQueueNumber() {
        if(User.isBooking(getActivity())){
            num = 0;
//            SharedPrefManager.getInstance(getActivity()).pesanAntrian(
//                    new PemesanModel("udydg","09-01-2018","02:10","antri",1,FirebaseDatabase)
//            );
            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();

            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION+detailPasien.getAntriTanggal())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.e("antrian",dataSnapshot.getValue().toString());
                            for(DataSnapshot data:dataSnapshot.getChildren()){
//                                    Log.e("compare", data.child("namaPasien").getValue().toString());
                                    Log.e("to", detailPasien.getNamaPasien());
//                                    Log.e("comparingTo", String.valueOf((data.getValue(PemesanModel.class)== detailPasien))
                                //fix
//                                if(data.child("uid").getValue().toString().equals(User.getUid())) {
                                if(data.child("namaPasien").getValue().toString().equals(detailPasien.getNamaPasien())) {
//                                    Log.e("compare", data.getValue(PemesanModel.class).toString());
//                                    Log.e("to", detailPasien.toString());
//                                    Log.e("comparingTo", String.valueOf((data.getValue(PemesanModel.class)== detailPasien)));
                                    num += 1;
                                    Log.e("antrian",num+"");
                                    data.getRef().child("antriNo").setValue(num);
//                                    showSnackBar(num);
                                    PemesanModel pemesan = data.getValue(PemesanModel.class);

                                    Log.e("pemesan",pemesan.getAntriNo()+pemesan.getNamaPasien());
                                    SharedPrefManager.getInstance(getActivity()).updateNomerAntrian(num);
//                                    SharedPrefManager.getInstance(getActivity()).pesanAntrian(pemesan);

                                    break;
                                }
                                else {
                                    num += 1;
                                    Log.e("urutan",num+"");
                                }

                            }

//                            SharedPrefManager.getInstance(getActivity()).pesanAntrian(dataSnapshot.
//                                    new PemesanModel("udydg","09-01-2018","02:10","antri",null)
//                            );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void showSnackBar(int s) {
        final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
        final Snackbar snackbar = Snackbar.make(getView(),"kode antrian anda adalah "+s, Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        snackbar.show();

        if(User.isBooking(getActivity())){
//            getView().setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_DOWN:
//                            snackbar.show();
//                            break;
//                    }
//                    return true;
//                }
//            });
        }
    }

    private void requireLogin(){
        Toast.makeText(getActivity(),"masuk",Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(getActivity())
                .setTitle("Perhatian")
                .setMessage("anda harus Log In untuk menggunakan fasilitas ini"
                        + "\nLog In sekarang?"
                )
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(Conversation.this, SignInActivity.class));
                        ((MainActivityDrawer)getActivity()).signIn();
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

        Toast.makeText(getActivity(),item.getTitle().toString()+User.isLogedIn(),Toast.LENGTH_LONG).show();

        if (item.getTitle().equals("Antri")) {
//            if(User.isLogedIn()){
            changeFrame(1);
            item.setTitle(R.string.set_back);
//            } else requireLogin();
        } else if (item.getTitle().equals("kembali")){
            item.setTitle(R.string.set_booking);
            changeFrame(0);
        } else if(item.getTitle().equals("batal")){
            new AlertDialog.Builder(getActivity())
                    .setTitle("PERHATIAN")
                    .setMessage("antrian anda akan dihapus \napakah anda yakin?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("hapus", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                        ref.removeValue();
                            SharedPrefManager.getInstance(getActivity()).cancelBooking();
                            Toast.makeText(getActivity(),"antrian anda berhasil dihapus",Toast.LENGTH_LONG).show();
                            item.setTitle(R.string.set_booking);
                            SharedPrefManager.getInstance(getActivity()).cancelBooking();
                            changeFrame(0);
//                                        ((MainActivityDrawer)getApplicationContext()).myOnResume();
                        }
                    })
                    .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }

        item.setChecked(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem actionLogIn = menu.findItem(R.id.masuk);
        if (User.isLogedIn()) actionLogIn.setTitle("Keluar");
        else actionLogIn.setTitle("Masuk");
    }


}
