
package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.ConvHandler;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;
import com.rspkumobile.util.NotificationUtils;
import com.rspkumobile.util.Remainder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Conversation extends AppCompatActivity {

    private static final String TAG = Conversation.class.getSimpleName(),message="text",userUid="uidFrom";
    //    private FirebaseListAdapter<ConvHandler> adapter;
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private EditText inputan;
    private TextView quoteText,quotedFrom;
    private Map<String, String> quote;
    String topicKey, url, cUserToken;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public int listenerCheck;

    ImageView citationCloser;
    RelativeLayout quoteReview;
    DatabaseReference dbRef;
    RecyclerView listOfMessages;

//    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar

    TextView emptyConv;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private boolean listening=false;
    private ProgressDialog pDialog;
    private ConvHandler payload;
    private boolean reqExist;
    private String value;
    private DatabaseReference reqRef;
    private String uid;

    @Override
    protected void onStart() {
        super.onStart();
        setLayoutTitle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reqExist = false;

        listOfMessages =(RecyclerView)findViewById(R.id.list_of_messages);
        listOfMessages.setNestedScrollingEnabled(false);
        listOfMessages.setLayoutManager(new LinearLayoutManager(this));

        quoteText=(TextView)findViewById(R.id.citation_text);
        quotedFrom=(TextView)findViewById(R.id.citation_from);
        inputan = (EditText)findViewById(R.id.input);
        quoteReview=(RelativeLayout)findViewById(R.id.review_citation);

//        listOfMessages.addItemDecoration(new DividerItemDecoration(Conversation.this, R.drawable.divider));

        topicKey=getIntent().getExtras().getString("topicKey");
        url= Config.FURL_CONSULTATION+"topic/approved/"+topicKey;
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(url);

        emptyConv=(TextView)findViewById(R.id.emptyTextView);



        inputSetting();

//        if(User.getUid().equals(null)){
//            inputan.setFocusableInTouchMode(false);
//        }else inputan.setFocusableInTouchMode(true);

//        inputan.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(User.getUid().equals(null)){
//                    requireLogin();
//                }
//                else if(!inputan.isFocusableInTouchMode()){
//                    if (event.getAction()==MotionEvent.ACTION_UP){
//                        showAlertReqExist();
////                    }
//                }
//                return false;
//            }
//        });
        inputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.getUid().equals(null)){
                    Helper.requireLogin(Conversation.this);
                }else if(!inputan.isFocusableInTouchMode()){
                    showAlertReqExist();
                }
            }
        });

//        //scrolldown worked
////        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
////        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//
////        Toast.makeText(this,"isi"+FirebaseDatabase.getInstance().getReferenceFromUrl("https://tugas-akhir-msr.firebaseio.com/topic.json"),Toast.LENGTH_SHORT).show();
////
////        if(FirebaseDatabase.getInstance().getReferenceFromUrl("https://tugas-akhir-msr.firebaseio.com/topic.json")!=null) {
////            FirebaseDatabase.getInstance()
////                    .getReferenceFromUrl("https://tugas-akhir-msr.firebaseio.com/topic/")
////                    .push()
////                    .setValue("topic satu");$♠
////        }
//
//
//


        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity

//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setLogo(R.drawable.logo192x192)
//                            .setAvailableProviders(
//                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
//                            .build(),
//                    SIGN_IN_REQUEST_CODE
//            );

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast

//            Toast.makeText(this,FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).toString(),Toast.LENGTH_LONG).show();
//            Toast.makeText(this,
//                    "Welcome " + FirebaseAuth.getInstance().getCurrentUser()
//                            .getDisplayName(),
//                    Toast.LENGTH_LONG)
//                    .show();

            // Load chat room contents
