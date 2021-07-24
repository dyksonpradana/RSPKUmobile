package com.rspkumobile.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.rspkumobile.R;
import com.rspkumobile.activity.MainActivityDrawer;
import com.rspkumobile.activity.SplashScreenActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DK on 3/12/2018.
 */

public class LoginDoctor extends Fragment {
    private User userDetail;
    private EditText etUnip, etPwd;
    private TextView etError;
    private Button send;
    private TextView entryNotFound;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_doctor, container, false);

        entryNotFound = (TextView)view.findViewById(R.id.not_found);

        etUnip = (EditText)view.findViewById(R.id.unip);
        etPwd = (EditText)view.findViewById(R.id.pwd);
        send = (Button)view.findViewById(R.id.btn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etUnip.getText())&&!TextUtils.isEmpty(etPwd.getText())){
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Memuat...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    new isDocterExist(etUnip.getText().toString(), etPwd.getText().toString()).execute();
                }else if(TextUtils.isEmpty(etUnip.getText())) {
                    etUnip.setError("masukan etUnip anda");
                    etUnip.requestFocus();
                }else if(TextUtils.isEmpty(etPwd.getText())){
                    etPwd.setError("masukan password");
                    etPwd.requestFocus();
                }
            }
        });


        return view;
    }

    public class isDocterExist extends AsyncTask<Void,String,String> {

        private String unip,pwd,xresponse;

        public isDocterExist(String unip, String pwd) {
            this.unip = unip;
            this.pwd = pwd;
        }

        protected void onPreExecute(){
            super.onPreExecute();
            Log.e("masukDocter","masuk");
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler reqH=new RequestHandler();

            HashMap<String,String>params=new HashMap<>();
            params.put("rspku_mobile","login doctor");
            params.put("uid",User.getUid());
            params.put("unip",unip);
            params.put("pwd",pwd);

            String response = reqH.sendPostRequest(Config.DOCTOR_LOGIN, params);

            Log.e("loginDocterResponse", String.valueOf(response));

            return response;

//            return reqH.sendPostRequest(Config.HOME,params);
//            if(!x.isEmpty())
//                SharedPrefManager.getInstance(getActivity()).storeHomeContent(x.toString().replace("localhost",Config.IP));
//            return x;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            JSONObject json = null;
////            String unip
//
            Log.e("response",response.toString());

            try {
                json = new JSONObject(response);
                response = json.getString("response").toString();
//                datax = json.getJSONObject("data").toString();
//                name = json.getString("name").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("responseDocter", response);
            if(("success").equals(response)){

                try {

                    JSONObject data = json.getJSONObject("data");

                    String name = data.getString("name").toString();

                    Toast.makeText(getActivity(),"selamat datang Dr. "+name,Toast.LENGTH_LONG).show();

                    Map val = new HashMap();
                    val.put("unip", data.getString("unip").toString());
                    val.put("name", name);
                    val.put("uid", User.getUid());

                    User.updateDisplayName(name);

                    addUserDoctor(val);

//                    getActivity().finish();

                } catch (JSONException e) {
                    Helper.signOut(getActivity());
                    e.printStackTrace();
                }

            }else{
                pDialog.dismiss();
                Toast.makeText(getActivity(),"mohon masukan etUnip dan password dengan benar",Toast.LENGTH_LONG).show();
                etUnip.clearComposingText();
                etPwd.clearComposingText();
//                entryNotFound.setText(R.string.entry_not_found);
                entryNotFound.setVisibility(View.VISIBLE);
            }

        }
    }

    public void addUserDoctor(Map val){

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
                .child(User.getUid())
                .setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                pDialog.dismiss();
                getActivity().finish();
            }
        });
    }
}
