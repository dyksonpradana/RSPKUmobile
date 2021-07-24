package com.rspkumobile.drawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.adapter.ThreeLevelListAdapter;
import com.rspkumobile.app.Config;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class Booking extends Fragment {

    /**
     * The Expandable list view.
     */
    ExpandableListView expandableListView;

    // We have two  main category. (third one is left for example addition)

    /**
     * The Parent Group Names.
     */

//    List<String> parent = new ArrayList<>(Arrays.asList("MOVIES", "GAMES"));
    String[] parent = new String[]{"MOVIES", "GAMES"}; // comment this when uncomment bottom
    //String[] parent = new String[]{"MOVIES", "GAMES", "SERIALS"}; // example for 3 main category lists

    /*
    If above line is uncommented uncomment the following too:
    - serials array
    - serials genre list
    - Datastructure for Third level Serials.
    - secondLevel.add(serials);
    - serials category all data
    - data.add(thirdLevelSerials);

     */

    /**
     * The Movies Genre List.
     */


    // We have two  main category. (third one is left for example addition)
    String[] movies = new String[]{"Horror", "Action", "Thriller/Drama"};

    /**
     * The Games Genre List.
     */
    String[] games = new String[]{"Fps", "Moba", "Rpg", "Racing"};

    /**
     * The Serials Genre List.
     */
    // String[] serials = new String[]{"Crime", "Family", "Comedy"};


    /**
     * The Horror movie list.
     */
    // movies category has further genres
    String[] horror = new String[]{"Conjuring", "Insidious", "The Ring"};
    /**
     * The Action Movies List.
     */
    String[] action = new String[]{"Jon Wick", "Die Hard", "Fast 7", "Avengers"};
    /**
     * The Thriller Movies List.
     */
    String[] thriller = new String[]{"Imitation Game", "Tinker, Tailer, Soldier, Spy", "Inception", "Manchester by the Sea"};


    /**
     * The Fps games.
     */
    // games category has further genres
    String[] fps = new String[]{"CS: GO", "Team Fortress 2", "Overwatch", "Battlefield 1", "Halo II", "Warframe"};
    /**
     * The Moba games.
     */
    String[] moba = new String[]{"Dota 2", "League of Legends", "Smite", "Strife", "Heroes of the Storm"};
    /**
     * The Rpg games.
     */
    String[] rpg = new String[]{"Witcher III", "Skyrim", "Warcraft", "Mass Effect II", "Diablo", "Dark Souls", "Last of Us"};
    /**
     * The Racing games.
     */
    String[] racing = new String[]{"NFS: Most Wanted", "Forza Motorsport 3", "EA: F1 2016", "Project Cars"};

    // serials genre list
    /*String[] crime = new String[]{"CSI: MIAMI", "X-Files", "True Detective", "Sherlock (BBC)", "Fargo", "Person of Interest"};

    String[] family = new String[]{"Andy Griffith", "Full House", "The Fresh Prince of Bel-Air", "Modern Family", "Friends"};

    String[] comedy = new String[]{"Family Guy", "Simpsons", "The Big Bang Theory", "The Office"};
*/


    /**
     * Datastructure for Third level movies.
     */
    LinkedHashMap<String, String[]> thirdLevelItemsSet = new LinkedHashMap<>();
    /**
     * Datastructure for Third level games.
     */
    LinkedHashMap<String, String[]> thirdLevelGames = new LinkedHashMap<>();

    /**
     * Datastructure for Third level Serials.
     */
    // LinkedHashMap<String, String[]> thirdLevelSerials = new LinkedHashMap<>();


    /**
     * The Second level.
     */
    List<String[]> secondLevel = new ArrayList<>();


    /**
     * The Data.
     */
    List<LinkedHashMap<String, String[]>> data = new ArrayList<>();
    public TextView categoryPoli;
    private JSONObject daysCode;
    private int previousGroup;
    private JSONObject expandableListLevel2ItemList;
    private ArrayList categoryServiceNameList;
    private TextView categoryDoctor;
    private TextView categoryDay;
    private TextView categoryAll;
    private TextView bookingEmpty;
    private ArrayList categoryDateList;
    private ArrayList doctorCategoryList;
    private CardView cardDoctor,cardAll,cardPoli,cardDay;
    private ProgressDialog pDialog;
    private SwipeRefreshLayout refreshLayout;
//    private ArrayList<String> expandableListLevel2ItemList;

    public Booking() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        SharedPrefManager.getInstance(getActivity()).expandFirstRowAt(-1);
//        SharedPrefManager.getInstance(getActivity()).expandSecondRowAt(-1);
        SharedPrefManager.getInstance(getActivity()).clearToExpand();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDates();
        setSchedule();
        new updateContent().execute();
        Log.e("jam",System.currentTimeMillis()+" ");
    }

    public class updateContent extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HashMap<String,String> param=new HashMap<>();
            // "rspku_mobile" is the variable of php POST method to fetch particular data
            // following by the php POST method value "prefetch data"
            param.put("rspku_mobile","fetch data");

            // create a new class to handle request to API
            RequestHandler reqH=new RequestHandler();
            // send request to the server through API and catch the response
            // "Config.HOME is the link server with particular php POST value to reach the API
            String response = reqH.sendPostRequest(Config.FEATURES, param);

            Log.e("fetching", String.valueOf(response));

            JSONObject json = null;
            String galery = null;
            String article = null;
            String services = null;

            try {
                // generate json object from string stored in variable response
                json = new JSONObject(String.valueOf(response));

                // get and store object's value of article in shared preferences variable
                article = json.getJSONArray("article").toString().replace("localhost",Config.IP);
                if(article!=null)
                SharedPrefManager.getInstance(getActivity()).storeArticle(article);
                Log.e("fetchArticle","booking"+SharedPrefManager.getInstance(getActivity()).getArticle());

                // get and store object's value of gallery in shared preferences variable
                galery = json.getJSONArray("gallery").toString().replace("localhost",Config.IP);
                if(galery!=null)
                    SharedPrefManager.getInstance(getActivity()).storeGallery(galery);
                Log.e("fetchGallery","booking"+SharedPrefManager.getInstance(getActivity()).getGallery());

                services = json.getJSONObject("features").toString().replace("localhost",Config.IP);
                if(services!=null)
                    SharedPrefManager.getInstance(getActivity()).storeFeaturesData(services);
                Log.e("service","booking"+SharedPrefManager.getInstance(getActivity()).getServices());
                Log.e("doctor","booking"+SharedPrefManager.getInstance(getActivity()).getDoctorList());
                Log.e("schedule","booking"+SharedPrefManager.getInstance(getActivity()).getServicesSchedule());
                Log.e("dataHari","booking"+SharedPrefManager.getInstance(getActivity()).getDayData());
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

        }
    }


    @Override
    public void onPause() {
        super.onPause();
//        SharedPrefManager.getInstance(getActivity()).expandFirstRowAt(-1);
//        SharedPrefManager.getInstance(getActivity()).expandSecondRowAt(-1);
        SharedPrefManager.getInstance(getActivity()).clearToExpand();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSchedule();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.drawer_booking, container, false);

        HorizontalScrollView categories = (HorizontalScrollView)view.findViewById(R.id.categories);

        categoryPoli = (TextView)view.findViewById(R.id.poli);
        categoryDoctor = (TextView)view.findViewById(R.id.doctor);
        categoryDay = (TextView)view.findViewById(R.id.day);
        categoryAll = (TextView)view.findViewById(R.id.all);
        bookingEmpty = (TextView)view.findViewById(R.id.booking_empty);

        cardAll = (CardView)view.findViewById(R.id.card_all);
        cardPoli = (CardView)view.findViewById(R.id.card_poli);
        cardDay = (CardView)view.findViewById(R.id.card_day);
        cardDoctor = (CardView)view.findViewById(R.id.card_doctor);

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.fui_bgTwitter)
                ,getResources().getColor(R.color.fui_bgPhone)
                ,getResources().getColor(R.color.colorPrimaryDark));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSchedule();
            }
        });

        categoryPoli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCategoryListItem(0, categoryServiceNameList);
            }
        });
        categoryDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCategoryListItem(1, categoryDateList);
            }
        });
        categoryDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCategoryListItem(2, doctorCategoryList);
            }
        });
        categoryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDates();
                setSchedule();

            }
        });

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandible_listview);

