package com.rspkumobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.TipsHandler;

public class TipsActivityEx extends AppCompatActivity {

    private TextView emptyList;
    private RecyclerView tipsList;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;

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


        attachRecyclerViewAdapter();
        
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

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            }

            @Override
            public void onChanged() {
                super.onChanged();

            }
        });
        tipsList.setAdapter(adapter);

    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<TipsHandler> options =
                new FirebaseRecyclerOptions.Builder<TipsHandler>()
                        .setQuery(FirebaseDatabase.getInstance()
                                        .getReferenceFromUrl(Config.FURL_TIPS)
//                                        .limitToLast(50)
                                , TipsHandler.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<TipsHandler, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_tips_activity, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final TipsHandler model) {
                holder.tips.setText(model.getContent());
                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog(holder.tips.getText().toString());
                    }
                });

                holder.setIsRecyclable(false);
            }

            @Override
            public void onDataChanged() {

                if(getItemCount()==0) {
                    tipsList.removeAllViews();
                    tipsList.setVisibility(View.INVISIBLE);
                    emptyList.setVisibility(View.VISIBLE);
                }else{
                    emptyList.setVisibility(View.GONE);
                    tipsList.setVisibility(View.VISIBLE);
                }

            }
        };
    }

    private void dialog(String s) {
        new AlertDialog.Builder(this)
                .setTitle(null)
                .setMessage(s)
                .setNegativeButton("tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tips;
        public LinearLayout container;

        public ViewHolder(View v){
            super(v);
            tips = (TextView)v.findViewById(R.id.text);
            container = (LinearLayout)v.findViewById(R.id.container_booking);
        }
    }
    
}
