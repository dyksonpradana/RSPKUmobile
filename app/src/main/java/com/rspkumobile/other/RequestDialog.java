package com.rspkumobile.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;

/**
 * Created by DK on 1/18/2018.
 */

public class RequestDialog {

    private Context ctx;
    public RequestDialog(Context context){
        this.ctx = context;
    }

    public void dialogAntri() {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptView = li.inflate(R.layout.prompt_request,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);

        dialogBuilder.setView(promptView);

        final EditText topic = (EditText)promptView.findViewById(R.id.et_topic);
        final EditText content = (EditText)promptView.findViewById(R.id.et_question);

        dialogBuilder
//                .setTitle("Nama Pemesan")
//                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendRequest();
                        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                })
                .setNegativeButton("kembali",null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void sendRequest(){

    }

    private void checkRequest(){
        FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_CONV_REQ)
                .orderByChild("senderUid")
                .equalTo(User.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        Log.e(TAG,"checkRequest()"+dataSnapshot.getValue());

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            if (String.valueOf(data.child("topicRef").getValue()).equals("")) {

//                                Log.e(TAG,"checkRequest()");

//                                text.setText(String.valueOf(data.child("payload").child("messageText").getValue()));
//                                title.setText(String.valueOf(data.child("topicTitle").getValue()));
//                                reqExist=true;
//                                Log.e(TAG, "reqExist "+String.valueOf(reqExist));
//                                reqRef=String.valueOf(data.getRef());
                                break;
                            }
                        }

//                        if (reqExist==false){
//                            editLay.setVisibility(View.GONE);
//                            requestLay.setVisibility(View.VISIBLE);
//                            Log.e(TAG,"belum request");
//                            sendBtn();
//                        }else{
//                            requestLay.setVisibility(View.GONE);
//                            editLay.setVisibility(View.VISIBLE);
//                            Log.e(TAG,"sudah request");
//                            editBtn();
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // to make sure when no internet
//        if (reqExist==false){
//            editLay.setVisibility(View.GONE);
//            requestLay.setVisibility(View.VISIBLE);
//        }

    }

}