//        setDates();
//        setSchedule();

//        categoryPoli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        setSchedule();

//        // second level category names (genres)
//        secondLevel.add(movies);
//        secondLevel.add(games);
//        Log.e("secondLevel", String.valueOf(secondLevel));
//        // secondLevel.add(serials);
//
//        // movies category all data
//        thirdLevelItemsSet.put(movies[0], horror);
//        thirdLevelItemsSet.put(movies[1], action);
//        thirdLevelItemsSet.put(movies[2], thriller);
//
//        Log.e("thirdLevelItemsSet", String.valueOf(thirdLevelItemsSet));
//
//
//        // games category all data
//        thirdLevelGames.put(games[0], fps);
//        thirdLevelGames.put(games[1], moba);
//        thirdLevelGames.put(games[2], rpg);
//        thirdLevelGames.put(games[3], racing);
//        Log.e("thirdLevelGames", String.valueOf(thirdLevelGames));
//
//
//        // serials category all data
//      /*  thirdLevelSerials.put(serials[0], crime);
//        thirdLevelSerials.put(serials[1], family);
//        thirdLevelSerials.put(serials[2], comedy);
//*/
//
//
//        // all data
//        data.add(thirdLevelItemsSet);
//        data.add(thirdLevelGames);
//        Log.e("data", String.valueOf(data));
//        //data.add(thirdLevelSerials);


        // expandable listview
