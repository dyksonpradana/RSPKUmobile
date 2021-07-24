package com.rspkumobile.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rspkumobile.R;
import com.rspkumobile.activity.TipsActivity;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Created by DK on 1/6/2018.
 */

public class HomeContentTips extends Fragment {

    private ArrayList<String> items;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private MyRecycleViewAdapter adapter;
    private RecyclerView tipList;
    private boolean Started;
    private Handler handler = new Handler();
    private TextView more;
    public static JSONArray dataList;

    public HomeContentTips(){

    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        items = new ArrayList<>();
//        items.add(new ArticleModel(R.drawable.foto_rs,"Didfp Isdfst Lfgdfow", "Csdhrisdasdstina Mialsfaian"));
//        items.add(new ArticleModel(R.drawable.a1,"D", "asdffhg"));
//        items.add(new ArticleModel(R.drawable.a2,"Sdfg dfg", "fhtyhyjgtdf"));
//        items.add(new ArticleModel(R.drawable.a3,"Radgdhfda adfh", "adgjekjghjhgh"));
//        items.add(new ArticleModel(R.drawable.a4,"Pcmdjgje", "uyltyhtf"));
//        items.add(new ArticleModel(R.drawable.a5,"Fodsvd", "erqgadgdfg"));
//        items.add(new ArticleModel(R.drawable.a6,"Ssd sdgasdg sdgasdf sadf", "rtuhegdfg"));
//        items.add(new ArticleModel(R.drawable.a7,"Marsdgsdek", "ytkjgrfjt"));
//        getAllitem();
//        for (String i:items){
//            Log.e("recycle",i);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content_tips, container, false);


        more = (TextView)view.findViewById(R.id.more_tips);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),TipsActivity.class);
                startActivity(intent);
            }
        });

//        getAllitem();

        tipList = (RecyclerView) view.findViewById(R.id.tip_list);
        tipList.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        // these codes make new item stack from top
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);

//        mDividerItemDecoration = new DividerItemDecoration(tipList.getContext(),mLayoutManager.getOrientation());
//        tipList.addItemDecoration(mDividerItemDecoration);

        //scrolling animation
        tipList.setLayoutManager(mLayoutManager);
        SlideInDownAnimator animator = new SlideInDownAnimator();
        animator.setAddDuration(100);
        animator.setInterpolator(new OvershootInterpolator());
        tipList.setItemAnimator(animator);

//        tipList.setLayoutManager(new LinearLayoutManager(getActivity()));

        getAllitem();