//           h▬$ displayConvHandlers();

        }
        //button to cancel citation
        citationCloser=(ImageView) findViewById(R.id.citation_close);
        citationCloser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quoteText.setText("");
                quotedFrom.setText("");
                quoteReview.setVisibility(View.GONE);
            }
        });

        final FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(User.isLogedIn()) {
                if(inputan.getText().length()>0) {
                    if(User.isDoctor(Conversation.this)){
                        //TODO send direct msg
                        sendMessage();
                    }
                    else {

                        // Read the inputan field and push a new instance
                        // of ConvHandler to the Firebase database
//                        if (inputan.getText().toString().trim().length() != 0) {

//                        sendMessage();
                            sendRequest();
//                        inputSetting();
//                        String response= sendMessage();
//                        Toast.makeText(Conversation.this, "response"+response, Toast.LENGTH_SHORT).show();
//
//
//
//                        Log.i("response1",response);
//                        try {
//                            JSONObject obj=new JSONObject(response);
//                            Log.d("response",obj.getString("message"));
//                            Toast.makeText(Conversation.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                            // make the sender to listen to update in this topic
                            followConversatoin(true);

                            // update token for current user detail each time sending message
//                        updateUserDetail();
                            inputan.setFocusableInTouchMode(false);


//                        inputSetting();
                            inputan.clearFocus();
//                        }
                    }
                    Helper.hideKeyboard(Conversation.this);
//                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.hideSoftInputFromWindow(inputan.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }else{
                    if(User.getUid()==null) Helper.requireLogin(Conversation.this);
//                    else showAlertReqExist();
                }
//                else requireLogin();

            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide action_bar_notification
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(topicKey)) {
                    // new push notification is received

                    String message = intent.getStringExtra("topicKey");
//                    txtMessage.setText(message);
                    // clear the notification area when the app is opened

                }


            }
        };


        attachRecyclerViewAdapter();
    }

    private void setLayoutTitle(){
        dbRef.child("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(Conversation.this, String.valueOf(dataSnapshot.getValue()), Toast.LENGTH_SHORT).show();
                        setTitle(String.valueOf(dataSnapshot.getValue()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        inputSetting();

//        mAuth.addAuthStateListener(mAuthListener);

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));


        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(topicKey));

        NotificationUtils.clearNotifications(getApplicationContext(),Config.NOTIFICATION_ID_CONSULTATION);


    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(mAuthListener!=null){
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }

    public class ConvHolder extends RecyclerView.ViewHolder{
        RelativeLayout balloon;
        TextView messageText;
        TextView userDisplayName;
        TextView messageDate;
        TextView senderUid;
//        TextView senderUid;
        TextView quote;
        TextView quoteFrom;
        public ConvHolder(View v){
            super(v);
            balloon = (RelativeLayout) v.findViewById(R.id.balloon);
            messageText = (TextView) v.findViewById(R.id.message_text);
            userDisplayName = (TextView) v.findViewById(R.id.message_display_name);
            messageDate = (TextView) v.findViewById(R.id.message_time);
            senderUid = (TextView) v.findViewById(R.id.message_uid);
//            senderToken=(TextView)v.findViewById(R.id.user_token);
            quote=(TextView)v.findViewById(R.id.message_citation_text);
            quoteFrom=(TextView)v.findViewById(R.id.message_citation_from);
        }
        public void setAll(String quoteText, String quoteUid, String message,
                           final String messageUid,
                           final String sendersDisplayName,
//                           String userToken,
                           long messageTime){
            //set margin between displayname and citation AND message and citation
            LinearLayout.LayoutParams quoteMargin = (LinearLayout.LayoutParams) quote.getLayoutParams();
            if(!TextUtils.isEmpty(quoteText)) {
                quote.setBackgroundResource(R.drawable.balloon_quote);
                quote.setText(quoteText);
                quote.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                quote.setVisibility(View.VISIBLE);

                quoteMargin.setMargins(1, 20, 1, 20);
            }else{
                quoteMargin.setMargins(0,0,0,5);
            }
            quote.setLayoutParams(quoteMargin);
            quoteFrom.setText(quoteUid);
            messageText.setText(message);

            if(messageUid.equals(User.getUid())){
                userDisplayName.setText("SAYA");
//                if(User.isDoctor(Conversation.this))
//                    balloon.setBackgroundResource(R.drawable.balloon_docter);
//                else
                    balloon.setBackgroundResource(R.drawable.balloon_current_user);
            }else {
                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                        .child(messageUid)
                        .child("userDetail")
                        .child("displayName")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.getValue()==null)userDisplayName.setText(sendersDisplayName);
                                if (dataSnapshot.getValue() == null){
                                    userDisplayName.setText(sendersDisplayName);
                                }
                                else {
//                                if(User.isLogedIn()){
//                                    if (User.getUid().equals(messageUid)) {
//                                        userDisplayName.setText("SAYA");
//                                        balloon.setBackgroundResource(R.drawable.balloon_current_user);
////                    balloon.setGravity(RelativeLayout.ALIGN_END);
////                                    } else if(auth){
////                                        userDisplayName.setText(dataSnapshot.getValue(String.class));
////                                        balloon.setBackgroundResource(R.drawable.balloon_docter);
//                                    } else
                                        userDisplayName.setText(dataSnapshot.getValue(String.class));
//                                } else userDisplayName.setText(dataSnapshot.getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                if(messageUid.equals(User.getUid())){
                    balloon.setBackgroundResource(R.drawable.balloon_current_user);
                }else {

                    FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
                            .orderByChild("uid").equalTo(messageUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) ;
//                                balloon.setBackgroundResource(R.drawable.balloon_current_user);
                                    else {
                                        balloon.setBackgroundResource(R.drawable.balloon_docter);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
            }


//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
//                    .orderByChild("uid").equalTo(messageUid)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.getValue()==null);
//                            else{
//                                balloon.setBackgroundResource(R.drawable.balloon_docter);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

//            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageUid)) {
//                    userDisplayName.setText("SAYA");
//                    balloon.setBackgroundResource(R.drawable.balloon_current_user);
////                    balloon.setGravity(RelativeLayout.ALIGN_END);
//                } else if(auth){
//                    userDisplayName.setText(sendersDisplayName);
//                    balloon.setBackgroundResource(R.drawable.balloon_docter);
//                }else userDisplayName.setText(sendersDisplayName);
//            } else userDisplayName.setText(sendersDisplayName);

            // Format the date before showing it
//            senderUid.setText(userToken);
            senderUid.setText(messageUid);
            messageDate.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    messageTime));
        }
        public String getQuote(){
            return quote.getText().toString();
        }
        public String getQuoteFrom(){
            return quoteFrom.getText().toString();
        }
    }



    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
