package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashScreenActivityOld extends AppCompatActivity {

    public int time=8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Helper.setFullscreen(SplashScreenActivityOld.this);

        new prefetchData().execute();

    }

    private class prefetchData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            RequestHandler reqH=new RequestHandler();

            HashMap<String,String> param=new HashMap<>();
            param.put("rspku_mobile","prefetch data");

            String response = reqH.sendPostRequest(Config.HOME, param);

            Log.e("prefetch", String.valueOf(response));

            JSONObject json = null;
            String homeContent = null;
            String serviceData = null;

            try {
                json = new JSONObject(String.valueOf(response));
                homeContent = json.getJSONObject("homeContent").toString().replace("localhost",Config.IP);
//                Log.e("prefetchHomeContent",homeContent);
                serviceData = json.getJSONObject("serviceData").toString();
                Log.e("prefetchServiceData",serviceData);
//                if(!homeContent.isEmpty())
                SharedPrefManager.getInstance(getApplicationContext()).storeHomeContent(homeContent);
                SharedPrefManager.getInstance(getApplicationContext()).storeFeaturesData(serviceData);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("prefetchError","error");
//                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("postPrefetch",SharedPrefManager.getInstance(SplashScreenActivityOld.this).getContentSlide()+" "+
                    SharedPrefManager.getInstance(SplashScreenActivityOld.this).getContentService());
            if(SharedPrefManager.getInstance(SplashScreenActivityOld.this).getContentSlide()!=null &&
                    SharedPrefManager.getInstance(SplashScreenActivityOld.this).getContentService()!=null) {

                if(User.getUid()!=null)
                    Helper.setAlarms(SplashScreenActivityOld.this);

                startActivity(new Intent(SplashScreenActivityOld.this,
                        MainActivityDrawer.class));

                finish();

            }else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreenActivityOld.this);

                dialog.setMessage("gagal mengunduh data, mohon periksa koneksi internet anda");

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });

                dialog.show();
            }
        }
    }
}