//        adapter.setClickListener(new MyRecycleViewAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Toast.makeText(getActivity(),""+adapter.getItem(position)+"on row"+position,Toast.LENGTH_LONG).show();
//                String title = null, content, text = null;
//                try {
//                    title = dataList.getJSONObject(position).getString("title");
//                    content = dataList.getJSONObject(position).getString("content");
//                    byte[] data = Base64.decode(content, Base64.DEFAULT);
//                    text = new String(data, StandardCharsets.UTF_8);
//                    Log.e("text",new JSONObject(text).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(title)
//                        .setMessage(text.replace("�",""))
//                        .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .show();
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startRolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRolling();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            adapter.swipeData(0);
            if(Started){
                startRolling();
            }
        }
    };

    public void startRolling(){
        Started = true;
//        handler.postDelayed(runnable,3000);
    }

    public void stopRolling(){
        Started = false;
        handler.removeCallbacks(runnable);
    }


    private ArrayList<String> getAllitem(){
        adapter = new MyRecycleViewAdapter(getActivity());

        items = new ArrayList<>();
//        StringRequest request = new StringRequest(Request.Method.GET, Config.FURL+"/tips.json", new Response.Listener<String>(){
//            @Override
//            public void onResponse(String s) {
//
//                Log.e("sukses",s.toString());
//                doOnSuccess(s);
//            }
//        },new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println("" + volleyError);
//                Toast.makeText(getActivity(),"maaf, terjadi kesalahan tips tidak dapat dimuat (internet)",Toast.LENGTH_LONG).show();
//            }
//        });
//
//        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
//        rQueue.add(request);
        String data = SharedPrefManager.getInstance(getActivity()).getContentService();
//        Log.e("data --",data);
        if(data!=null) {
            dataList = null;
            try {
                dataList = new JSONArray(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject dataJson = null;

            for (int i = 0; i < dataList.length(); i++) {
                try {
                    dataJson = dataList.getJSONObject(i);
                    items.add(dataJson.getString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //to revers order make newest on first, LIFO
        Collections.reverse(items);
        adapter.setItems(items);

        tipList.setAdapter(adapter);
//        startRolling();

        return items;
    }



    public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder>{
        private List<String> mData;
        private final LayoutInflater mInflater;
//        private ItemClickListener mClickListener;

        public MyRecycleViewAdapter(Context ctx){
            this.mInflater = LayoutInflater.from(ctx);
        }

        public void setItems(List<String> data){
            this.mData = data;
            notifyDataSetChanged();
        }

        public void swipeData(int indexBtm){
            String itemBtm = mData.get(indexBtm);
            mData.add(itemBtm);
            mData.remove(indexBtm);
//            notifyDataSetChanged();
            notifyItemRemoved(indexBtm);
            notifyItemChanged(indexBtm);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = mInflater.inflate(R.layout.holder_tips,parent,false);
            ViewHolder viewHolder= new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String tip = mData.get(position);
            holder.tip.setText(tip);

            holder.setIsRecyclable(true);

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView tip;
            public ViewHolder(View v){
                super(v);
                tip = (TextView)v.findViewById(R.id.tip);
                tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(),""+adapter.getItem(position)+"on row"+position,Toast.LENGTH_LONG).show();
                        showDialog(tip.getText().toString());

                    }
                });
            }

            @Override
            public void onClick(View v) {
//                if (mClickListener != null) mClickListener.onItemClick(v,getAdapterPosition());
            }
        }

        public String getItem(int id){
            return mData.get(id);
        }

//        public void setClickListener(ItemClickListener itemClickListener){
//            this.mClickListener = itemClickListener;
//        }

//        public interface ItemClickListener{
//            void onItemClick(View v,int position);
//        }
    }

    public void showDialog(String s){
        String title = null, content, text = null;
        for (int i = 0; i < dataList.length(); i++) {
            try {
                if(dataList.getJSONObject(i).getString("title").equals(s)) {
                    content = dataList.getJSONObject(i).getString("content");
                    byte[] data = Base64.decode(content, Base64.DEFAULT);
                    text = new String(data, StandardCharsets.UTF_8).replace("�","");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(s)
                .setMessage(text.replaceAll(System.getProperty("line.separator"),""))
                .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

//    public void doOnSuccess(String s) {
//        try {
//            JSONObject obj = new JSONObject(s);
//
//            Iterator i = obj.keys();
//            String key = "";
//
//            while (i.hasNext()) {
//                key = i.next().toString();
//                items.add(obj.getJSONObject(key).getString("content"));
//                items.add(obj.getJSONObject(key).getString("content")+"1");
//                items.add(obj.getJSONObject(key).getString("content")+"2");
//                Log.e("recycle",obj.getJSONObject(key).getString("content"));
////                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("recycle0",items.get(0));
//
////        adapter.setClickListener(new MyRecycleViewAdapter.ItemClickListener() {
////            @Override
////            public void onItemClick(View v, int position) {
////                Toast.makeText(getActivity(),""+adapter.getItem(position)+"on row"+position,Toast.LENGTH_LONG).show();
////                String title = null, content, text = null;
////                try {
////                    title = dataList.getJSONObject(position).getString("title");
////                    content = dataList.getJSONObject(position).getString("content");
////                    byte[] data = Base64.decode(content, Base64.DEFAULT);
////                    text = new String(data, StandardCharsets.UTF_8);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////                new AlertDialog.Builder(getActivity())
////                        .setTitle(title)
////                        .setMessage(text)
////                        .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                            }
////                        })
////                        .show();
////            }
////        });
//
//        //to revers order make newest on first, LIFO
//        Collections.reverse(items);
//        adapter.setItems(items);
//
//        tipList.setAdapter(adapter);
//        startRolling();
//
//    }

}
