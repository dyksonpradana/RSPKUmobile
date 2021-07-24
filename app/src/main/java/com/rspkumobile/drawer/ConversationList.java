package com.rspkumobile.drawer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rspkumobile.R;
import com.rspkumobile.activity.Conversation;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConversationList extends Fragment {
    public ListView topicList;
    public ArrayList<String> topicsKey = new ArrayList<>();
    public SwipeRefreshLayout refreshLayout;
    public String url = Config.FURL_CONSULTATION+"topic/approved.json";
    private TextView emptyView;
    private ArrayList<String> data;
    private ArrayList<String> topicsTitle;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_conversation, container, false);

        setHasOptionsMenu(true);

//        ArrayList<String> datahari = new ArrayList<>();
//
//        datahari.addAll(Arrays.asList(getResources().getStringArray(R.array.hari)));

        topicList = (ListView)view.findViewById(R.id.topic_list);
        emptyView = (TextView)view.findViewById(R.id.empty_list);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.fui_bgTwitter)
                ,getResources().getColor(R.color.fui_bgPhone)
                ,getResources().getColor(R.color.colorPrimaryDark));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshList();
//            }
//        });

//        /** refreshList(....
//        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
//            @Override
//            public void onResponse(String s) {
//
//                doOnSuccess(s);
//            }
//        },new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println("" + volleyError);
//            }
//        });
//
//        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
//        rQueue.add(request); **//

        topicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getActivity(), Conversation.class);

                String topic = data.get(position);
                int location= topicsTitle.indexOf(topic);

                i.putExtra("topicKey",topicsKey.get(location));
                startActivity(i);
                Toast.makeText(getActivity(),topicsKey.get(location),Toast.LENGTH_LONG).show();

                // start conversation fragment
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                Fragment
            }
        });

//        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
//
//        topicList.setAdapter(adapter);
        refreshList();

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
        return  view;
    }

    public void refreshList(){

        //start refresh animation on create
        refreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                refreshLayout.setRefreshing(false);
//                Toast.makeText(getActivity(),volleyError+"maaf, terjadi kesalahan",Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);
    }

    public void doOnSuccess(String s){
        topicsKey.clear();
        topicsTitle = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();
                topicsKey.add(key);
                topicsTitle.add(obj.getJSONObject(key).getString("title"));
//                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(topicsKey.size()==0){
            topicList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.GONE);
            topicList.setVisibility(View.VISIBLE);
        }

        data = topicsTitle;

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);

        topicList.setAdapter(adapter);

        //stop refresh animation
        refreshLayout.setRefreshing(false);
//        Toast.makeText(getActivity(),"masuk",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.action_bar,menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        if(searchMenuItem!=null) {
            searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
//        {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.e("query", query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.e("new text", newText);
//                if (newText.equals("")) {
//
//                } else {
//                    String[] keys = newText.split(" ");
//                    for (String key : keys) {
//                        Arrays.asList(data).contains(key);
//                    }
//                    if (newText.length() == 0) data = topicsTitle;
//
//                    topicList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data));
//                }
//
//                return false;
//            }
//        });
            SearchManager searchm = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchm.getSearchableInfo(getActivity().getComponentName()));
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}