package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspkumobile.R;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private TextView emptyList;
    private RecyclerView tipsList;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private JSONArray dataList;
    private ArrayList<String> items;
    private MyRecycleViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        emptyList = (TextView)findViewById(R.id.empty_list);

        tipsList = (RecyclerView)findViewById(R.id.tips);
        tipsList.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        // these codes make new item stack from top
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mDividerItemDecoration = new DividerItemDecoration(tipsList.getContext(),mLayoutManager.getOrientation());

        tipsList.addItemDecoration(mDividerItemDecoration);
        tipsList.setLayoutManager(mLayoutManager);

        getAllitem();

        
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

    private ArrayList<String> getAllitem(){
        adapter = new MyRecycleViewAdapter(TipsActivity.this);

        items = new ArrayList<>();

        String data = SharedPrefManager.getInstance(TipsActivity.this).getContentService();
        Log.e("data --",data);
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
        //to revers order make newest on first, LIFO
        Collections.reverse(items);
        adapter.setItems(items);

        tipsList.setAdapter(adapter);

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

            View v = mInflater.inflate(R.layout.holder_tips_activity,parent,false);
            ViewHolder viewHolder= new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String tip = mData.get(position);
            holder.tip.setText(tip);

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView tip;
            public ViewHolder(View v){
                super(v);
                tip = (TextView)v.findViewById(R.id.text);
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

        new AlertDialog.Builder(TipsActivity.this)
                .setTitle(s)
                .setMessage(text.replaceAll(System.getProperty("line.separator"),""))
                .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    
}
