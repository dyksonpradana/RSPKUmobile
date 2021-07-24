package com.rspkumobile.drawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.LogHandler;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BookingAntrian extends Fragment implements View.OnTouchListener {

    FragmentManager fm;
    Fragment fragment;
    private int num;
    private ViewFlipper flipperHundreds;
    private ViewFlipper flipperTens;
    private ViewFlipper flipperUnits;
    private TextView total;
    private Calendar cal;
    private Date dateTime;
    private SimpleDateFormat sdf;
    private String date;
    private RecyclerView queueLog;
    private ProgressDialog pDialog;
    private TextView emptyLog;
    private Snackbar snack;
    private PointF start = new PointF();
    private Animation fade;
    private Boolean pause;

    public BookingAntrian() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        //make snack disappear when change to another drawer
        snack.dismiss();
        pause = true;
    }

    @Override
    public void onStart() {
        super.onStart();
//        costumeSnackContent();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.drawer_booking_antrian, container, false);

        fade = AnimationUtils.loadAnimation(getActivity(), R.anim.notif_read_fade);

//        Log.e("view-",getActivity().getCurrentFocus().toString());

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Memuat...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        cal = Calendar.getInstance();
        dateTime =  cal.getTime();
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        date = sdf.format(dateTime);

        LinearLayout blankSpot = (LinearLayout)view.findViewById(R.id.blank);
        blankSpot.setClickable(true);
        blankSpot.setOnTouchListener(this);

        total = (TextView) view.findViewById(R.id.total);
        queueCount();

        queueLog = (RecyclerView) view.findViewById(R.id.queue_log);
        queueLog.setClickable(true);
        queueLog.setOnTouchListener(this);
        emptyLog = (TextView)view.findViewById(R.id.empty_log);
        emptyLog.requestFocus();
        emptyLog.setClickable(true);
        emptyLog.setOnTouchListener(this);
        queueLog.setNestedScrollingEnabled(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // these codes make new item stack from top
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        
        queueLog.setLayoutManager(mLayoutManager);
        attachRecyclerViewAdapter();

        flipperHundreds = (ViewFlipper)view.findViewById(R.id.hundreds);
        flipperHundreds.setClickable(true);
        flipperHundreds.setOnTouchListener(this);
        flipperHundreds.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_y));
        flipperHundreds.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_y));
        
        
        flipperTens = (ViewFlipper)view.findViewById(R.id.tens);
        flipperTens.setClickable(true);
        flipperTens.setOnTouchListener(this);
        flipperTens.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_y));
        flipperTens.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_y));

        
        flipperUnits = (ViewFlipper)view.findViewById(R.id.units);
        flipperUnits.setClickable(true);
        flipperUnits.setOnTouchListener(this);
        flipperUnits.setInAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_y));
        flipperUnits.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_y));

//        snack = Snackbar.make(getActivity().getCurrentFocus()
//                ,"pesan antrian disini",
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction("ANTRI", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialogAntri();
//                    }
//                });

        costumeSnackContent();

        counter();
        
        
        return view;
    }

    private void queueCount(){
        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(date)
                .child("antrian")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            total.setText("000");
                        }else {
                            String count = null;
                            Log.e("total", dataSnapshot.getValue().toString());
                            total.startAnimation(fade);
                            if (dataSnapshot.getChildrenCount() < 10) {
                                count = "00";
                            } else if (dataSnapshot.getChildrenCount() < 100) count = "0";
                            count += String.valueOf(dataSnapshot.getChildrenCount());
                            total.setText(count);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    private void costumeSnackContent() {
        //fix

        total.setFocusable(true);
        total.requestFocus();
//        try {
            Log.e("try","in");
            if(User.isBooking(getActivity())){

                Log.e("log in","in");

                final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
                snack = Snackbar.make(getActivity().getCurrentFocus(),"",Snackbar.LENGTH_INDEFINITE);
                snack.setText("No. "+detailPasien.getAntriNo() + ", Kode: " +
                        detailPasien.getRef().substring(detailPasien.getRef().toString().length() - 5,
                                detailPasien.getRef().toString().length()));

                                snack.setAction("BATAL", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogCancle(detailPasien);
                                    }
                                });

            }else{

                    snack = Snackbar.make(getActivity().getCurrentFocus()
                            , "pesan antrian disini",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("ANTRI", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogAntri();
                                }
                            });

            }