//                inputSetting();
                listOfMessages.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
//                inputSetting();
                Log.e(TAG,"recycle view"+positionStart+itemCount);
            }

            @Override
            public void onChanged() {
//                inputSetting();
                super.onChanged();
            }
        });

        listOfMessages.setAdapter(adapter);
    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<ConvHandler> options =
                new FirebaseRecyclerOptions.Builder<ConvHandler>()
                        .setQuery(FirebaseDatabase.getInstance()
                                .getReferenceFromUrl(url)
                                .child("conversation")
                                .limitToLast(50)
                                ,ConvHandler.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<ConvHandler, ConvHolder>(options) {
            @Override
            public ConvHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_message, parent, false);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        TextView message=(TextView)view.findViewById(R.id.message_text);
                        TextView token=(TextView)view.findViewById(R.id.message_uid);

                        quoteReview.setVisibility(View.VISIBLE);
                        quoteText.setText(message.getText());
                        quotedFrom.setText(token.getText());
                        return false;
                    }
                });
                return new ConvHolder(view);
            }

            @Override
            protected void onBindViewHolder(ConvHolder holder, int position, ConvHandler model) {
                holder.setAll(model.getQuote().get(message),
                        model.getQuote().get(userUid),
                        model.getMessageText(),
                        model.getSenderUid(),
                        model.getSenderDisplayName(),
//                        model.getSenderToken(),
                        model.getMessageTime());

                holder.setIsRecyclable(false);
            }

            @Override
            public void onDataChanged() {
//                pDialog = new ProgressDialog(Conversation.this);
//                pDialog.setMessage("Sedang Mengirim...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                if(getItemCount() == 0) pDialog.show();
//                else pDialog.hide();

                // If there are no chat messages, show a view that invites the user to add a message.
                emptyConv.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void displayConvHandlers() {
//        RecyclerView listOfMessages =(RecyclerView)findViewById(R.id.list_of_messages);
//        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
//        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        adapter = new FirebaseListAdapter<ConvHandler>(this, ConvHandler.class,
//                R.layout.message, FirebaseDatabase.getInstance()
//                .getReferenceFromUrl("https://tugas-akhir-msr.firebaseio.com/topic/kanker")
//                .child("conversation")
//                .orderByChild("messageApproval")
//                .equalTo(true)
// //               .getReference()

//        FirebaseRecyclerOptions<ConvHandler> options=new FirebaseRecyclerOptions.Builder<ConvHandler>()
//                .setQuery(FirebaseDatabase.getInstance()
//                        .getReferenceFromUrl(url)
//                        .child("conversation")
////                        .orderByChild("messageApproval")
////                        .equalTo(true)
//                        .limitToLast(50)
//                        ,ConvHandler.class)
//                .build();
//
//
//
//        FirebaseRecyclerAdapter<ConvHandler,ConvHolder> adapter1=new FirebaseRecyclerAdapter<ConvHandler,ConvHolder>
//                (options) {
//            @Override
//            public ConvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.message, parent, false);
//                view.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        TextView message=(TextView)view.findViewById(R.id.message_text);
//                        TextView token=(TextView)view.findViewById(R.id.user_token);
//
//                        quoteReview.setVisibility(View.VISIBLE);
//                        quoteText.setText(message.getText());
//                        quotedToken.setText(token.getText());
//                        return false;
//                    }
//                });
//                return new ConvHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(ConvHolder holder, int position, ConvHandler model) {
//                holder.setAll(model.getQuote().get(message),
//                        model.getQuote().get(userToken),
//                        model.getMessageText(),
//                        model.getSenderUid(),
//                        model.getSenderDisplayName(),
//                        model.getSenderToken(),
//                        model.getMessageTime());
//            }
//        };
//
//        listOfMessages.setAdapter(adapter1);

//        adapter = new FirebaseListAdapter<ConvHandler>(new FirebaseListOptions.Builder<ConvHandler>()
//                .setQuery(FirebaseDatabase.getInstance()
//                                .getReferenceFromUrl("https://tugas-akhir-msr.firebaseio.com/topic/kanker")
//                                .child("conversation")
//                                .orderByChild("messageApproval")
//                                .equalTo(true)
//                                .limitToLast(50)
//                        ,ConvHandler.class)
//                .build()
//        )
//        {
//            @Override
//            protected void populateView(View v, ConvHandler model, int position) {
//                // Get references to the views of message.xml
//                RelativeLayout balloon = (RelativeLayout) v.findViewById(R.id.balloon);
//                TextView messageText = (TextView) v.findViewById(R.id.message_text);
//                TextView messageDisplayName = (TextView) v.findViewById(R.id.message_display_name);
//                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
//                TextView messageUser = (TextView) v.findViewById(R.id.message_uid);
//                TextView messageToken=(TextView)v.findViewById(R.id.user_token);
//                TextView messageCitation=(TextView)v.findViewById(R.id.message_citation_text);
//                TextView messageCitationFrom=(TextView)v.findViewById(R.id.message_citation_from);
//
//
//                //set margin between displayname and citation AND message and citation
//                LinearLayout.LayoutParams citationMargin = (LinearLayout.LayoutParams) messageCitation.getLayoutParams();
//                if(!TextUtils.isEmpty(model.getQuote().get(message))) {
//                    messageCitation.setBackgroundResource(R.drawable.balloon_quote);
//                    messageCitation.setText(model.getQuote().get(message));
//                    messageCitation.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    messageCitation.setVisibility(View.VISIBLE);
//
////                android:layout_marginTop="10dp"
////                android:layout_marginBottom="10dp"
////                android:layout_marginLeft="5dp"
////                android:layout_marginRight=
//                    citationMargin.setMargins(1, 20, 1, 20);
//
//                }else{
//                    citationMargin.setMargins(0,0,0,5);
//                }
//                messageCitation.setLayoutParams(citationMargin);
//
//
//                messageCitationFrom.setText(model.getQuote().get(userToken));
//                messageText.setText(model.getMessageText());
//
//                if (FirebaseAuth.getInstance().getCurrentUser()
//                        .getCurrentUser().getUid().equals(model.getMessageUid())) {
//                    messageDisplayName.setText("SAYA");
//                    balloon.setBackgroundResource(R.drawable.balloon_current_user);
//                    balloon.setGravity(RelativeLayout.ALIGN_END);
//                } else {
//                    messageDisplayName.setText(model.getMessageDisplayName());
//                    balloon.setBackgroundResource(R.drawable.balloon_other_user);
//                }
//
//                // Format the date before showing it
//                messageToken.setText(model.getToken());
//                messageUser.setText(model.getMessageUid());
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                        model.getMessageTime()));
//            }
//
//        };
//        listOfMessages.setAdapter(adapter);
//
//        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView message=(TextView)view.findViewById(R.id.message_text);
//                TextView token=(TextView)view.findViewById(R.id.user_token);
//                quoteReview.setVisibility(View.VISIBLE);
//
//                quote.setText(message.getText());
//                quotedToken.setText(token.getText());
////
////                quote.setVisibility(View.VISIBLE);
////                quote.setBackgroundResource(R.drawable.balloon_quote_pop_up.xml);
//
////                citationCloser.setVisibility(View.VISIBLE);
//
//                return true;
//            }
//        });
//
//        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                UserDetails.chatWith = al.get(position);
////                startActivity(new Intent(getActivity(), Chat.class));
////                startActivity(new Intent(getActivity(), ChatRoom.class).putExtra("topic",al.get(position)));
////                Intent i=new Intent(this, ChatRoom.class);
////                i.putExtra("topic",topic.get(position));
////                startActivity(i);
////                TextView message=(TextView)view.findViewById(R.id.message_text);
////                inputan.setText(message.getText());
//
////                TextView message=(TextView)view.findViewById(R.id.message_text);
////                TextView token=(TextView)view.findViewById(R.id.user_token);
////
////                quote.setText(message.getText());
////                quotedToken.setText(token.getText());
////
////                quote.setVisibility(View.VISIBLE);
//
//            }
//        });
//
//        //scrolldown worked
////        adapter.notifyDataSetChanged();
//
////        listOfMessages.setSelection(adapter.getCount()-1);
////        listOfMessages.fullScroll(View.FOCUS_DOWN);
    }

