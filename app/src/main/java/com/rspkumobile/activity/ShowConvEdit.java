package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;

public class ShowConvEdit extends AppCompatActivity {

    private String topicRef,reqRef;
    private static final String TAG = ShowConvEdit.class.getSimpleName();
    EditText text, titleEt;
    TextView title;
    private String sTitle;
    private String sText;
    private String textOnCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conv_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Log.e(TAG, "dot-"+topicRef+","+reqRef);

        title = (TextView) findViewById(R.id.title_val);
        text = (EditText) findViewById(R.id.text_val);
        titleEt = (EditText) findViewById(R.id.title_et);


        Button save = (Button)findViewById(R.id.save);
        Button delete = (Button)findViewById(R.id.delete);
        Button send =(Button)findViewById(R.id.btn_send);

        // check if there is no such extra return false

        topicRef = Config.FURL_CONV_APPROVED + getIntent().getExtras().getString("topic");
        reqRef = getIntent().getExtras().getString("reqRef");

        setText();
        setTitle();

        Toast.makeText(ShowConvEdit.this,"out",Toast.LENGTH_LONG).show();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!String.valueOf(text.getText()).equals(textOnCreate))showAlertReq(101);
                else Toast.makeText(ShowConvEdit.this,"tidak terdapat perubahan untuk disimpan",Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertReq(100);
            }
        });

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

    private void showAlertReq(final int i){
        Log.e(TAG, "topic :" + topicRef);
        Log.e(TAG, "reqRef :" + reqRef);
        String warningText = null;
        if (i==100){
            warningText = "anda akan menghapus permintaan anda";
        } else {
            warningText = "apakah anda ingin menyimpan perubahan?";
        }
        new AlertDialog.Builder(ShowConvEdit.this)
                .setTitle("Perhatian")
                .setMessage(warningText)
                .setPositiveButton(i==100 ? "hapus":"simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(i==100) FirebaseDatabase.getInstance().getReferenceFromUrl(reqRef).removeValue();
                        else FirebaseDatabase.getInstance().getReferenceFromUrl(reqRef+"/payload/messageText").setValue(text.getText().toString());
                        Toast.makeText(ShowConvEdit.this,i==100 ? "permintaan anda sudah dihapus":"perubahan telah disimpan",Toast.LENGTH_LONG).show();
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

    private String setTitle(){
        FirebaseDatabase.getInstance().getReferenceFromUrl(topicRef).child("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "title -:" + String.valueOf(dataSnapshot.getValue()));

//                        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
////                                if (String.valueOf(data.child("title").getValue()).equals(topicRef)) {
//                            Log.e(TAG, "title :" + String.valueOf(data.getValue()));
//                            Log.e(TAG, "title :" + String.valueOf(data.child("title").getValue()));
////                                    Log.e(TAG, "data :" + data.child("topic").getValue());
////                                    Log.e(TAG, "data :" + data.child("topic").getValue() + "=" + topicRef + "," + (String.valueOf(data.child("topic").getValue()).equals(topicRef)));
////                                    Log.e(TAG, "data :" + data.child("payload").child("messageText").getValue());
                        String x = dataSnapshot.getValue(String.class);

//                        TextView title = (TextView) findViewById(R.id.title_val);
                        title.setText(x);
//                            title.setText(sTitle);
//                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return sTitle;
    }

    private String setText(){
        FirebaseDatabase.getInstance().getReferenceFromUrl(reqRef)
//
//                .orderByChild("senderUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "hasil :" + String.valueOf(dataSnapshot.getValue()));
//                        EditText text = (EditText) findViewById(R.id.text_val);
                        String s = null;

//                        if (dataSnapshot.getValue() == null) {
//                        } else {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

//                                if (String.valueOf(data.child("topic").getValue()).equals(topicRef)) {
//                                    Log.e(TAG, "data :" + data.toString());
//                                    Log.e(TAG, "data :" + data.child("topic").getValue());
//                                    Log.e(TAG, "data :" + data.child("topic").getValue() + "=" + topicRef + "," + (String.valueOf(data.child("topic").getValue()).equals(topicRef)));
//                                    Log.e(TAG, "data :" + data.child("payload").child("messageText").getValue());

//                                s.add(data.child("messageText").getValue(String.class));


                            Log.e(TAG, "text messageText:" + data.child("messageText").getValue());
                            s = String.valueOf(data.child("messageText").getValue());
                            break;

//                                }
                        }
                        text.setText(s);
                        textOnCreate = s;


//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return  sText;

    }
}
