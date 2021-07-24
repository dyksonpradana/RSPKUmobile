package com.rspkumobile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.rspkumobile.R;
import com.rspkumobile.adapter.ArticleMenuAdapter;
import com.rspkumobile.adapter.ArticleMenuAdapterBackUp;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.model.ArticleModelBackUp;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ArticleMenuActivityBackUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView gridview = (GridView)findViewById(R.id.gridview);

        List<ArticleModelBackUp> allItems = getAllArticleModel();
        ArticleMenuAdapterBackUp customAdapter = new ArticleMenuAdapterBackUp(this, allItems);
        gridview.setAdapter(customAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ArticleMenuActivityBackUp.this, "Position: " + position, Toast.LENGTH_SHORT).show();
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

    private List<ArticleModelBackUp> getAllArticleModel(){
        List<ArticleModelBackUp> items = new ArrayList<>();
//        items.add(new ArticleModel(R.drawable.foto_rs,"Didfp Isdfst Lfgdfow", "Csdhrisdasdstina Mialsfaian"));
//        items.add(new ArticleModel(R.drawable.a1,"D", "asdffhg"));
//        items.add(new ArticleModel(R.drawable.a2,"Sdfg dfg", "fhtyhyjgtdf"));
//        items.add(new ArticleModel(R.drawable.a3,"Radgdhfda adfh", "adgjekjghjhgh"));
//        items.add(new ArticleModel(R.drawable.a4,"Pcmdjgje", "uyltyhtf"));
//        items.add(new ArticleModel(R.drawable.a5,"Fodsvd", "erqgadgdfg"));
//        items.add(new ArticleModel(R.drawable.a6,"Ssd sdgasdg sdgasdf sadf", "rtuhegdfg"));
//        items.add(new ArticleModel(R.drawable.a7,"Marsdgsdek", "ytkjgrfjt"));

        String data = SharedPrefManager.getInstance(ArticleMenuActivityBackUp.this).getContentArticle();
        Log.e("data --",data);
        JSONArray dataList = null;
        try {
            dataList = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject dataJson = null;

        for (int i = 0; i < dataList.length(); i++) {
            try {
                dataJson = dataList.getJSONObject(i);
                items.add(new ArticleModelBackUp(dataJson.getString("img"),dataJson.getString("title"), dataJson.getString("content")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return items;
    }

}