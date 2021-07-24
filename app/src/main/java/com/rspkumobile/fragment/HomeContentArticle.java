package com.rspkumobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rspkumobile.R;
import com.rspkumobile.activity.ArticleMenuActivity;
import com.rspkumobile.adapter.ArticleCardAdapter;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeContentArticle extends Fragment {

    private RecyclerView HotArticlesRecyclerView,PopularArticlesRecyclerView;
    private TextView more;
    public List<ArticleModel> items;
    public List<ArticleModel> popularArticleList;
    public List<ArticleModel> hotArticleList;

    public HomeContentArticle() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareContent();
//        items = new ArrayList<>();
//        items.add(new ArticleModel(R.drawable.foto_rs,"Didfp Isdfst Lfgdfow", "Csdhrisdasdstina Mialsfaian"));
//        items.add(new ArticleModel(R.drawable.a1,"D", "asdffhg"));
//        items.add(new ArticleModel(R.drawable.a2,"Sdfg dfg", "fhtyhyjgtdf"));
//        items.add(new ArticleModel(R.drawable.a3,"Radgdhfda adfh", "adgjekjghjhgh"));
//        items.add(new ArticleModel(R.drawable.a4,"Pcmdjgje", "uyltyhtf"));
//        items.add(new ArticleModel(R.drawable.a5,"Fodsvd", "erqgadgdfg"));
//        items.add(new ArticleModel(R.drawable.a6,"Ssd sdgasdg sdgasdf sadf", "rtuhegdfg"));
//        items.add(new ArticleModel(R.drawable.a7,"Marsdgsdek", "ytkjgrfjt"));
    }

    private void prepareContent() {
        items = new ArrayList<>();
        popularArticleList = new ArrayList<>();
        hotArticleList = new ArrayList<>();

        // get stored article data in shared preference (string)
        String data = SharedPrefManager.getInstance(getActivity()).getArticle();
        Log.e("dataArticle",data.toString());

        // check if the data is empty
        if(data!=null) {
            Log.e("data--", data);
            JSONArray dataList = null;
            try {

                // convert data string into json array
                dataList = new JSONArray(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject dataJson = null;

            // insert each json array value (object) into new array list "items"
            for (int i = 0; i < dataList.length(); i++) {
                try {
                    dataJson = dataList.getJSONObject(i);

                    items.add(new ArticleModel(dataJson.getInt("id"),
                            dataJson.getString("title"),
                            dataJson.getString("image"),
                            dataJson.getString("content"),
                            dataJson.getString("date"),
                            dataJson.getString("popularity")));
                    popularArticleList.add(new ArticleModel(dataJson.getInt("id"),
                            dataJson.getString("title"),
                            dataJson.getString("image"),
                            dataJson.getString("content"),
                            dataJson.getString("date"),
                            dataJson.getString("popularity")));
                    hotArticleList.add(new ArticleModel(dataJson.getInt("id"),
                            dataJson.getString("title"),
                            dataJson.getString("image"),
                            dataJson.getString("content"),
                            dataJson.getString("date"),
                            dataJson.getString("popularity")));
                } catch (JSONException e) {
                    items.add(new ArticleModel(0000,
                            "kesalaha dalam pengunduhan data",
                            "error",
                            "error",
                            "error",
                            "error"));
                    hotArticleList.add(new ArticleModel(0000,
                            "kesalaha dalam pengunduhan data",
                            "error",
                            "error",
                            "error",
                            "error"));
                    popularArticleList.add(new ArticleModel(0000,
                            "kesalaha dalam pengunduhan data",
                            "error",
                            "error",
                            "error",
                            "error"));
//                    e.printStackTrace();
                }
            }
//            popularArticleList =items;
//            hotArticleList =items;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content_article, container, false);

        // initialising text link to open more articles
        more = (TextView)view.findViewById(R.id.more_article);
        // set on click to open articles menu
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "Replace with your own action_bar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getActivity(),ArticleMenuActivity.class);
                startActivity(intent);
            }
        });

        // initialising hot articles container
        HotArticlesRecyclerView = (RecyclerView) view.findViewById(R.id.hot_articles_list);
        HotArticlesRecyclerView.setHasFixedSize(true);

        PopularArticlesRecyclerView = (RecyclerView) view.findViewById(R.id.popular_articles_list);
        PopularArticlesRecyclerView.setHasFixedSize(true);

//        Arrays.sort(items, ArticleModel::byHot);


        // sort items based on hot articles
        Collections.sort(hotArticleList, new Comparator<ArticleModel>(){

            public int compare(ArticleModel o1, ArticleModel o2)
            {
                // prepate date format to convert date string to date format
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                // converting
                Date date1 = null;
                try {
                    date1 = sdf.parse(o1.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date2 = null;
                try {
                    date2 = sdf.parse(o2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return date2.compareTo(date1);
            }
        });

        // create article card view for each items's value
//        if (hotArticleList.size() > 0 & HotArticlesRecyclerView != null) {
//        hotArticleList.remove(1);
//        hotArticleList.remove(2);
        if (hotArticleList.size() > 0) {
//            if (hotArticleList.size() > 7) hotArticleList = hotArticleList.subList(0,7);
            List<ArticleModel> hotItems = new ArrayList<>();
            for(int i = 0; i< (hotArticleList.size() > 7 ? 7: hotArticleList.size()); i++){
                hotItems.add(hotArticleList.get(i));
            }
            HotArticlesRecyclerView.setAdapter(new ArticleCardAdapter(hotItems,getActivity().getApplicationContext()));
        }

        // sorting array of object using the object properties
        // sort items based on popular articles
        Collections.sort(popularArticleList, new Comparator<ArticleModel>(){

            public int compare(ArticleModel o1, ArticleModel o2)
            {
                return o2.getPopularity().compareTo(o1.getPopularity());
            }
        });

        // create article card view for each items's value
//        if (popularArticleList.size() > 0 & PopularArticlesRecyclerView != null) {
        if (popularArticleList.size() > 0) {
//            if (popularArticleList.size() > 3) popularArticleList = popularArticleList.subList(0,3);
            List<ArticleModel> popularItems = new ArrayList<>();
            for(int i = 0; i< (popularArticleList.size() > 7 ? 7: popularArticleList.size()); i++){
                popularItems.add(popularArticleList.get(i));
            }
            PopularArticlesRecyclerView.setAdapter(new ArticleCardAdapter(popularItems,getActivity().getApplicationContext()));
        }

        // typical recylerview or any other list view in android studio is vertical oriented
        // codes bellow set the orientation to horizontal
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        HotArticlesRecyclerView.setLayoutManager(MyLayoutManager);

        LinearLayoutManager MyLayoutManager2 = new LinearLayoutManager(getActivity());
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        PopularArticlesRecyclerView.setLayoutManager(MyLayoutManager2);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        prepareContent();

    }

}
