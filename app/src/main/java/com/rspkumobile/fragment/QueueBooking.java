package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.activity.Conversation;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.PemesanModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QueueBooking extends Fragment implements View.OnTouchListener {
    private static final String TAG = Conversation.class.getSimpleName();
    private RecyclerView QueueList;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private TextView emptyQueue;
    private ImageView refresh;
    private SwipeRefreshLayout notifLayout;
    private SwipeRefreshLayout swipeLayout;

    FragmentManager fm;
    Fragment fragment;
    private boolean Started;
    Handler handler;
    private ProgressDialog pDialog;
    private String date;
    private Date dateTime;
    private SimpleDateFormat sdf;
    private int num;
    private Calendar cal;
    private GestureDetectorCompat detector;
    private Snackbar snack;
    private PointF start = new PointF();
    private float dx,dy;
    private SharedPreferences sharedPreferences;
    private String URUTAN = "giliran";
    private String NOMER = "sekarang";
    private SharedPreferences.Editor  editor;

    @Override
    public void onResume() {
        super.onResume();
        attachRecyclerViewAdapter();
    }

//    private void setQueueNumber() {
//        if(User.isBooking(getActivity())){
//            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
//
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION+date)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for(DataSnapshot data:dataSnapshot.getChildren()){
//                                if(!data.toString().equals(detailPasien.toString()))
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_booking, container, false);

        sharedPreferences=getActivity().getSharedPreferences(URUTAN, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        cal = Calendar.getInstance();
//        cal.set(year,month,day);
//        cal.add(cal.DATE,2);
        dateTime =  cal.getTime();
        sdf = new SimpleDateFormat("dd-MM-yyyy");

        date = sdf.format(dateTime);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Memuat...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.container_booking);

        emptyQueue = (TextView)view.findViewById(R.id.empty_list);

        QueueList = (RecyclerView)view.findViewById(R.id.notification_list);
        QueueList.requestFocus();
        QueueList.setNestedScrollingEnabled(false);
//        QueueList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        // these codes make new item stack from top
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);

        mDividerItemDecoration = new DividerItemDecoration(QueueList.getContext(),mLayoutManager.getOrientation());

        QueueList.addItemDecoration(mDividerItemDecoration);
        QueueList.setLayoutManager(mLayoutManager);


        attachRecyclerViewAdapter();

        costumeSnackContent();

        QueueList.setOnTouchListener(this);

        QueueList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    snack.dismiss();
                }
            }
        });

        return  view;
    }

    private void costumeSnackContent() {
        if(User.isBooking(getActivity())){

            QueueList.requestFocus();
            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
            snack = Snackbar.make(getActivity().getCurrentFocus(),"",Snackbar.LENGTH_INDEFINITE);
            snack.setText("No. "+detailPasien.getAntriNo() + ", Kode :" +
                            detailPasien.getRef().substring(detailPasien.getRef().toString().length() - 5,
                                    detailPasien.getRef().toString().length()));

            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                    .child(detailPasien.getAntriTanggal())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                            int total = (int) dataSnapshot.getChildrenCount()-1;
                            int urutan=1;

                            Log.e("urutan",dataSnapshot.toString()+total);
                            if (dataSnapshot.child("urutan").getValue()!=null) urutan  = dataSnapshot.child("urutan").getValue(Integer.class);
                            snack.setAction(""+urutan+"/"+total, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();



                            editor.putInt(NOMER,urutan);
                            editor.apply();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

//            final PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
//                    .child(detailPasien.getAntriTanggal())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String urutan="1";
//                            if(dataSnapshot.child("urutan")!=null){
//                                urutan = String.valueOf(dataSnapshot.getValue());
//                            }
//                            Log.e("uid test",urutan+dataSnapshot.child(detailPasien.getRef()).child("uid").getValue().toString());
//                            if(dataSnapshot.child(detailPasien.getRef()).child("uid").getValue().equals(detailPasien.getUid()))
//                                snack = Snackbar.make(getActivity().getCurrentFocus()
//                                        ,detailPasien.getAntriNo() + " " +
//                                                detailPasien.getRef()
//                                                        .indexOf(detailPasien.getRef().length()-4,
//                                                                detailPasien.getRef().length()),
//                                        Snackbar.LENGTH_INDEFINITE)
//                                        .setAction(urutan, null);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

        }else{
            snack = Snackbar.make(getActivity().getCurrentFocus()
                    ,"pesan antrian disini",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ANTRI", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!User.isBooking(getActivity())) {
                                fm.beginTransaction()
                                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                        .replace(R.id.container_booking, new SetBooking())
                                        .commit();
                            }
                        }
                    });
        }
//        if (snack.isShown())
//            snack.dismiss();
//        else snack.show();
//        else snack.dismiss();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

//        if (show==true)
//            costumeSnackContent();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
//                costumeSnackContent(1);
//                snack.show();
//                break;
////            case MotionEvent.ACTION_MOVE:
////                dx = event.getX() - start.x;
////                dy = event.getY() - start.y;
//////                costumeSnackContent(dy);
                break;
            case MotionEvent.ACTION_UP:
//                getAc
                dy = event.getY() - start.y;
                Log.e("dy",dy+"");
                if(dy>=0)
                    snack.dismiss();
                else if (dy<0) costumeSnackContent();
                break;
        }