//        expandableListView = (ExpandableListView) view.findViewById(R.id.expandible_listview);

//        // parent adapter
//        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parent, secondLevel, data, categoryPoli);
//
//
//        // set adapter
//        expandableListView.setAdapter( threeLevelListAdapterAdapter );

        previousGroup = -1;
        // OPTIONAL : Show one list at a time
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
//                expandableListView.getChildAt(groupPosition).get
//                (ImageView)findViewById(R.id.ivGroupIndicator)).setBackgroundResource(R.drawable.indicator_group_up);
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
//                SharedPrefManager.getInstance(getActivity()).expandFirstRow(-1);
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if(groupPosition==previousGroup) {
//                    SharedPrefManager.getInstance(getActivity()).expandFirstRow(-1);
                }
            }
        });

        if(User.isDoctor(getActivity())) {
            categories.setVisibility(View.GONE);
            bookingListForDoctor();
            Toast.makeText(getActivity(),"aku dokter",Toast.LENGTH_LONG).show();
        }

        setDates();
        setSchedule();

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    SharedPrefManager.getInstance(getActivity()).clearToExpand();
                    setSchedule();
                }
            }
        });
        
        return view;
    }

    private void dialogCategoryListItem(final int type, ArrayList items){

        String[] titles = new String[]{"Jenis Layanan","Hari/Tanggal","Dokter"};
        final CardView[] category = new CardView[]{cardPoli,cardDay,cardDoctor};

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptView = li.inflate(R.layout.promt_category_list_item,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setView(promptView);

        dialogBuilder.setTitle(titles[type])
                .setCancelable(true);

        final AlertDialog dialog = dialogBuilder.create();

        final ListView list = (ListView)promptView.findViewById(R.id.category_item_list);

//        if(type==2){
//            String doctor
//            for(String unip: items){
//
//            }
//        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
//        }

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(type==0){
                    String item = list.getAdapter().getItem(position).toString();
                    setSchedulePoliBase(item);
                }else if(type==1){
                    String item = list.getAdapter().getItem(position).toString();
//                    Toast.makeText(getActivity(),"daybase "+item,Toast.LENGTH_LONG).show();
                    setScheduleDateBase(item);
                }else if(type==2){
                    String item = list.getAdapter().getItem(position).toString();
                    setScheduleDoctorBase(item);
                }
                cardAll.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                for(CardView ctgry : category){
                    if(ctgry==category[type]) {
                        ctgry.setCardBackgroundColor(Color.GREEN);
                    }else{
                        ctgry.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setSchedule(){

        cardAll.setCardBackgroundColor(Color.GREEN);
        cardPoli.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        cardDoctor.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        cardDay.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

        setSchedule(null,null,null);
    }

    private void setSchedulePoliBase(String poliName){
        setSchedule(poliName,null,null);
    }

    private void setScheduleDateBase(String date){
        setSchedule(null,date,null);
    }

    private void setScheduleDoctorBase(String doctor){
        setSchedule(null,null,doctor);
    }


    private void setSchedule(final String filterServiceName, final String date, final String doctorName) {

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Memuat...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        refreshLayout.setRefreshing(true);

        JSONObject schedule = null;
        try {
            schedule = new JSONObject(SharedPrefManager.getInstance(getActivity()).getServicesSchedule());
            Log.e("schedule",schedule.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(schedule!=null){
            secondLevel = new ArrayList<>();
            data = new ArrayList<>();
            thirdLevelItemsSet = new LinkedHashMap<>();

            categoryServiceNameList = new ArrayList();
            categoryDateList = new ArrayList();
            doctorCategoryList = new ArrayList();

//            String[] parents = new String[schedule.length()];
            final ArrayList<String> parents = new ArrayList<>();

            final Iterator serviceKey =schedule.keys();
//            int index=0;

            final JSONObject finalSchedule = schedule;
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    })
//                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int index=0;
                            while(serviceKey.hasNext()){
                                String serviceName = (String) serviceKey.next();


                                // get index to expand of first rows
                                if (serviceName.equals(SharedPrefManager.getInstance(getActivity()).getFirstRowNameToExpand())) {


                                    SharedPrefManager.getInstance(getActivity()).expandFirstRowAt(parents.size());
//                                    SharedPrefManager.getInstance(getActivity()).clearToExpand();

                                    Log.e("expandservicename","servicename"+SharedPrefManager.getInstance(getActivity()).getFirstRowNameToExpand()+serviceName.equals(SharedPrefManager.getInstance(getActivity()).getFirstRowNameToExpand())+SharedPrefManager.getInstance(getActivity()).getIndexFirstRowToExpand()
                                    );
                                }
//                                if(SharedPrefManager.getInstance(getActivity()).isDoctor())
//                                    if(dataSnapshot.child(serviceName).getValue()==null) continue;

                                // set searching list for service category button
                                if(!categoryServiceNameList.contains(serviceName))
                                    categoryServiceNameList.add(serviceName);
//                String[] dayOnPractice = new String[0];

                                // running when user select particular item on service category
                                // null if nothing selected (semua)
                                ArrayList<String> dayOnPractice = new ArrayList<>();
                                if(serviceName.equals(filterServiceName)|| filterServiceName ==null) {
//                    parents[index] = serviceName;
//                    parents.add(serviceName);
                                    JSONObject days = null;
                                    try {
//                        days = schedule.getJSONObject(parents[index]);
                                        days = finalSchedule.getJSONObject(serviceName);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Iterator day = days.keys();
//                    dayOnPractice = new String[days.length()];
//                    ArrayList<String> dayOnPractice = new ArrayList<>();
                                    int indexDay = 0;

                                    while (day.hasNext()) {

                                        String dayKey = (String) day.next();

                                        String dates = null;
                                        String dateDoctor = null;
                                        try {

                                            // dates = "dayName, dd-MM-yyy" eg "sunday, 10-10-1010"
                                            dates = expandableListLevel2ItemList.getString(dayKey);
                                            String[] d = dates.replace(" ","").split(",");
                                            String[] dMy = d[1].split("-");
                                            String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
                                            dateDoctor = yMd;

                                            Log.e("dateDoctor",dateDoctor);
//                                            if(!categoryDateList.contains(dates))
//                                                categoryDateList.add(dates);

                                        } catch (JSONException e) {
                                        }

                                        if(User.isDoctor(getActivity())) {
                                            Log.e("continue1", date+ " "+ dateDoctor + " " + serviceName + " " + dataSnapshot.child(dateDoctor).child(serviceName).getValue());
                                            if (dataSnapshot.child(dateDoctor).child(serviceName).getValue() == null)
                                                continue;
                                        }
                                        Log.e("continue2",dateDoctor+" "+ serviceName +" "+dataSnapshot.child(dateDoctor).child(serviceName).getValue());
                                        if(dates!=null&&!categoryDateList.contains(dates))
                                            categoryDateList.add(dates);
//                        if(!categoryDateList.contains(dates))
//                            categoryDateList.add(dates);

//                        List<String> list = new ArrayList<>();
                                        String secondLv = index + ":" + dates;
//                            dayOnPractice[indexDay] = index + ":" + dates;
                                        if(dates.equals(date)||date==null) {
                                            Log.e("datebase","day "+date+"="+dates+" "+dates.equals(date));
                                            Log.e("datebase","masuk");
//                            dayOnPractice.add(secondLv);

//                    if(parents[index].equals(SharedPrefManager.getInstance(getActivity()).getFirstRowToExpand())
//                        &&dayOnPractice[indexDay].equals(SharedPrefManager.getInstance(getActivity()).getSecondRowToExpand())){
//                            if (parents[index].equals(SharedPrefManager.getInstance(getActivity()).getFirstRowToExpand())) {
                                            Log.e("secondRowName",indexDay+" "+dates+" "+SharedPrefManager.getInstance(getActivity()).getSecondRowNameToExpand());
                                            //TODO fix
                                            if (dates.equals(SharedPrefManager.getInstance(getActivity()).getSecondRowNameToExpand())) {
                                                Log.e("secondRow",indexDay+"");
                                                SharedPrefManager.getInstance(getActivity()).expandSecondRowAt(indexDay);
//                                                SharedPrefManager.getInstance(getActivity()).clearToExpand();
                                            }

                                            JSONArray shifts = null;
                                            try {
                                                shifts = days.getJSONArray(dayKey);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
//                            String[] time = null;
                                            final List<String> list = new ArrayList<>();

                                            for (int i = 0; i < shifts.length(); i++) {
                                                String[] open = null, close = null;
                                                String doctor = null, doctorNip= null;
                                                try {
                                                    open = shifts.getJSONObject(i).getString("buka").split(":");
                                                    close = shifts.getJSONObject(i).getString("tutup").split(":");
                                                    doctorNip = shifts.getJSONObject(i).getString("dokter");

                                                    JSONObject json = new JSONObject(SharedPrefManager.getInstance(getActivity()).getDoctorList());

                                                    Log.e("nipdoctor",doctorNip+SharedPrefManager.getInstance(getActivity()).getDoctorList());

                                                    doctor = json.getString(doctorNip);

                                                    if (!doctorCategoryList.contains(doctor))
                                                        doctorCategoryList.add(doctor);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
//                                if (open != null) {
                                                if(doctorName==null||doctor.equals(doctorName)) {
//                                    Log.e(serviceName + " " + dayOnPractice[indexDay],
//                                            Integer.parseInt(open[0]) + "" + Integer.parseInt(open[1]) + " - " +
//                                                    Integer.parseInt(close[0]) + "" + Integer.parseInt(close[1]) + " " + doctor);
                                                    Log.e(serviceName + " " + secondLv,
                                                            Integer.parseInt(open[0]) + "" + Integer.parseInt(open[1]) + " - " +
                                                                    Integer.parseInt(close[0]) + "" + Integer.parseInt(close[1]) + " " + doctor);
//                            List<String> list = new ArrayList<>();
                                                    for (int h = Integer.parseInt(open[0]); h <= Integer.parseInt(close[0]); h++) {
//                                    for (int h = Integer.parseInt(open[0]); h <= 24; h++) {
                                                        for (int m = Integer.parseInt(open[1]); m < 60; m += 15) {
                                                            final String time =(h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
//                                            if (((h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m)).equals(close[0] + ":" + close[1])) {


                                                            if (time.equals(close[0] + ":" + close[1])) {
                                                                break;
                                                            }

                                                            if(User.isDoctor(getActivity())) {
                                                                Log.e("continue3",dateDoctor+" "+ serviceName +" "+time+" "+dataSnapshot.child(dateDoctor).child(serviceName).child(time).getValue());
                                                                String nipDoctor = SharedPrefManager.getInstance(getActivity()).getDoctorNip();
                                                                if (dataSnapshot.child(dateDoctor).child(serviceName).child(time).getValue() == null)
                                                                    continue;
                                                                else if (!nipDoctor.equals(dataSnapshot.child(dateDoctor).child(serviceName).child(time).child("doctor").getValue(String.class)))
                                                                    continue;
                                                            }

                                                            //checking is current user a doctor
//                                            Log.e("isDoctor", SharedPrefManager.getInstance(getActivity()).isDoctor()+" " +
//                                                    SharedPrefManager.getInstance(getActivity()).getDoctorNip());
//                                            if(SharedPrefManager.getInstance(getActivity()).isDoctor()){
//                                                //checking is the booking exist
//                                                String[] d = dates.replace(" ","").split(",");
//                                                final String finalDoctor = doctor;
////                                                Log.e("filter",d[1]+" "+serviceName+" "+time);
//                                                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
//                                                        .child(d[1])
//                                                        .child(serviceName)
//                                                        .child(time)
//                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                if(dataSnapshot.getValue()==null);
//                                                                else {
//                                                                    Log.e("filter2",dataSnapshot.child("doctor").getValue(String.class)+" "+
//                                                                            SharedPrefManager.getInstance(getActivity())
//                                                                                    .getDoctorNip().equals(dataSnapshot.child("doctor").getValue(String.class)));
//                                                                    if(SharedPrefManager.getInstance(getActivity())
//                                                                    .getDoctorNip().equals(dataSnapshot.child("doctor").getValue(String.class))) {
//                                                                        list.add(time + "," + finalDoctor);
//                                                                        Log.e("jamx", time + "," + finalDoctor+" "+ list.toString());
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//                                            }
//                                            else {

//                                            list.add((h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + "," + doctor);
//                                            Log.e("jam",(h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + "," + doctor);
                                                            if(User.isDoctor(getActivity())){
                                                                String patientName = dataSnapshot.child(dateDoctor).child(serviceName).child(time).child("name").getValue(String.class);
                                                                Log.e("levelTiga",patientName+"");
                                                                list.add(time + "," + patientName);
                                                            }else {
                                                                list.add(time + "," + doctor);
                                                            }
                                                            Log.e("jam",time + "," + doctor);
//                                            }
//                                    list.add((h<10?"0"+h:h)+":");
                                                        }
                                                    }
//                            Log.e(parents[index]+" "+dayOnPractice[indexDay],list.toString()+" "+doctor);

                                                    //third level
//                            time = list.toArray(new String[list.size()]);
                                                }
//                                }
                                            }

                                            Log.e("checkingList", list.toString());

                                            Log.e(serviceName + " " + secondLv, list.toString());
                                            if (list.size() > 0) {
//                            if (dates.equals(date)||date==null)
                                                Log.e(serviceName + " " + secondLv, list.toString() + " set thirdlevel");
                                                dayOnPractice.add(secondLv);
                                                thirdLevelItemsSet.put(secondLv, list.toArray(new String[list.size()]));
                                            }
                                        }
                                        indexDay++;
                                    }
                                }

                                Log.e("service name", serviceName +" "+dayOnPractice.toString());

                                if(dayOnPractice.size()>0) {
                                    parents.add(serviceName);
                                    secondLevel.add(dayOnPractice.toArray(new String[dayOnPractice.size()]));
                                    data.add(thirdLevelItemsSet);
//                                    index++;
                                }
                                index++;
//                Log.e("json_schedule_children",dayOnPractice[days.length()-1]);
                            }
                            parent = parents.toArray(new String[parents.size()]);
//            Log.e("json_schedule",schedule.toString());
//            Log.e("json_schedule_parent",parent[0]+parent[1]+parent[2]+parent[schedule.length()-1]);

                            // parent adapter
                            ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parent, secondLevel, data, expandableListLevel2ItemList);
//            ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parents.toArray(new String[parents.size()]), secondLevel, data, expandableListLevel2ItemList);

                            // set adapter

                            Log.e("groupCount",threeLevelListAdapterAdapter.getGroupCount()+" ");
                            if(threeLevelListAdapterAdapter.getGroupCount()==0){
                                refreshLayout.setVisibility(View.GONE);
                                bookingEmpty.setVisibility(View.VISIBLE);
                            }else {

                                bookingEmpty.setVisibility(View.GONE);

                                expandableListView.setAdapter(threeLevelListAdapterAdapter);

                                Log.e("expandingIndex", SharedPrefManager.getInstance(getActivity()).getIndexFirstRowToExpand() + " " +
                                        SharedPrefManager.getInstance(getActivity()).getIndexSecondRowToExpand());

//                                SharedPrefManager.getInstance(getActivity()).clearToExpand();
//                                Toast.makeText(getActivity(),SharedPrefManager.getInstance(getActivity()).getIndexFirstRowToExpand()+" "+SharedPrefManager.getInstance(getActivity()).getFirstRowNameToExpand()+" "+SharedPrefManager.getInstance(getActivity()).getSecondRowNameToExpand(),Toast.LENGTH_LONG).show();

                                if (SharedPrefManager.getInstance(getActivity()).getIndexFirstRowToExpand() != -1) {
                                    expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getIndexFirstRowToExpand());
                                    SharedPrefManager.getInstance(getActivity()).expandFirstRowAt(-1);

                                }

                                pDialog.dismiss();

//                            if(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow()!=-1){
//                                expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow());
//                            }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

//            while(serviceKey.hasNext()){
//                String getPoli = (String)serviceKey.next();
//
//                if(!categoryServiceNameList.contains(getPoli))
//                    categoryServiceNameList.add(getPoli);
//
////                String[] dayOnPractice = new String[0];
//                ArrayList<String> dayOnPractice = new ArrayList<>();
//                if(getPoli.equals(filterServiceName)||filterServiceName==null) {
////                    parents[index] = getPoli;
////                    parents.add(getPoli);
//
//                    JSONObject days = null;
//                    try {
////                        days = schedule.getJSONObject(parents[index]);
//                        days = schedule.getJSONObject(getPoli);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    Iterator day = days.keys();
////                    dayOnPractice = new String[days.length()];
////                    ArrayList<String> dayOnPractice = new ArrayList<>();
//                    int indexDay = 0;
//
//                    while (day.hasNext()) {
//
//                        String dayKey = (String) day.next();
//
//                        String dates = null;
//                        try {
//                            dates = expandableListLevel2ItemList.getString(dayKey);
//
//                            if(!categoryDateList.contains(dates))
//                                categoryDateList.add(dates);
//
//                        } catch (JSONException e) {
//                        }
//
////                        if(!categoryDateList.contains(dates))
////                            categoryDateList.add(dates);
//
////                        List<String> list = new ArrayList<>();
//
//                        String secondLv = index + ":" + dates;
//
////                            dayOnPractice[indexDay] = index + ":" + dates;
//
//
//                        if(dates.equals(date)||date==null) {
//                            Log.e("datebase","day "+date+"="+dates+" "+dates.equals(date));
//                            Log.e("datebase","masuk");
//
//
////                            dayOnPractice.add(secondLv);
//
////                    if(parents[index].equals(SharedPrefManager.getInstance(getActivity()).getFirstRowToExpand())
////                        &&dayOnPractice[indexDay].equals(SharedPrefManager.getInstance(getActivity()).getSecondRowToExpand())){
////                            if (parents[index].equals(SharedPrefManager.getInstance(getActivity()).getFirstRowToExpand())) {
//                            if (getPoli.equals(SharedPrefManager.getInstance(getActivity()).getFirstRowToExpand())) {
//
//                                Toast.makeText(getActivity(), index + " " + indexDay, Toast.LENGTH_LONG).show();
//                                SharedPrefManager.getInstance(getActivity()).expandFirstRow(index);
////                        SharedPrefManager.getInstance(getActivity()).expandSecondRow(indexDay);
//
//                                SharedPrefManager.getInstance(getActivity()).clearToExpand();
//                            }
//
//                            JSONArray shifts = null;
//                            try {
//                                shifts = days.getJSONArray(dayKey);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
////                            String[] time = null;
//
//                            final List<String> list = new ArrayList<>();
//
//                            for (int i = 0; i < shifts.length(); i++) {
//                                String[] open = null, close = null;
//                                String doctor = null;
//                                try {
//                                    open = shifts.getJSONObject(i).getString("buka").split(":");
//                                    close = shifts.getJSONObject(i).getString("tutup").split(":");
//                                    doctor = shifts.getJSONObject(i).getString("dokter");
//
//                                    if (!doctorCategoryList.contains(doctor))
//                                        doctorCategoryList.add(doctor);
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
////                                if (open != null) {
//                                if(doctorName==null||doctor.equals(doctorName)) {
////                                    Log.e(getPoli + " " + dayOnPractice[indexDay],
////                                            Integer.parseInt(open[0]) + "" + Integer.parseInt(open[1]) + " - " +
////                                                    Integer.parseInt(close[0]) + "" + Integer.parseInt(close[1]) + " " + doctor);
//                                    Log.e(getPoli + " " + secondLv,
//                                            Integer.parseInt(open[0]) + "" + Integer.parseInt(open[1]) + " - " +
//                                                    Integer.parseInt(close[0]) + "" + Integer.parseInt(close[1]) + " " + doctor);
////                            List<String> list = new ArrayList<>();
//                                    for (int h = Integer.parseInt(open[0]); h <= Integer.parseInt(close[0]); h++) {
////                                    for (int h = Integer.parseInt(open[0]); h <= 24; h++) {
//                                        for (int m = Integer.parseInt(open[1]); m < 60; m += 15) {
//                                            final String time =(h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
////                                            if (((h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m)).equals(close[0] + ":" + close[1])) {
//                                            if (time.equals(close[0] + ":" + close[1])) {
//                                                break;
//                                            }
//
//                                            //checking is current user a doctor
////                                            Log.e("isDoctor", SharedPrefManager.getInstance(getActivity()).isDoctor()+" " +
////                                                    SharedPrefManager.getInstance(getActivity()).getDoctorNip());
////                                            if(SharedPrefManager.getInstance(getActivity()).isDoctor()){
////                                                //checking is the booking exist
////                                                String[] d = dates.replace(" ","").split(",");
////                                                final String finalDoctor = doctor;
//////                                                Log.e("filter",d[1]+" "+getPoli+" "+time);
////                                                FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
////                                                        .child(d[1])
////                                                        .child(getPoli)
////                                                        .child(time)
////                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
////                                                            @Override
////                                                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                                                if(dataSnapshot.getValue()==null);
////                                                                else {
////                                                                    Log.e("filter2",dataSnapshot.child("doctor").getValue(String.class)+" "+
////                                                                            SharedPrefManager.getInstance(getActivity())
////                                                                                    .getDoctorNip().equals(dataSnapshot.child("doctor").getValue(String.class)));
////                                                                    if(SharedPrefManager.getInstance(getActivity())
////                                                                    .getDoctorNip().equals(dataSnapshot.child("doctor").getValue(String.class))) {
////                                                                        list.add(time + "," + finalDoctor);
////                                                                        Log.e("jamx", time + "," + finalDoctor+" "+ list.toString());
////                                                                    }
////                                                                }
////                                                            }
////
////                                                            @Override
////                                                            public void onCancelled(DatabaseError databaseError) {
////
////                                                            }
////                                                        });
////                                            }
////                                            else {
//
////                                            list.add((h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + "," + doctor);
////                                            Log.e("jam",(h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + "," + doctor);
//                                            list.add(time + "," + doctor);
//                                            Log.e("jam",time + "," + doctor);
////                                            }
////                                    list.add((h<10?"0"+h:h)+":");
//                                        }
//                                    }
////                            Log.e(parents[index]+" "+dayOnPractice[indexDay],list.toString()+" "+doctor);
//
//                                    //third level
////                            time = list.toArray(new String[list.size()]);
//                                }
////                                }
//                            }
//
//                            Log.e("checkingList", list.toString());
//
//                            Log.e(getPoli + " " + secondLv, list.toString());
//                            if (list.size() > 0) {
////                            if (dates.equals(date)||date==null)
//                                Log.e(getPoli + " " + secondLv, list.toString() + " set thirdlevel");
//                                dayOnPractice.add(secondLv);
//                                thirdLevelItemsSet.put(secondLv, list.toArray(new String[list.size()]));
//                            }
//                        }
//                        indexDay++;
//                    }
//                }
//
//                Log.e(getPoli, dayOnPractice.toString());
//
//                if(dayOnPractice.size()>0) {
//                    parents.add(getPoli);
//                    secondLevel.add(dayOnPractice.toArray(new String[dayOnPractice.size()]));
//                    data.add(thirdLevelItemsSet);
//                }
//                index++;
//
////                Log.e("json_schedule_children",dayOnPractice[days.length()-1]);
//            }
//
//            parent = parents.toArray(new String[parents.size()]);
//
////            Log.e("json_schedule",schedule.toString());
////            Log.e("json_schedule_parent",parent[0]+parent[1]+parent[2]+parent[schedule.length()-1]);
//
//            // parent adapter
//            ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parent, secondLevel, data, expandableListLevel2ItemList);
//
////            ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parents.toArray(new String[parents.size()]), secondLevel, data, expandableListLevel2ItemList);
//
//            // set adapter
//            expandableListView.setAdapter( threeLevelListAdapterAdapter );
//            if(SharedPrefManager.getInstance(getActivity()).getExpandedFirstRow()!=-1){
//                expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getExpandedFirstRow());
//            }
//
//            if(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow()!=-1){
////            expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow());
//            }


        }else{
            Toast.makeText(getActivity(),"terjadi kesalahan dalam memproses data",Toast.LENGTH_LONG).show();
        }

        refreshLayout.setRefreshing(false);
//        // parent adapter
//        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parent, secondLevel, data, expandableListLevel2ItemList);
//
//
//        // set adapter
//        expandableListView.setAdapter( threeLevelListAdapterAdapter );
//        if(SharedPrefManager.getInstance(getActivity()).getExpandedFirstRow()!=-1){
//            expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getExpandedFirstRow());
//        }
//
//        if(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow()!=-1){
////            expandableListView.expandGroup(SharedPrefManager.getInstance(getActivity()).getExpandedSecondRow());
//        }
    }

    private void setDates() {
        expandableListLevel2ItemList = new JSONObject();

        Calendar cal = Calendar.getInstance();
//        cal.set(year,month,day);
//        cal.add(cal.DATE,2);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat day = new SimpleDateFormat("EEEE");

        try {
            daysCode = new JSONObject(SharedPrefManager.getInstance(getActivity()).getDayData());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<7;i++){
            String dateFormat = sdf.format(cal.getTime());
            String item = dateFormat;
//            try {
            try {
                item = daysCode.getString(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)))+", "+dateFormat;
//                item = dateFormat;
                expandableListLevel2ItemList.put(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)),item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            } catch (JSONException e) {
//                expandableListLevel2ItemList.add(cal.get(Calendar.DAY_OF_WEEK)+", "+item);
//            }
            cal.add(cal.DATE,1);
        }

//        categoryPoli.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, expandableListLevel2ItemList));
    }

    public void bookingListForDoctor(){
//        if(SharedPrefManager.getInstance(getActivity()).isDoctor()){
        Log.e("filter","bookingListForDoctor");
            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                    .orderByChild("doctor").equalTo(SharedPrefManager.getInstance(getActivity()).getDoctorNip())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot==null);
                            else{
//                                Log.e(SharedPrefManager.getInstance(getActivity()).getDoctorNip(),"bookingListForDoctor "+dataSnapshot.child("04-03-2018").getValue().toString());
                                for(DataSnapshot date : dataSnapshot.getChildren()){
                                    Log.e(SharedPrefManager.getInstance(getActivity()).getDoctorNip(),date.getKey().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//        }
    }

//    public void setPoliItem(){
//        JSONObject poliData = null;
//        try {
//            poliData = new JSONObject(SharedPrefManager.getInstance(getActivity()).getServiceData());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if(poliData!=null) {
//            Iterator poli = poliData.keys();
//
//            List<String> spinnerItem = new ArrayList<>();
//            while (poli.hasNext()) {
//                try {
//                    String poliName = poliData.getString((String) poli.next());
//                    spinnerItem.add(poliName);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        categoryPoli.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,spinnerItem));
//        }
//    }

}