//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.menu_conversation,menu);
//        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.menu_conversation, menu);
//        }else getMenuInflater().inflate(R.menu.menu_conversation_signed_in, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem follow = menu.findItem(R.id.follow_conv);
        final MenuItem signInOut = menu.findItem(R.id.sign_in_out);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            follow.setVisible(true);

            signInOut.setTitle("Keluar");

            dbRef.child("listener").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                follow.setTitle("Follow");
                            } else follow.setTitle("Unfollow");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }else{
            signInOut.setTitle("Masuk");
            follow.setVisible(false);
//            startActivity(new Intent(Conversation.this, SignInActivity.class));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            // finish the activity
            Helper.hideKeyboard(Conversation.this);
            onBackPressed();
            return true;
        }else if(item.getItemId() == R.id.sign_in_out) {

            if(item.getTitle().equals("Masuk")) {
//                startActivity(new Intent(Conversation.this, SignInActivity.class));
                signIn();
//                item.setTitle("Keluar");
            }else {
                Helper.signOut(Conversation.this);
//                pDialog = new ProgressDialog(Conversation.this);
//                pDialog.setMessage("mohon tunggu...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                final Context ctx = Conversation.this;
//
//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
//                        .child(User.getUid())
//                        .child("booking")
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(final DataSnapshot dataSnapshot) {
//
////                                if(dataSnapshot.getValue()!=null){
//
//                                //sign out function
////                                Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                AuthUI.getInstance().signOut(Conversation.this)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
////                                                myOnResume(0);
//
//                                                SharedPrefManager.getInstance(Conversation.this).doctorClear();
//
//                                                pDialog.hide();
//
//                                                getIntent().removeExtra(Config.ON_SIGNIN_SUCCESS);
//
//                                                Toast.makeText(Conversation.this,
//                                                        R.string.sign_out_done,
//                                                        Toast.LENGTH_LONG)
//                                                        .show();
//
//                                                User.deleteUserToken(User.getUid());
//
//                                                if(dataSnapshot.getValue()!=null) {
//
//                                                    //cancle alarm
//                                                    for (DataSnapshot children : dataSnapshot.getChildren()) {
//                                                        Remainder.setAlarmCancel(ctx,children.child("date").getValue(String.class), children.child("time").getValue(String.class));
////                                                            setAlarmCancel(children.child("date").getValue(String.class), children.child("time").getValue(String.class));
//                                                    }
//                                                }
//
//
//
//                                                recreate();
//
//                                            }
//
//                                        });
////                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

//                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                AuthUI.getInstance().signOut(this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(Conversation.this,
//                                        R.string.sign_out_done,
//                                        Toast.LENGTH_LONG)
//                                        .show();
////                                Config.deleteUserToken(uid);
//                                User.deleteUserToken(uid);
//                                recreate();
//
//                            }
//                        });

//                item.setTitle("Masuk");
            }
        }else if(item.getItemId() == R.id.follow_conv){
            if(item.getTitle().equals("Follow")){
                followConversatoin(true);
                item.setTitle("Unfollow");
            }else{
                followConversatoin(false);
                item.setTitle("Follow");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertReqExist(){
        Log.e(TAG, "topic :" + topicKey);
        Log.e(TAG, "reqRef :" + reqRef);
        new AlertDialog.Builder(Conversation.this)
                .setTitle("Perhatian")
                .setMessage("anda sudah mengajukan pernyataan dalam percakapan ini\n"
                        + "silahkan tunggu admin menyutujui pernyataan anda untuk ditampilkan, "
                        + "ataukah anda ingin menyunting pernyattan anda yang sebelumnya"
                )
                .setPositiveButton("sunting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Conversation.this, ShowConvEdit.class)
                                .putExtra("topic",topicKey)
                                .putExtra("reqRef", String.valueOf(reqRef)));

//                        finish();

                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    private void inputSetting(){
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_REQ)
////                .child("users")
//                .orderByChild("senderUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        Log.d(TAG, "dot-"+String.valueOf(dataSnapshot.getValue()));
//                        Log.e(TAG, "dot-"+String.valueOf(dataSnapshot.getValue()));
//
//                        if (dataSnapshot.getValue() == null) {
//
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
        if (User.isLogedIn()) {

            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_REQ)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.child("senderUid").getValue(String.class).equals(User.getUid())&&
                                    topicKey.equals(dataSnapshot.child("topicRef").getValue(String.class))){
                                Toast.makeText(Conversation.this,s+" "+dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                                inputan.setFocusableInTouchMode(false);
                                reqRef=dataSnapshot.getRef();
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("senderUid").getValue(String.class).equals(User.getUid())&&
                                    topicKey.equals(dataSnapshot.child("topicRef").getValue(String.class))){
                                Toast.makeText(Conversation.this,dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                                inputan.setFocusableInTouchMode(true);
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

//            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_CONV_REQ)
//
//                    .orderByChild("senderUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addValueEventListener(new ValueEventListener() {
//
////                    .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            Log.e(TAG, "hasil :" + String.valueOf(dataSnapshot.getValue()));
//
//                            if (dataSnapshot.getValue() == null) {
//                            } else {
//
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    //                            Log.e(TAG, "data :"+data.toString());
//                                    //                            Log.e(TAG, "data :"+data.child("topic").getValue());
//                                    //                            Log.e(TAG, "data :"+data.child("topic").getValue()+"="+topicRef+","+(String.valueOf(data.child("topic").getValue()).equals(topicRef)));
//                                    if (String.valueOf(data.child("topicRef").getValue()).equals(topicKey)) {
////                                        Log.e(TAG, "data :" + data.toString());
////                                        Log.e(TAG, "data :" + data.child("topic").getValue());
////                                        Log.e(TAG, "data :" + data.child("topic").getValue() + "=" + topicRef + "," + (String.valueOf(data.child("topic").getValue()).equals(topicRef)));
//                                        reqExist = true;
////                                        Log.e(TAG, "reqExist :" + reqExist);
////                                        value = data.child("topic").getValue(String.class);
//                                        Log.e(TAG, "reqRef :" + data.getRef());
//                                        reqRef=data.getRef();
//                                        break;
//                                    }
//                                }
//
//                                if (!reqExist) inputan.setFocusableInTouchMode(true);
//                                else inputan.setFocusableInTouchMode(false);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
        }else inputan.setFocusableInTouchMode(false);
//        Log.e(TAG, "-data :"+reqExist);
//        Log.e(TAG, "value :"+value);
//        return reqExist;
    }

    private void sendRequest(){

        quote = new HashMap<String, String>();
        quote.put(message, quoteText.getText().toString());
        quote.put(userUid, quotedFrom.getText().toString());

        String displayName;
        displayName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

//        boolean auth = User.isAuth();
//        if(!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
//            auth = true;
//        }

        payload = new ConvHandler(inputan.getText().toString(),
                displayName,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),quote);

        Log.i("quote", String.valueOf(quote));
        Log.i("payload", String.valueOf(payload));

        Map req = new HashMap();
        req.put("topicTitle",getTitle());
        req.put("topicRef", topicKey);
        req.put("senderUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        req.put("payload", payload);

        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_CONV_REQ)
                .push()
                .setValue(req);

//        ref.child("topic").setValue(topicRef);
//        ref.child("userDetail").setValue(FirebaseAuth.getInstance().getCurrentUser());
//        ref.child("senderUid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.child("payload").setValue(payload);

//        key.setValue(payload);

        // Clear the inputan
        inputan.setText("");
        quoteText.setText("");
        quotedFrom.setText("");
        quoteReview.setVisibility(View.INVISIBLE);
        Log.i("topic",topicKey);

    }

    private void sendMessage(){
        TextView citationText=(TextView)findViewById(R.id.citation_text);
        TextView citationFrom=(TextView)findViewById(R.id.citation_from);

        quote = new HashMap<String, String>();
        quote.put(message, citationText.getText().toString());
        quote.put(userUid, citationFrom.getText().toString());

        String displayName;
//        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")){
//            displayName=String.valueOf(
//                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//            ).substring(0,
//                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().length()-3)+"XXX";
//        }else
        displayName=User.getDisplayName();

        payload = new ConvHandler(inputan.getText().toString(),
                displayName,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),quote);

        Log.i("listener check", displayName);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(url);
        ref.child("conversation").push()
//                .setValue(new ConvHandler(inputan.getText().toString(),
//                        displayName,
//                        FirebaseAuth.getInstance().getCurrentUser().getUid(),quote))
                .setValue(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.child("time").setValue(System.currentTimeMillis());
            }
        });

        // Clear the inputan
        inputan.setText("");
        quoteText.setText("");
        quotedFrom.setText("");
        quoteReview.setVisibility(View.INVISIBLE);

//                params.put("status", pemesan.getAntriStatus());
//                params.put("keterangan", pemesan.getAntriNo());

//        String response = reqH.sendPostRequest(Config.NOTIF,new ConvHandler(inputan.getText().toString(),
//                displayName,
//                FirebaseAuth.getInstance().getCurrentUser().getUid(),quote).sendNotif());

//        return reqH.sendPostRequest(Config.ANTRIAN,params);

        Log.i("topic",topicKey);
//        new pushNotif().execute();

    }

    // followingConversation
    private void followConversatoin(final Boolean follow){

        dbRef.child("listener").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(follow) {
                            if (dataSnapshot.getValue() == null) {
                                Map listener = new HashMap();
                                listener.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                dbRef.child("listener")
                                        .push()
                                        .setValue(listener);
                            }
//                            for(DataSnapshot data: dataSnapshot.getChildren()) {
//                                Log.d("follow1", data.toString());
//                                Log.d("follow3", data.getKey().toString());
//                                Log.d("follow2", data.getRef().toString());
//                                Log.d("follow4", data.child("listener").getRef().toString());
//                            }
//                            Log.d("follow11", dataSnapshot.toString());
//                            Log.d("follow31", dataSnapshot.getValue().toString());
//                            Log.d("follow21", dataSnapshot.child("listener").getKey().toString());
//                            Log.d("follow41", dataSnapshot.child("listener").child("Uid").getRef().toString());
                        }else if(!follow){
                            for(DataSnapshot data: dataSnapshot.getChildren()) {

                                Log.d("follow", data.getRef().toString());
                                FirebaseDatabase.getInstance().getReferenceFromUrl(
                                        data.getRef().toString()
                                ).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //update user detail
    private void updateUserDetail(){
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null) {
                            updateDetails(null);
                        }else{
                            for(DataSnapshot data: dataSnapshot.getChildren()){
//                                Log.d("data", data.toString());
//                                Log.d("data", data.getRef().toString());
                                updateDetails(data.getRef().toString());
                            }

                        }
//                                    Log.d("data", dataSnapshot.child("uid").getRef().toString());
//                                    Log.d("data", dataSnapshot.getChildren().toString());
//                                    Log.d("data", dataSnapshot.getValue().toString());
//                                    Log.d("data", dataSnapshot.toString());
//                                    Log.d("data", dataSnapshot.getRef().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void updateDetails(final String url){

        //get current user's token
        Map userDetail = new HashMap();
        userDetail.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDetail.put("token", FirebaseInstanceId.getInstance().getToken());
        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
            userDetail.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }else userDetail.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if(url==null) {
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                    .push()
                    .setValue(userDetail);
        }else {
            FirebaseDatabase.getInstance().getReferenceFromUrl(url).setValue(userDetail);
        }
//        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnSuccessListener(
//                new OnSuccessListener<GetTokenResult>() {
//                    @Override
//                    public void onSuccess(GetTokenResult getTokenResult) {
//
//                        Map userDetail = new HashMap();
//                        userDetail.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        userDetail.put("token", getTokenResult.getToken());
//
//                        Log.e("TAG", "Firebase USER TOKEN: "+getTokenResult.getToken());
//
//                        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")) {
//                            userDetail.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//                        }else userDetail.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//
//                        if(url==null) {
//                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL)
//                                    .child("users")
//                                    .push()
//                                    .setValue(userDetail);
//                        }else {
//                            FirebaseDatabase.getInstance().getReferenceFromUrl(url).setValue(userDetail);
//                        }
//
//                        cUserToken=getTokenResult.getToken();
//                        Toast.makeText(Conversation.this,"usertoken: "+cUserToken,Toast.LENGTH_LONG).show();
//
//                    }
//                }
//        );

//        return cUserToken;
    }

    private void requireLogin(){
        new AlertDialog.Builder(Conversation.this)
                .setTitle("Perhatian")
                .setMessage("anda harus Log In untuk menggunakan fasilitas ini"
                        + "\nLog In sekarang?"
                        )
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signIn();
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

//    public class pushNotif extends AsyncTask<Void,String,String> {
//        protected void onPreExecute(){
//            super.onPreExecute();
//            pDialog = new ProgressDialog(Conversation.this);
//            pDialog.setMessage("Sedang Mengirim...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
////            pDialog.show();
//        }
//        @Override
//        protected String doInBackground(Void... voids) {
//            RequestHandler reqH=new RequestHandler();
//
//            HashMap<String,String>params=new HashMap<>();
//            params.put("payload", String.valueOf(payload.sendNotif()));
//            params.put("topic",topicKey);
//            params.put("type","conversation");
//
////            params.put("antriTanggal", pemesan.getAntriTanggal());
////            params.put("antriJam", pemesan.getAntriPukul());
//
//            Log.d("params", String.valueOf(params));
//
//            return reqH.sendPostRequest(Config.FIREBASE_MESSAGE,params);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
////            pDialog.dismiss();
//            Log.d("payload", String.valueOf(payload.sendNotif()));
//            Log.d("response",s);
////            Toast.makeText(Conversation.this, s, Toast.LENGTH_SHORT).show();
//
////            SharedPrefManager.getInstance(getApplicationContext()).hapus();
//
//        }
//    }

    private void signIn(){
        Helper.signIn(Conversation.this);
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setLogo(R.drawable.logo192x192)
//                        .setAvailableProviders(
//                                Arrays.asList(
//                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
//                                        ,new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//                                ))
//                        .build(),
//                SIGN_IN_REQUEST_CODE
//        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Helper.onActivityResult(Conversation.this,requestCode,resultCode,data);

//        if(requestCode == SIGN_IN_REQUEST_CODE) {
//
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if(resultCode == RESULT_OK) {
//                SharedPrefManager.getInstance(Conversation.this).turnNotificationOn(true);
//                SharedPrefManager.getInstance(Conversation.this).turnSoundOn(true);
//
////                Toast.makeText(this,
////                        response.getProviderType(),
////                        Toast.LENGTH_LONG)
////                        .show();
//
//                Log.e("Sing IN", "Firebase reg id: " + FirebaseInstanceId.getInstance().getToken());
////                startActivity(new Intent(this, MainActivityDrawer.class)
////                        .putExtra(Config.ON_SIGNIN_SUCCESS, FirebaseInstanceId.getInstance().getToken()));
//
////                ((MainActivityDrawer)getApplicationContext()).myOnResume();
////                setTitle(R.string.layout_sign_in_display_name);
//                User.updateCurrentUserDetail();
//
////                if(response.getProviderType().equals("phone")) {
////                    Toast.makeText(this,
////                            response.getProviderType(),
////                            Toast.LENGTH_LONG)
////                            .show();
//////                    finish();
////                    startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//////                    startActivity(new Intent(MainActivityDrawer.this,AboutUsActivity.class));
////                }
//
//                //is doctor
//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
//                        .child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.getValue()==null) {
//                            recreate();
//                            startActivity(new Intent(Conversation.this,SignInActivity.class));
//                        }
//                        else {
//
//                            String name = dataSnapshot.child("name").getValue(String.class);
//                            String nip = dataSnapshot.child("nip").getValue(String.class);
//
//                            Log.e("login doctor",dataSnapshot.getValue().toString());
//                            Log.e("login doctor",name + nip);
//
//                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(name)
//                                    .build();
//
//                            SharedPrefManager.getInstance(Conversation.this).setAsDoctor(nip);
//
//                            User.setDisplayName(profileUpdate);
//
//                            User.updateCurrentUserDetail();
//
//                            Toast.makeText(Conversation.this,"selamat datang Dr. "+name,Toast.LENGTH_LONG).show();
//
//                            recreate();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
////                return;
//
//            } else {
//                // Close the app
////                finish();
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Toast.makeText(this,
//                            R.string.login_canceled,
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//
//                }
//
//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(this,
//                            "no network",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    Toast.makeText(this,
//                            "error",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//            }
////            Toast.makeText(this,
////                    "error",
////                    Toast.LENGTH_LONG)
////                    .show();
//////            finish();
////            return;
//        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == SIGN_IN_REQUEST_CODE) {
//
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if(resultCode == RESULT_OK) {
//
////                Toast.makeText(this,
////                        response.getProviderType(),
////                        Toast.LENGTH_LONG)
////                        .show();
//
//                Log.e("Sing IN", "Firebase reg id: " + FirebaseInstanceId.getInstance().getToken());
////                startActivity(new Intent(this, MainActivityDrawer.class)
////                        .putExtra(Config.ON_SIGNIN_SUCCESS, FirebaseInstanceId.getInstance().getToken()));
//
////                ((MainActivityDrawer)getApplicationContext()).myOnResume();
////                setTitle(R.string.layout_sign_in_display_name);
//                User.updateCurrentUserDetail();
//
////                if(response.getProviderType().equals("phone")) {
////                    Toast.makeText(this,
////                            response.getProviderType(),
////                            Toast.LENGTH_LONG)
////                            .show();
//////                    finish();
////                    startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//////                    startActivity(new Intent(MainActivityDrawer.this,AboutUsActivity.class));
////                }
//
//                //is doctor
//                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
//                        .child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.getValue()==null) {
//                            recreate();
//                            startActivity(new Intent(MainActivityDrawer.this,SignInActivity.class));
//                        }
//                        else {
//
//                            String name = dataSnapshot.child("name").getValue(String.class);
//                            String nip = dataSnapshot.child("nip").getValue(String.class);
//
//                            Log.e("login doctor",dataSnapshot.getValue().toString());
//                            Log.e("login doctor",name + nip);
//
//                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(name)
//                                    .build();
//
//                            SharedPrefManager.getInstance(MainActivityDrawer.this).setAsDoctor(nip);
//
//                            User.setDisplayName(profileUpdate);
//
//                            User.updateCurrentUserDetail();
//
//                            Toast.makeText(MainActivityDrawer.this,"selamat datang Dr. "+name,Toast.LENGTH_LONG).show();
//
//                            recreate();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
////                return;
//
//            } else {
//                // Close the app
////                finish();
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Toast.makeText(this,
//                            R.string.login_canceled,
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//
//                }
//
//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(this,
//                            "no network",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    Toast.makeText(this,
//                            "error",
//                            Toast.LENGTH_LONG)
//                            .show();
////                    finish();
//                    return;
//                }
//            }
////            Toast.makeText(this,
////                    "error",
////                    Toast.LENGTH_LONG)
////                    .show();
//////            finish();
////            return;
//        }
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == SIGN_IN_REQUEST_CODE) {
//
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if(resultCode == RESULT_OK) {
//
////                Toast.makeText(this,FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).toString(),Toast.LENGTH_LONG).show();
//
//                Toast.makeText(this,
//                        "Successfully signed in. Welcome!",
//                        Toast.LENGTH_LONG)
//                        .show();
//
//
//
////                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
////                FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
////                displayConvHandlers();
//
////                updateUserDetail();
//                recreate();
//                return;
//
//            } else {
//                // Close the app
////                finish();
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Toast.makeText(this,
//                            "cancle",
//                            Toast.LENGTH_LONG)
//                            .show();
//                    return;
//
//                }
//
//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Toast.makeText(this,
//                            "no network",
//                            Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    Toast.makeText(this,
//                            "error",
//                            Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                }
//            }
//            Toast.makeText(this,
//                    "error",
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
//
//    }
}