//
        return false;
    }


    public class NotifHolder extends RecyclerView.ViewHolder{
        LinearLayout container;
        TextView head;
        TextView body;

        public NotifHolder(View v){
            super(v);

            head=(TextView)v.findViewById(R.id.tv_head);
            body=(TextView)v.findViewById(R.id.tv_body);
            container=(LinearLayout)v.findViewById(R.id.notif_container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private void attachRecyclerViewAdapter() {
        pDialog.show();
        final RecyclerView.Adapter adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

            }

            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        QueueList.setAdapter(adapter);

    }

    protected RecyclerView.Adapter newAdapter() {

        final Query query = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION + date)
                .orderByChild("tinjau").equalTo(true);


        FirebaseRecyclerOptions<PemesanModel> options =
                new FirebaseRecyclerOptions.Builder<PemesanModel>()
                        .setQuery(query
                                , PemesanModel.class)
                        .setLifecycleOwner(this)
                        .build();

//        Log.e("option", String.valueOf(options.getSnapshots().size()));

        return new FirebaseRecyclerAdapter<PemesanModel, NotifHolder>(options) {
            @Override
            public NotifHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_notification, parent, false);

                return new NotifHolder(view);
            }

            @Override
            protected void onBindViewHolder(final NotifHolder holder, final int position, final PemesanModel model) {
                holder.head.setText(model.getNamaPasien());
                holder.body.setText(model.getAntriTanggal()+" "+model.getAntriNo());
                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.e(" sharedref compare",SharedPrefManager.getInstance(getActivity()).getPemesan().getUid());
                        Log.e("to",model.getUid());

                        String uidPemesan = SharedPrefManager.getInstance(getActivity()).getPemesan().getUid();

                        if (uidPemesan!=null) {
                            Log.e(" sharedref compare",SharedPrefManager.getInstance(getActivity()).getPemesan().getUid());

                            //fix
//                            if (uidPemesan.equals(model.getUid())) {
                            if (SharedPrefManager.getInstance(getActivity()).getPemesan().getNamaPasien().equals(model.getNamaPasien())) {
                                showDetail();
                            } else {

                            }
                        } else Log.e(" sharedref compare","null");
                    }
                });
                if(position==2){
//                    holder.container.setVisibility(View.GONE);
                }

                holder.setIsRecyclable(false);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                QueueList.setVisibility(getItemCount() != 0 ? View.VISIBLE : View.INVISIBLE);

                if(getItemCount()==0) {
                    QueueList.removeAllViews();
                    QueueList.setVisibility(View.INVISIBLE);
                    emptyQueue.setVisibility(View.VISIBLE);
                }else{
                    emptyQueue.setVisibility(View.INVISIBLE);
                    QueueList.setVisibility(View.VISIBLE);
                }
                pDialog.dismiss();

            }
        };
    }

    private void showDetail() {
        PemesanModel detailPasien = SharedPrefManager.getInstance(getActivity()).getPemesan();

        new AlertDialog.Builder(getActivity())
                .setTitle("antrian anda ")
                .setMessage("nama : " + detailPasien.getNamaPasien()
                        + "\ntanggal : " + detailPasien.getAntriTanggal()
                        + "\nstatus : " + detailPasien.getAntriStatus()
                        + "\nnomer : " + detailPasien.getAntriNo())
                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialogDelete();

                    }
                })
                .show();
    }

    private void dialogDelete() {
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

    private void dialog(int i) {

        String msg = "";

        if(i==0){
            msg = "pertanyaan anda tidak disetujui untuk ditampilkan oleh admin," +
                    " mungkin dikarenakan konten yang tidak sesuai." +
                    "\n silahkan mencoba membuat pertanyaan yang lain";
        }else if(i==1) msg = "terima kasih anda telah menggunakan fitur booking\nsemoga anda diberi kesehatan";

        new AlertDialog.Builder(getActivity())
                .setTitle(null)
                .setMessage(msg)
                .setPositiveButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//
////        Toast.makeText(getActivity(), id + "All bersihkan!", Toast.LENGTH_LONG).show();
//
//        if (id == R.id.action_mark_all_read) {
////            Toast.makeText(getActivity(), "All read", Toast.LENGTH_LONG).show();
//            Toast.makeText(getActivity(), id + "All bersihkan!", Toast.LENGTH_LONG).show();
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                    .child(User.getUid())
//                    .child("notification")
//                    .orderByChild("read").equalTo("false")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.getValue()!=null){
//
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    data.getRef().child("read").setValue("true");
//                                }
//                                attachRecyclerViewAdapter();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
////            refreshNotification();
////            attachRecyclerViewAdapter();
//        }
//
//        // user is in action_bar_notification fragment
//        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getActivity(), id + "All delete!", Toast.LENGTH_LONG).show();
////            Toast.makeText(getApplicationContext(), "Clear all action_bar_notification!", Toast.LENGTH_LONG).show();
//
//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                    .child(User.getUid())
//                    .child("notification")
//                    .removeValue()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
////                            refreshNotification();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getActivity(), "terjadi kesalahan dalam proses "+e, Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//        }
//
//        item.setChecked(false);
//        return super.onOptionsItemSelected(item);
//    }
}