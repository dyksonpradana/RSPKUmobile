package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.ConvHandler;
import com.rspkumobile.other.User;


import java.util.HashMap;
import java.util.Map;

public class NewTopicReq extends AppCompatActivity {

    private String topicRef,reqRef;
    private static final String TAG = NewTopicReq.class.getSimpleName();
    EditText text, title;
    LinearLayout editLay,requestLay;
    Boolean reqExist;
    private boolean textChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic_req);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reqExist=false;
        textChange=false;



//        Log.e(TAG, "dot-"+topicRef+","+reqRef);

        text = (EditText) findViewById(R.id.text_val);
        title = (EditText) findViewById(R.id.title_val);
        editLay = (LinearLayout)findViewById(R.id.btn_edit);
        requestLay = (LinearLayout)findViewById(R.id.btn_request);

        checkRequest();

        Log.e(TAG, "reqExist "+String.valueOf(reqExist));
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

    private void sendBtn(){



        Button send =(Button)findViewById(R.id.btn_send);

        // check if there is no such extra return false

        Toast.makeText(NewTopicReq.this,"out",Toast.LENGTH_LONG).show();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

                    if (title.getText().toString().trim().length() != 0 && text.getText().toString().trim().length() != 0) {
                        text.clearFocus();
                        title.clearFocus();
                        showAlertReq();
                    }
                    hideKeyboard();

                }else redirectSignIn();
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(title.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        inputManager.hideSoftInputFromWindow(text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void editBtn(){

        Button del =(Button)findViewById(R.id.delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                editBtnAlert(100);
            }
        });

        Button save =(Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                text.addTextChangedListener(tW);
                title.addTextChangedListener(tW);

                if(textChange==true)editBtnAlert(101);
                else Toast.makeText(NewTopicReq.this,"tidak terdapat perubahan untuk disimpan",Toast.LENGTH_LONG).show();
            }
        });


    }

    // listener whether there is textchange or not
    private TextWatcher tW = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            textChange=true;
        }
    };

    private void editBtnAlert(final int i){
        Log.e(TAG, "topic :" + topicRef);
        Log.e(TAG, "reqRef :" + reqRef);
        String warningText = null;
        if (i==100){
            warningText = "anda akan menghapus permintaan anda";
        } else {
            warningText = "apakah anda ingin menyimpan perubahan?";
        }
        new AlertDialog.Builder(NewTopicReq.this)
                .setTitle("Perhatian")
                .setMessage(warningText)
                .setPositiveButton(i==100 ? "hapus":"simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(i==100) FirebaseDatabase.getInstance().getReferenceFromUrl(reqRef).removeValue();
                        else FirebaseDatabase.getInstance().getReferenceFromUrl(reqRef+"/payload/messageText").setValue(text.getText().toString());
                        Toast.makeText(NewTopicReq.this,i==100 ? "permintaan anda sudah dihapus":"perubahan telah disimpan",Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(ShowConvReq.this,Conversation.class)
//                                .putExtra("topic",getIntent().getExtras().getString("topic")));
                        finish();
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    private void checkRequest(){
        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_CONV_REQ)
                .orderByChild("senderUid")
                .equalTo(User.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.e(TAG,"checkRequest()"+dataSnapshot.getValue());

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            if (String.valueOf(data.child("topicRef").getValue()).equals("")) {

                                Log.e(TAG,"checkRequest()");

                                text.setText(String.valueOf(data.child("payload").child("messageText").getValue()));
                                title.setText(String.valueOf(data.child("topicTitle").getValue()));
                                reqExist=true;
                                Log.e(TAG, "reqExist "+String.valueOf(reqExist));
                                reqRef=String.valueOf(data.getRef());
                                break;
                            }
                        }

                        if (reqExist==false){
                            editLay.setVisibility(View.GONE);
                            requestLay.setVisibility(View.VISIBLE);
                            Log.e(TAG,"belum request");
                            sendBtn();
                        }else{
                            requestLay.setVisibility(View.GONE);
                            editLay.setVisibility(View.VISIBLE);
                            Log.e(TAG,"sudah request");
                            editBtn();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // to make sure when no internet
        if (reqExist==false){
            editLay.setVisibility(View.GONE);
            requestLay.setVisibility(View.VISIBLE);
        }

    }

    private void redirectSignIn(){
        new AlertDialog.Builder(NewTopicReq.this)
                .setTitle("Perhatian")
                .setMessage("anda harus Log In untuk menggunakan fasilitas ini"
                                + "\nLog In sekarang?"
                )
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(NewTopicReq.this, SignInActivity.class));
                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    private void showAlertReq(){

        new AlertDialog.Builder(NewTopicReq.this)
                .setTitle("Perhatian")
                .setMessage("apakah anda yakin ingin mengirim pertanyaan ini?")
                .setPositiveButton("kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendReq();
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .show();
    }

    private void sendReq(){
        EditText text = (EditText) findViewById(R.id.text_val);
        EditText title = (EditText) findViewById(R.id.title_val);

        Map quote = new HashMap<String, String>();
        quote.put("text", "");
        quote.put("uidFrom", "");

        String displayName;
        if(FirebaseAuth.getInstance().getCurrentUser().getProviders().toString().equals("[phone]")){
            displayName=String.valueOf(
                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
            ).substring(0,
                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().length()-3)+"XXX";
        }else displayName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        boolean auth = false;
        if(!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            auth = true;
        }

        ConvHandler payload = new ConvHandler(text.getText().toString(),
                displayName,
//                FirebaseAuth.getInstance().getCurrentUser().getUid(), quote,auth);
                FirebaseAuth.getInstance().getCurrentUser().getUid(), quote);

        Log.e("quote", String.valueOf(quote));
        Log.e("payload", String.valueOf(payload));

        Map req = new HashMap();
        req.put("topicRef", "");
        req.put("topicTitle", title.getText().toString());
        req.put("senderUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        req.put("payload", payload);

        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_CONV_REQ)
                .push()
                .setValue(req);

        Toast.makeText(getApplication(),"permintaas sudah dikirim silahkan tunggu konfirmasi",Toast.LENGTH_LONG).show();

        finish();
    }

}