//        }catch (Exception e){
////            snack = null;
//            Log.e("snack", e.toString());
//        }

        snack.show();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
//                getAc
                float dy = event.getY() - start.y;
                Log.e("dy",dy+"");
                if(dy>0) { snack.dismiss();dy = 0;}
                else if (dy<0) if(!snack.isShown()){ costumeSnackContent();dy=0;}
                break;
        }
        return false;
    }

    private void dialogCancle(final PemesanModel model) {
        new AlertDialog.Builder(getActivity())
                .setTitle("PERHATIAN")
                .setMessage("antrian anda akan dihapus \napakah anda yakin?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                                        ref.removeValue();
                        if(snack!=null);snack.dismiss();
                        SharedPrefManager.getInstance(getActivity()).cancelBooking();
                        doFirebase(model);
                        costumeSnackContent();
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void doFirebase(PemesanModel model) {

        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(dateTime);

        LogHandler logData = new LogHandler("antrian "+model.getAntriNo()+" membatalkan antrian",time);

//        HashMap<String, String> val = new HashMap<String, String>();
//        val.put("log","antrian "+model.getAntriNo()+" membatalkan antrian");
//        val.put("time",time);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(model.getAntriTanggal());
        ref.child("antrian").child(model.getRef()).child("antriStatus").setValue("batal");
        ref.child("antrian").child(model.getRef()).child("uid").setValue("-");
        ref.child("log").push().setValue(logData);

        JSONObject val2 = new JSONObject();
        final DatabaseReference ref2 = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("notification")
                .push();

        NotifHandler notifData = new NotifHandler("cancel"
                ,"anda membatalkan antrian", "booking", false, ref2.getKey(), time);

//        HashMap<String, String> val2 = new HashMap<String, String>();
//        try {
//            val2.put("type", "booking");
//            val2.put("time", time);
//            val2.put("head", "cancel");
//            val2.put("note", "anda membatalkan antrian");
//            val2.put("read", false);
//            val2.put("key", ref2.getKey());

            ref2.setValue(notifData);
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                ref2.child("read").setValue(false);
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONObject value = new JSONObject();
//        try {
//            value.put("type", "booking");
//            value.put("head", "expired");
//            value.put("read", false);
//            value.put("note", "antrian anda kadaluarsa karena hari");
//            value.put("key", ref.getKey());
//            value.put("time",time);
//
//            ref.setValue(value);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        Toast.makeText(getActivity(),"antrian anda berhasil dihapus",Toast.LENGTH_LONG).show();
    }

    private void dialogAntri() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptView = li.inflate(R.layout.prompt,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setView(promptView);

        final EditText userInput = (EditText)promptView.findViewById(R.id.reserver);

        dialogBuilder.setTitle("Nama Pemesan")
//                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("pesan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(userInput.getText().toString())){
                            userInput.setError("tidak");
                            userInput.requestFocus();
                            return;
                        }else
                            setBooking(userInput.getText().toString());
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(userInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(userInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setBooking(String name) {
        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(date)
                .child("antrian")
                .push();

        final PemesanModel pemesan = new PemesanModel(name,date,ref.getKey().toString());
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

                        Calendar cal = Calendar.getInstance();
                        Date dateTime = cal.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String time = sdf.format(dateTime);

//                        HashMap<String, String> value = new HashMap<String, String>();

                        final DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReferenceFromUrl(Config.FURL_USERS)
                                .child(User.getUid())
                                .child("notification")
                                .push();
                        // app is in background, show the notification in notification tray

                        NotifHandler notifData = new NotifHandler("processing"
                                ,"anda sedang memesan antrian", "booking", false, ref.getKey(), time);

//                        value.put("type", "booking");
//                        value.put("time", time);
//                        value.put("head", "processing");
////                        value.put("read", false);
//                        value.put("note", "anda sedang memesan antrian");
//                        value.put("key", ref.getKey());

                        ref.setValue(notifData);
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                ref.child("read").setValue(false);
//                                succesDialog(pemesan.getRef().substring(pemesan.getRef().length() - 5, pemesan.getRef().length()));
//                            }
//                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "error " + e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void succesDialog(final String s){
//        new AlertDialog.Builder(getActivity())
//                .setCancelable(false)
//                .setTitle("pemesanan sukses")
//                .setMessage("kode pesanan anda adalah "+s
//                        +"\nterima kasih telah menggunakan layanan kami")
//                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptView = li.inflate(R.layout.prompt_booking_success,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setView(promptView);

        final TextView code = (TextView) promptView.findViewById(R.id.code);
        code.setText(s);

        dialogBuilder.setTitle("Pemesanan sukses")
                .setCancelable(false)
//                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        snack.show();
                        costumeSnackContent();
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    private void attachRecyclerViewAdapter() {
        pDialog.show();
//        swipeLayout.setRefreshing(true);
        final RecyclerView.Adapter adapter = newAdapter();
//        notificationList.refreshDrawableState();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
//                notificationList.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

            }

            @Override
            public void onChanged() {
                super.onChanged();

            }
        });

        queueLog.setAdapter(adapter);

    }



    public class LogHolder extends RecyclerView.ViewHolder{
        TextView body,time;

        public LogHolder(View v){
            super(v);

            body=(TextView)v.findViewById(R.id.text);
            time=(TextView)v.findViewById(R.id.time);
        }
    }



    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<LogHandler> options =
                new FirebaseRecyclerOptions.Builder<LogHandler>()
                        .setQuery(FirebaseDatabase.getInstance()
                                        .getReferenceFromUrl(Config.FURL_RESERVATION)
                                        .child(date)
                                        .child("log")
//                                        .limitToLast(20)
                                , LogHandler.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<LogHandler, LogHolder>(options) {

            @Override
            public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_log_antrian, parent, false);

                return new LogHolder(view);
            }

            @Override
            protected void onBindViewHolder(final LogHolder holder, final int position, final LogHandler model) {
                holder.body.setText(model.getLog());
                holder.time.setText(model.getTime());
                holder.setIsRecyclable(false);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                notificationList.setVisibility(getItemCount() != 0 ? View.VISIBLE : View.INVISIBLE);

                if(getItemCount()==0) {
                    queueLog.removeAllViews();
                    queueLog.setVisibility(View.INVISIBLE);
                    emptyLog.setText("kosong");
                    emptyLog.setVisibility(View.VISIBLE);
                }else{
                    emptyLog.setVisibility(View.INVISIBLE);
                    queueLog.setVisibility(View.VISIBLE);
                }
                pDialog.dismiss();

            }


        };


    }


    private void counter() {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("antrian").getValue()==null) {
                    flipperUnits.setDisplayedChild(0);
                    flipperTens.setDisplayedChild(0);
                    flipperHundreds.setDisplayedChild(0);
                }else{

                    final Integer giliran = dataSnapshot.child("giliran").getValue() == null ? 1 : dataSnapshot.child("giliran").getValue(Integer.class);
                    Log.e("giliran", giliran.toString() + " ");

                    Runnable flipController = new Runnable() {
                        @Override
                        public void run() {
                            flipperTens.setDisplayedChild((giliran % 100) / 10);
                            ;
                        }
                    };

                    Runnable flipController2 = new Runnable() {
                        @Override
                        public void run() {
                            flipperHundreds.setDisplayedChild(giliran / 100);
                        }
                    };

                    //unit
                    // even
//                for(int i=0;i<100;i++){
                    if (giliran % 10 != flipperUnits.getDisplayedChild()) {
                        flipperUnits.setDisplayedChild(giliran % 10);
                    }
                    if ((giliran % 100) / 10 != flipperTens.getDisplayedChild()) {
                        flipperTens.postDelayed(flipController, 1000);
                    }
                    if (giliran / 100 != flipperHundreds.getDisplayedChild()) {
                        flipperHundreds.postDelayed(flipController2, 1500);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        counter();
        pause = false;
//        costumeSnackContent();
        Log.e("view-",getActivity().getCurrentFocus().toString());
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

        Toast.makeText(getActivity(),item.getTitle().toString()+User.isLogedIn(),Toast.LENGTH_LONG).show();

        if (item.getTitle().equals("Antri")) {
//            if(User.isLogedIn()){
//            changeFrame(1);
            item.setTitle(R.string.set_back);
//            } else requireLogin();
        } else if (item.getTitle().equals("kembali")){
            item.setTitle(R.string.set_booking);
//            changeFrame(0);
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
//                            SharedPrefManager.getInstance(getActivity()).cancelBooking();
//                            changeFrame(0);
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
//        final MenuItem actionLogIn = menu.findItem(R.id.masuk);
//        if (User.isLogedIn()) actionLogIn.setTitle("Keluar");
//        else actionLogIn.setTitle("Masuk");
    }


}
