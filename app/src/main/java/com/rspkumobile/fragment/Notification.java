package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.activity.Conversation;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

public class Notification extends Fragment {
    private static final String TAG = Conversation.class.getSimpleName();
    private RecyclerView notificationList;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private TextView emptyNotification;
    private ImageView refresh;
    private SwipeRefreshLayout notifLayout;
    private SwipeRefreshLayout swipeLayout;

    FragmentManager fm;
    Fragment fragment;
    private boolean Started;
    Handler handler;
    private ProgressDialog pDialog;

    @Override
    public void onResume() {
        super.onResume();
        attachRecyclerViewAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem actionLogIn = menu.findItem(R.id.masuk);
//        if (User.isLogedIn()) actionLogIn.setTitle("Keluar");
//        else actionLogIn.setTitle("Masuk");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Memuat...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.container_notif);

        emptyNotification = (TextView)view.findViewById(R.id.empty_list);
//        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_layout);
//        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.fui_bgTwitter)
//                ,getResources().getColor(R.color.fui_bgPhone)
//                ,getResources().getColor(R.color.colorPrimaryDark));
//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifLayout.setRefreshing(false);
//                    }
//                },5000);
//            }
//        });
        notificationList = (RecyclerView)view.findViewById(R.id.notification_list);
        notificationList.setNestedScrollingEnabled(false);
//        notificationList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        // these codes make new item stack from top
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

//        mDividerItemDecoration = new DividerItemDecoration(notificationList.getContext(),mLayoutManager.getOrientation());
//
//        notificationList.addItemDecoration(mDividerItemDecoration);
        notificationList.setLayoutManager(mLayoutManager);


        attachRecyclerViewAdapter();

//        getActivity().getActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
//            @Override
//            public void onMenuVisibilityChanged(boolean isVisible) {
//                Toast.makeText(getActivity(), String.valueOf(getActivity().getActionBar().getTitle())
//                        +getActivity().getActionBar().getSubtitle()
//                        +getActivity().getActionBar(),Toast.LENGTH_LONG).show();
//            }
//        });

        return  view;
    }

    public void refreshNotification(){

        if(getActivity().getTitle().equals("Pemberitahuan")){
            fm.beginTransaction()
                    .replace(R.id.container_booking, new com.rspkumobile.fragment.Notification())
                    .commit();
        }
    }

//    private class NotifHandler{
//
//        String head;
//        String body;
//
//        public NotifHandler(){
//        }
//
//        public NotifHandler(String h, String b){
//            this.head=h;
//            this.body=b;
//        }
//
//        public String getHeader(){
//            return head;
//        }
//
//        public void setHeader(String s){
//            this.head = s;
//        }
//
//        public String getBody(){
//            return body;
//        }
//
//        public void setBody(String s){this.body=s;}
//
//
//    }

    public class NotifHolder extends RecyclerView.ViewHolder{
        LinearLayout container;
        TextView head;
        TextView body;
        TextView time;
        View root;
        private String header;
        private String bodyText;
        private String[] val;

        public NotifHolder(View v){
            super(v);

            this.root = v;

            head=(TextView)v.findViewById(R.id.tv_head);
            body=(TextView)v.findViewById(R.id.tv_body);
            time=(TextView)v.findViewById(R.id.tv_time);
            container=(LinearLayout)v.findViewById(R.id.notif_container);

        }

        public void bindTo(final NotifHandler model){

            final Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.notif_read);
            final Animation fade = AnimationUtils.loadAnimation(getActivity(), R.anim.notif_read_fade);
//            fade.setStartOffset(500);

            if(model.getRead()==true) {
                head.setTypeface(Typeface.DEFAULT);
                body.setTypeface(Typeface.DEFAULT);
            }else {
                head.setTypeface(Typeface.DEFAULT_BOLD);
                body.setTypeface(Typeface.DEFAULT_BOLD);
            }

            header = "pemberitahuan";
            bodyText = "rspku";

