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
import com.rspkumobile.adapter.ArticleCardAdapterBackUp;
import com.rspkumobile.model.ArticleModelBackUp;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeContentArticleBackUp extends Fragment {

    private RecyclerView MyRecyclerView;
    private TextView more;
    public ArrayList<ArticleModelBackUp> items;

    public HomeContentArticleBackUp() {
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

        String data = SharedPrefManager.getInstance(getActivity()).getContentArticle();
        if(data!=null) {
            Log.e("data --", data);
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
                    items.add(new ArticleModelBackUp(dataJson.getString("img"), dataJson.getString("title"), dataJson.getString("content")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content_article, container, false);

        more = (TextView)view.findViewById(R.id.more_article);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "Replace with your own action_bar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getActivity(),ArticleMenuActivity.class);
                startActivity(intent);
            }
        });

        MyRecyclerView = (RecyclerView) view.findViewById(R.id.hot_articles_list);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (items.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new ArticleCardAdapterBackUp(items,getActivity().getApplicationContext()));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        prepareContent();

    }
}
