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

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    public int time=8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Helper.setFullscreen(SplashScreenActivity.this);

        // fetching data from the database before running the main page
        new prefetchData().execute();

    }

    private class prefetchData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            HashMap<String,String> param=new HashMap<>();
            // "rspku_mobile" is the variable of php POST method to fetch particular data
            // following by the php POST method value "prefetch data"
            param.put("rspku_mobile","prefetch data");

            // create a new class to handle request to API
            RequestHandler reqH = new RequestHandler();
            // send request to the server through API and catch the response
            // "Config.HOME is the link server with particular php POST value to reach the API
            String response = reqH.sendPostRequest(Config.PREFETCH, param);

            Log.e("prefetch", String.valueOf(response));

            JSONObject json = null;
            String galery = null;
            String article = null;
            String services = null;

            try {
                // generate json object from string stored in variable response
                json = new JSONObject(String.valueOf(response));

                // get and store object's value of article in shared preferences variable
                article = json.getJSONArray("article").toString().replace("localhost",Config.IP);
                SharedPrefManager.getInstance(getApplicationContext()).storeArticle(article);
                Log.e("prefetchArticle",TAG+article);

                // get and store object's value of gallery in shared preferences variable
                galery = json.getJSONArray("gallery").toString().replace("localhost",Config.IP);
                SharedPrefManager.getInstance(getApplicationContext()).storeGallery(galery);
                Log.e("prefetchGallery",TAG+SharedPrefManager.getInstance(getApplicationContext()).getGallery());

                services = json.getJSONObject("features").toString().replace("localhost",Config.IP);
                SharedPrefManager.getInstance(getApplicationContext()).storeFeaturesData(services);
//                Log.e("prefetchService",TAG+SharedPrefManager.getInstance(getApplicationContext()).getGallery());
//                if(!homeContent.isEmpty())

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

//            Log.e("postPrefetch",SharedPrefManager.getInstance(SplashScreenActivity.this).getContentSlide()+" "+
//                    SharedPrefManager.getInstance(SplashScreenActivity.this).getContentService());

            // check whether shared preference data (gallery and article) is empty
            if(SharedPrefManager.getInstance(SplashScreenActivity.this).getGallery()!=null &&
                    SharedPrefManager.getInstance(SplashScreenActivity.this).getArticle()!=null) {

                // check is user logged in or not
//                if(User.getUid()!=null)
                if(User.isLogedIn())
                    // if user is logged in reset local remainder of booking
                    Helper.setAlarms(SplashScreenActivity.this);

                // proceed to main activity/menu
                startActivity(new Intent(SplashScreenActivity.this,
                        MainActivityDrawer.class));

                // destroy current activity
                finish();

            }else {

                // if the data is empty
                // create (initialization) alert dialog of downloading content data failure as dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreenActivity.this);
                dialog.setMessage("gagal mengunduh data, mohon periksa koneksi internet anda");
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // destroy application when user touching the screen (canceling the dialog)
                        finish();
                    }
                });

                // show the alert dialog
                dialog.show();
            }
        }
    }
}