            if(model.getType().equals("booking")) {

                val = model.getNote().split("_");
                String service = val[0];
                String dates = val[1];
                String time = val[2];
                if(User.isDoctor(getActivity())) {
//                    val = model.getNote().split("_");
                    header = "seseorang melakukan BOOKING ";
//                    bodyText = val[0]+val[1]+", pukul "+val[2];
                    bodyText = service+dates+", pukul "+time;
                }else{
//                if(model.getHeader().equals("done")) header += " SUKSES";
//                else if(model.getHeader().equals("canceled")) header += " DIBATALKAN";
//                else if(model.getHeader().equals("skipped")) header += " DILEWATI";
//                else if(model.getHeader().equals("cancel")) header += " GAGAL";
//                else if(model.getHeader().equals("expired")) header += " KADALUARSA";
//                else
//                if(model.getHeader().equals(Config.BOOKING_STATUS[0])||
//                        model.getHeader().equals(Config.BOOKING_STATUS[1])){
//                    SharedPrefManager.getInstance(getActivity()).setToExpand(model.getNote());
//                    ((MainActivityDrawer)getActivity()).myOnResume(1);
////                                }else if(model.getHeader().equals(Config.BOOKING_STATUS[3])){
////                                    dialog(model.getHeader(),model.getNote());
////                                }else if(model.getHeader().equals(Config.BOOKING_STATUS[4])){
////                                    dialog(model.getHeader(),model.getNote());
//                }else dialog(model.getHeader(),model.getNote());
                    header = "BOOKING "+service;
                    if (model.getHeader().equals(Config.BOOKING_STATUS[0])) {
//                    val = model.getNote().split("_");
//                    header += val[0];
//                        header += model.getNote();
//                    bodyText = "anda sedang memesan "+val[0];
                        bodyText = "anda sedang memesan " + header;
                    } else if (model.getHeader().equals(Config.BOOKING_STATUS[1])) {
//                    val = model.getNote().split("_");
//                    header += val[0];
//                        header += model.getNote();
                        bodyText = "anda melakukan konfirmasi " + header;
                    } else if (model.getHeader().equals(Config.BOOKING_STATUS[3])) {
//                    val = model.getNote().split("_");
//                    header += val[0];
//                        header += model.getNote();
                        bodyText = header + " selesai";
                    } else if (model.getHeader().equals(Config.BOOKING_STATUS[4])) {
//                    val = model.getNote().split("_");
//                    header += val[0];
//                        header += model.getNote().replace("_", " ");
                        bodyText = "mohon segera konfirmasi booking anda di resepsionis";
                    } else if (model.getHeader().equals(Config.BOOKING_STATUS[5])) {
//                    val = model.getNote().split("_");
//                    header += val[0];
//                        header += model.getNote().replace("_", " ");
                        bodyText = "booking hangus sebab waktu praktek telah tutup";
                    }
                }
                head.setText(header);
            }else if(model.getType().equals("conversation")){

                Log.e("notificationCheck",model.getType()+" "+model.getHeader());

                bodyText = model.getNote();

//                String[] headers = model.getHeader().split("_");
                String[] headers = new  String[]{model.getHeader().substring(0,model.getHeader().lastIndexOf("_")),
                        model.getHeader().substring(model.getHeader().lastIndexOf("_")+1)};

                header = headers[1];

//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_APPROVED)
//                        .child(model.getHeader())
//                        .child("title")
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                header = dataSnapshot.getValue(String.class);
//                                head.setText(header);
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

            }else{
                header = model.getHeader();
                bodyText = model.getNote();
                if(User.isDoctor(getActivity()))
                    bodyText = "seseorang menanyakan sesuatu";

//                head.setText(header);
            }

            body.setText(bodyText);

            head.setText(header);

            time.setText(model.getTime());

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!model.getRead()==true) {
//                        container.startAnimation(slide);
                        container.startAnimation(fade);
                    }
                    FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                            .child(User.getUid())
                            .child("notification")
                            .child(model.getKey())
                            .child("read").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue(Boolean.class)==false){
                                dataSnapshot.getRef().setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        container.startAnimation(fade);
                                    }
                                });

                            }

                            if(model.getType().equals("conversation")) {

                                final String[] headers = new  String[]{model.getHeader().substring(0,model.getHeader().lastIndexOf("_")),
                                        model.getHeader().substring(model.getHeader().lastIndexOf("_")+1)};

                                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_APPROVED)
                                        .child(headers[0])
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot==null){
                                                    //TODO
                                                    dialog("conversation","");
                                                }
                                                else{
                                                    Intent i = new Intent(getActivity(), Conversation.class);
                                                    i.putExtra("topicKey", headers[0]);
                                                    startActivity(i);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

//                                Intent i = new Intent(getActivity(), Conversation.class);
//                                i.putExtra("topicKey", headers[0]);
//                                startActivity(i);
                            }
