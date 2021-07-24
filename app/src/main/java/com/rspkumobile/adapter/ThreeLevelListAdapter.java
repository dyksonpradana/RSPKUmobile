package com.rspkumobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspkumobile.R;
import com.rspkumobile.other.SecondLevelExpandableListView;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ThreeLevelListAdapter extends BaseExpandableListAdapter {

    String[] parentHeaders;
    List<String[]> secondLevel;
    private Activity activity;
    List<LinkedHashMap<String, String[]>> data;
    JSONObject spinner;

    public ThreeLevelListAdapter(Activity activity, String[] parentHeader, List<String[]> secondLevel, List<LinkedHashMap<String, String[]>> data, JSONObject spinner) {
        this.activity = activity;

        this.parentHeaders = parentHeader;

        this.secondLevel = secondLevel;

        this.data = data;

        this.spinner = spinner;
    }

    @Override
    public int getGroupCount() {
        return parentHeaders.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {


        // no idea why this code is working

        return 1;

    }

    @Override
    public Object getGroup(int groupPosition) {

        return groupPosition;
    }

    @Override
    public Object getChild(int group, int child) {


        return child;


    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.expandable_list_row_first, null);
        final TextView text = (TextView) convertView.findViewById(R.id.rowParentText);
        Log.e("group position",groupPosition+" ");
        if(this.parentHeaders.length>0)
            text.setText(this.parentHeaders[groupPosition]);
//        text.setText(this.parentHeaders[groupPosition]);

        ImageView indicator = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);

        if(isExpanded){
            indicator.setBackgroundResource(R.drawable.indicator_group_up);
        }else indicator.setBackgroundResource(R.drawable.indicator_group_down);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(activity);

        String[] headers = secondLevel.get(groupPosition);


        List<String[]> childData = new ArrayList<>();
        HashMap<String, String[]> secondLevelData=data.get(groupPosition);


        Log.e("header second lv key", Arrays.asList(headers).toString());

        Log.e("second lv key", secondLevelData.toString());

        for(String key : headers)
        {

            childData.add(secondLevelData.get(key));

//            Log.e("second lv key", key+" "+ Arrays.asList(secondLevelData.get(key)));

        }

        final int x = groupPosition;


        secondLevelELV.setAdapter(new SecondLevelAdapter(activity, groupPosition, this.parentHeaders[groupPosition],headers,childData, spinner));

        secondLevelELV.setGroupIndicator(null);

        Log.e("expandingIndexLv2",SharedPrefManager.getInstance(activity).getIndexSecondRowToExpand()+" "+
                SharedPrefManager.getInstance(activity).getIndexSecondRowToExpand());

        if(SharedPrefManager.getInstance(activity).getIndexSecondRowToExpand()!=-1){
            secondLevelELV.expandGroup(SharedPrefManager.getInstance(activity).getIndexSecondRowToExpand());
            SharedPrefManager.getInstance(activity).expandSecondRowAt(-1);
        }

//        secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(activity,x+" "+groupPosition+" "+childPosition,Toast.LENGTH_LONG).show();
//                Log.e("click",groupPosition+" "+childPosition);
//                return false;
//            }
//        });


        secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    secondLevelELV.collapseGroup(previousGroup);
                previousGroup = groupPosition;

            }
        });

//        secondLevelELV.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                if(groupPosition==previousGroup)
//                    SharedPrefManager.getInstance(getActivity()).expandSecondRow(-1);
//            }
//        });

        return secondLevelELV;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