//                            else if(model.getType().equals("booking")&&model.getHeader().equals(Config.BOOKING_STATUS[0])){
                            else if(model.getType().equals("booking")){
//                                Toast.makeText(getActivity(),header,Toast.LENGTH_LONG).show();
                                if(User.isDoctor(getActivity())) {
                                    final String service = val[0];
                                    String[] dates = val[1].replace(" ","").split(",");
                                    String day = dates[0];
                                    String date = dates[1];
                                    String bookingTime = val[2];
                                    if(!Helper.isExpired(getActivity(), day, date, bookingTime, service)) {
                                        String[] dMy = date.split("-");
                                        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
                                        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                                                .child(yMd).child(service).child(bookingTime)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Toast.makeText(getActivity()," "+dataSnapshot,Toast.LENGTH_LONG).show();
                                                        if (dataSnapshot.getValue() == null) {
                                                            //TODO exception when null
                                                            dialog("deleted", null);
                                                        } else {
                                                            String status = dataSnapshot.child("status").getValue(String.class);
//                                                        if(!Config.BOOKING_STATUS_TYPE_EX[2].equals(status)||!Config.BOOKING_STATUS_TYPE_EX[4].equals(status)) {
                                                            if (!Config.BOOKING_STATUS[3].equals(status) || !Config.BOOKING_STATUS[5].equals(status)) {
                                                                SharedPrefManager.getInstance(getActivity()).setToExpand(service, val[1]);
                                                                ((MainActivityDrawer) getActivity()).myOnResume(1);
                                                            } else dialog("over", null);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }else{
                                        dialog("over", null);
                                    }
                                }else{
//                                    Toast.makeText(getActivity(),model.getHeader()+" USER "+header,Toast.LENGTH_LONG).show();
                                    //show users booking
                                    val = model.getNote().split("_");
                                    final String service = val[0];
//                                    final String service = model.getNote();
                                    if (model.getHeader().equals(Config.BOOKING_STATUS[0]) ||
                                            model.getHeader().equals(Config.BOOKING_STATUS[1])) {

                                        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                                                .child(User.getUid()).child("booking").child(service)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() == null)
                                                           dialog("over",model.getNote());
                                                        else {
                                                            SharedPrefManager.getInstance(getActivity()).setToExpand(service);
                                                            ((MainActivityDrawer) getActivity()).myOnResume(1);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
//                                    SharedPrefManager.getInstance(getActivity()).setToExpand(model.getNote());
//                                    ((MainActivityDrawer)getActivity()).myOnResume(1);
//                                }else if(model.getHeader().equals(Config.BOOKING_STATUS[3])){
//                                    dialog(model.getHeader(),model.getNote());
//                                }else if(model.getHeader().equals(Config.BOOKING_STATUS[4])){
//                                    dialog(model.getHeader(),model.getNote());
                                    } else dialog(model.getHeader(), service);
                                }
                            }else
                                dialog(model.getHeader(),model.getType());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    protected RecyclerView.Adapter newAdapter() {

        String uid = User.getUid()==null?"":User.getUid();

        FirebaseRecyclerOptions<NotifHandler> options =
                new FirebaseRecyclerOptions.Builder<NotifHandler>()
                        .setQuery(FirebaseDatabase.getInstance()
                                        .getReferenceFromUrl(Config.FURL_USERS)
                                        .child(uid)
                                        .child("notification")
//                                        .limitToLast(50)
                                , NotifHandler.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<NotifHandler, NotifHolder>(options) {
            @Override
            public NotifHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_notification, parent, false);

                return new NotifHolder(view);
            }

            @Override
            protected void onBindViewHolder(final NotifHolder holder, final int position, final NotifHandler model) {
//                if(model.getType().equals("conversation")) {
//                    FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_APPROVED)
//                            .child(model.getHeader())
//                            .child("title")
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                    model.setHeader(dataSnapshot.getValue(String.class));
//                                    holder.bindTo(model);
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                }else
                    holder.bindTo(model);

//                holder.bindTo(model);

//                holder.head.setText(model.getHeader());
//                holder.time.setText(model.getTime());

//                if(String.valueOf(model.getType()).equals("approved")){
//                    holder.body.setText("pertanyaan anda telah disetujui");
//                    holder.bindTo(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
////                            Typeface normalTypeFace = Typeface.defaultFromStyle();
//
////                            holder.head.setTypeface(Typeface.DEFAULT);
////                            holder.body.setTypeface(Typeface.DEFAULT);
//
//
////                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
////                                    .child(User.getUid())
////                                    .child("notification")
////                                    .child(model.getKey())
////                                    .child("read").setValue("true");
//
//                            startActivity(new Intent(getActivity(),Conversation.class)
//                                    .putExtra("topicKey",model.getNote()));
//
////                            attachRecyclerViewAdapter();
//                            v.setVisibility(View.GONE);
//
////                            v.startAnimation(zoom);
//                        }
//                    });
//                }else if(String.valueOf(model.getType()).equals("denied")){
//                    holder.body.setText("pertanyaan anda ditolak");
//                    holder.bindTo(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
////                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
////                                    .child(User.getUid())
////                                    .child("notification")
////                                    .child(model.getKey())
////                                    .child("read").setValue("true");
//
//                            dialog("denied");
//
////                            attachRecyclerViewAdapter();
//
//                            v.setVisibility(View.GONE);
////                            v.startAnimation(zoom);
//                        }
//                    });
//                }else if(String.valueOf(model.getType()).equals("booking")){

//                    holder.head.setText("ANTRIAN");
//                    holder.body.setText(model.getNote());
//                    holder.root.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(final View v) {
//
//                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                                .child(User.getUid())
//                                .child("notification")
//                                .child(model.getKey())
////                                    .updateChildren();
//                                .child("read").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if(dataSnapshot.getValue().toString().equals("false")){
//                                        dataSnapshot.getRef().setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                            }
//                                        });
//                                        v.setVisibility(View.GONE);
//                                    }
////                                    if(model.getHeader().equals("done"))
////                                        dialog(1);
////                                    else if(model.getHeader().equals("canceled"))
////                                        dialog(2);
////                                    else if(model.getHeader().equals("skipped"))
//                                        dialog(model.getHeader());
////
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
////                            attachRecyclerViewAdapter();
//
//
//                        }
//                    });

//                }

//                if(!String.valueOf(model.getRead()).equals("true")){
//                    Typeface boldTypeFace = Typeface.defaultFromStyle(Typeface.BOLD);
//
//                    holder.head.setTypeface(boldTypeFace);
//                    holder.body.setTypeface(boldTypeFace);
//                }else{
////                    Typeface normalTypeFace = Typeface.defaultFromStyle(Typeface.NORMAL);
//
//                    holder.head.setTypeface(Typeface.DEFAULT);
//                    holder.body.setTypeface(Typeface.DEFAULT);
//                }

                holder.setIsRecyclable(true);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                notificationList.setVisibility(getItemCount() != 0 ? View.VISIBLE : View.INVISIBLE);

                if(getItemCount()==0) {
                    notificationList.removeAllViews();
                    notificationList.setVisibility(View.INVISIBLE);
                    emptyNotification.setVisibility(View.VISIBLE);
                }else{
                    emptyNotification.setVisibility(View.INVISIBLE);
                    notificationList.setVisibility(View.VISIBLE);
                }
                pDialog.dismiss();

            }
        };
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

        notificationList.setAdapter(adapter);

    }

    private void dialog(String s, final String service) {

        String msg = "";

//        if (s.equals("dissmised")) msg = "booking anda dibatalkan karena anda tidak datang ke rumah sakit sampai waktu praktek selesai";
//        else if(s.equals("done")) msg = "terima kasih anda telah menggunakan fitur booking\nsemoga anda diberi kesehatan";
//        else if(s.equals("canceled")) msg = "antrian anda dibatalkan, karena anda belum hadir saat giliran anda tiba\n\n" +
//                "silahkan memesan antrian kembali";
//        else if(s.equals("skipped")) msg = "giliran anda dilewati karena anda belum hadir/mengisi data pasien di resepsionis\n\n" +
//                "mohon segera melengkapi data pasien di resepsionis";
//        else if(s.equals("cancel")) msg = "anda membatalkan antrian";
//        else if(s.equals("expired")) msg = "antrian anda kadaluarsa ketika, tanggal sekarang berbeda dengan tanggal booking antrian anda";
//        else if(s.equals("processing")) msg = "anda sedang memesan antrian.\n\n" +
//                "mohon konfirmasi pesanan anda di resepsionis sebelum giliran ada tiba\n\n" +
//                "jika antrian tidak dikonfirmasi saat giliran tiba, antrian anda akan dilewati, dan akan hangus setelah dilewati dua kali";
//        else
//            if (service.equals("denied"))msg = "pertanyaan anda tidak disetujui oleh admin untuk ditampilkan, mungkin dikarenakan konten yang tidak sesuai atau alasan lainnya.\nsilahkan mencoba membuat pertanyaan yang lain";
//            else msg = "terima kasih telah menggunakan layanan kami";

        AlertDialog.Builder pdialog = new AlertDialog.Builder(getActivity())
                .setTitle(null)
                .setCancelable(false)
                .setNegativeButton("tutup", null);

        if(s.equals(Config.BOOKING_STATUS[3])){
            msg = "terima kasih telah menggunakan layanan kami, semoga anda selalu diberi kesehatan";
        }else if(s.equals(Config.BOOKING_STATUS[4])){
            msg = "mohon segera mengkonfirmasi booking anda di resepsionis sebelum waktu praktek tutup";
            pdialog.setPositiveButton("booking", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPrefManager.getInstance(getActivity()).setToExpand(service);
                    ((MainActivityDrawer)getActivity()).myOnResume(1);
                }
            });
        }else if(s.equals(Config.BOOKING_STATUS[5])){
            msg = "booking anda hangus karena anda tidak melakukan konfirmasi sampai waktu praktek tutup";
        }else if(s.equals("over")){
            msg = "sesi pemberitahuan ini telah berakhir/kadaluarsa";
        }else if(s.equals("converstion")){
            msg = "percakapan yang anda tuju sudah kadaluarsa/dihapus";
        }else if(s.equals("deleted")){
            msg = "sesi pemberitahuan ini telah kadaluarsa, sepertinya pasien membatalkan booking ini";
        }else if (service.equals("denied")){
            msg = "pertanyaan anda tidak disetujui oleh admin untuk ditampilkan, mungkin dikarenakan konten yang tidak sesuai atau alasan lainnya.\nsilahkan mencoba membuat pertanyaan yang lain";
        }


        pdialog.setMessage(msg);
        pdialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        Toast.makeText(getActivity(), id + "All bersihkan!", Toast.LENGTH_LONG).show();

        if (id == R.id.action_mark_all_read) {
//            Toast.makeText(getActivity(), "All read", Toast.LENGTH_LONG).show();
//            Toast.makeText(getActivity(), id + "All bersihkan!", Toast.LENGTH_LONG).show();
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                    .child(User.getUid())
                    .child("notification")
                    .orderByChild("read").equalTo(false)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null){

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    data.getRef().child("read").setValue(true);
                                }
                                attachRecyclerViewAdapter();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//            refreshNotification();
//            attachRecyclerViewAdapter();
        }

        // user is in action_bar_notification fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getActivity(), id + "All delete!", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Clear all action_bar_notification!", Toast.LENGTH_LONG).show();

            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                    .child(User.getUid())
                    .child("notification")
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            refreshNotification();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "terjadi kesalahan dalam proses "+e, Toast.LENGTH_LONG).show();
                        }
                    });

        }

        item.setChecked(false);
        return super.onOptionsItemSelected(item);
    }
}