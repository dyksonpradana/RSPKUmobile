package com.rspkumobile.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rspkumobile.R;
import com.rspkumobile.app.Config;
import com.rspkumobile.model.NotifHandler;
import com.rspkumobile.other.Helper;
import com.rspkumobile.other.RequestHandler;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;
import com.rspkumobile.util.Remainder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class SecondLevelAdapter extends BaseExpandableListAdapter {

    private final JSONObject daysCode;

    private Activity activity;

    String parent;

    int firstRowPosition;

    List<String[]> data;

    String[] headers;


    public SecondLevelAdapter(Activity activity, int firstRowPosition, String firstRow, String[] headers, List<String[]> data, JSONObject dCode) {
        this.activity = activity;
        this.parent = firstRow;
        this.data = data;
        this.headers = headers;
        this.daysCode = dCode;
        this.firstRowPosition = firstRowPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {

        return headers[groupPosition];
    }

    @Override
    public int getGroupCount() {

        return headers.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.expandable_list_row_second, null);
        TextView text = (TextView) convertView.findViewById(R.id.rowSecondText);
        String groupText = getGroup(groupPosition).toString();

//        String dates = null;

        String[] secondLvText = groupText.split(":");

//        try {
//            dates = daysCode.getString(groupText);
        text.setText(secondLvText[1]);
//        } catch (JSONException e) {
//            text.setText(groupText);
//        }

        String[] date = secondLvText[1].replace(" ","").split(",");

//        if((SharedPrefManager.getInstance(activity).getBookingPoliName(this.parent)+SharedPrefManager.getInstance(activity).getBookingDate(this.parent)).equals(this.parent +date[1]))convertView.setBackgroundColor(Color.GREEN);

        setSecondLvBgColor(date[1],this.parent,convertView);

        ImageView indicator2 = (ImageView) convertView.findViewById(R.id.day_indicator);

        String currentDay = null;

        try {
            currentDay = daysCode.getString(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(secondLvText[1].equals(currentDay)){
//            text.setTextColor(Color.GREEN);
            indicator2.setVisibility(View.VISIBLE);
        }

        ImageView indicator = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);

        if(isExpanded){
            indicator.setBackgroundResource(R.drawable.indicator_group2_up);
        }else indicator.setBackgroundResource(R.drawable.indicator_group2_down);

//        setTextColour(text,0);

//        this.spinnerSelectedItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                setTextColour(text,position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
////                setTextColour(text,0);
//            }
//        });

        return convertView;
    }

    private void setSecondLvBgColor(String date, String poliName, final View secondLvView) {
        //TODO balik date
        String[] dMy = date.split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poliName)
//                .orderByChild("uid").equalTo(User.getUid())
                .orderByChild("uid").equalTo(User.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()==null){
                            secondLvView.setBackgroundColor(-1);

                        }else{
                            for(DataSnapshot data: dataSnapshot.getChildren()){
//                                if((Config.BOOKING_STATUS_TYPE_EX[0]).equals(data.child("status").getValue(String.class)))
                                    secondLvView.setBackgroundColor(Color.GREEN);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTextColour(TextView text, int position) {
//        String[] item = spinnerSelectedItem.getAdapter().getItem(position).toString().replace(" ","").split(",");
//
//        Toast.makeText(this.activity,position+" "+item[0],Toast.LENGTH_LONG).show();
//
//        if(item[0].equals(text.getText().toString())){
//            text.setTextColor(Color.GREEN);
//        }else text.setTextColor(Color.BLACK);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        String[] childData;

        childData = data.get(groupPosition);


        return childData[childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.expandable_list_row_third, null);

        final TextView textView = (TextView) convertView.findViewById(R.id.rowThirdText);

        final int secondLevelPosition = groupPosition;
        String[] secondLvText = headers[groupPosition].split(":");
        final String[] dates = secondLvText[1].replace(" ","").split(",");

        String[] childArray=data.get(groupPosition);

//        String text = childArray[childPosition];
        String[] text = childArray[childPosition].split(",");

        final String groupCode = getGroup(groupPosition).toString();

        final String time = text[0];
//        final String name = (text.replace(" ","").split(","))[1];
        final String name = text[1];

//        if(User.isDoctor(activity))
//            setTextBookingListLv3Doctor(textView,this.parent,dates[1],time);
//        else
            textView.setText(time+" ("+ name +")");

        final String poliName = this.parent;

//        try {
//            secondT = daysCode.getString(headers[groupPosition]);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//        final View finalConvertView = convertView;

        setThirdLvBgColor(dates[1],poliName,time,convertView);


        if(isExpired(dates[1],time,0)){
            textView.setTextColor(Color.RED);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "waktu yang anda pilih kadaluarsa",Toast.LENGTH_LONG).show();
                }
            });
        }else if(isExpired(dates[1],time,3600000)){
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertBookingOffset(3600000);
                }
            });
        }
        else {
//        if(true){
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(activity, parent + " " + dates[0] + " " + thirdT, Toast.LENGTH_LONG).show();

                    int ColorId = -1;
                    ColorDrawable viewColor = (ColorDrawable)v.getBackground();

                    if(viewColor!=null)
                        ColorId = viewColor.getColor();

                    Log.e("color",  ColorId + " " + Color.GRAY);

//                    Log.e("MILIES clr", v.getSolidColor() + " " + Color.DKGRAY+ " " + Color.GREEN+ " " +  ColorId);
//
//                    Log.e("MILIES txt", SharedPrefManager.getInstance(activity).getBookingPoliName(parent)+
//                            SharedPrefManager.getInstance(activity).getBookingDate(parent)+
//                            SharedPrefManager.getInstance(activity).getBookingTime(parent) + " " + parent+dates[1]+thirdT);

                    if(ColorId==Color.parseColor("#400DFF00")) {
                        if(User.isDoctor(activity))
                            Toast.makeText(activity,"pasien sudah menunggu",Toast.LENGTH_LONG).show();
                        else {
                            //TODO BALIK DATE
                            String[] dMy = dates[1].split("-");
                            String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                                    .child(yMd)
                                    .child(poliName)
                                    .child(time)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) {
                                            } else showBooking(poliName, dates, name, time);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
//                        Toast.makeText(activity, "pesanan anda",Toast.LENGTH_LONG).show();

//                        successDialog(SecondLevelAdapter.this.parent,
//                                dates,
//                                thirdT,
//                                SharedPrefManager.getInstance(activity).getBookingName(SecondLevelAdapter.this.parent));
                    }
//                    else if((SharedPrefManager.getInstance(activity).getBookingPoliName(parent)+
//                            SharedPrefManager.getInstance(activity).getBookingDate(parent))
//                            .equals(parent+dates[1])) {
//                        //TODO: ubah pesanan
//                    }
//                    else if(SharedPrefManager.getInstance(activity).getBookingPoliName(SecondLevelAdapter.this.parent)!=null){
////                        if((SharedPrefManager.getInstance(activity).getBookingPoliName(SecondLevelAdapter.this.parent)).equals(SecondLevelAdapter.this.parent)) {
//                            Toast.makeText(activity, "have", Toast.LENGTH_LONG).show();
//                            alertHasBooking();
////                        }
//                    }
                    else if (ColorId==Color.GRAY){
//                        alertSelectedItemHasBooked();
                        if(User.isDoctor(activity))
                            Toast.makeText(activity,"booking sukses",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(activity,"sudah dipesan oleh orang lain",Toast.LENGTH_LONG).show();
                    }
                    else if(ColorId==Color.DKGRAY){
                        if(User.isDoctor(activity))
                            Toast.makeText(activity,"pasien tidak datang",Toast.LENGTH_LONG).show();
                        else{}
                    }
                    else{
                        // item with default color
                        // when about to book
                        if(User.isDoctor(activity));
                        else if(User.getUid()!=null) {
                            FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                                    .child(User.getUid())
                                    .child("booking")
                                    .child(poliName)
//                                    .orderByChild("date_time").equalTo(dates[0] + ", " + dates[1] + "_" + time)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) {
                                                checkOwnTime(poliName, dates, time, name);
//                                            dialogBooking(dates,time,name,groupCode);
                                            } else {
                                                Toast.makeText(activity, "have", Toast.LENGTH_LONG).show();
                                                //TODO check if in reservation this booking still exist
//                                                alertHasBooking(poliName, dates, name, time);
                                                String unipDoctor = dataSnapshot.child("doctor").getValue(String.class);

                                                JSONObject json = null;
                                                String doctorName = "";
                                                try {
                                                    json = new JSONObject(SharedPrefManager.getInstance(activity).getDoctorList());
                                                    Log.e("nipdoctor",unipDoctor+SharedPrefManager.getInstance(activity).getDoctorList());
                                                    doctorName = json.getString(unipDoctor);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                alertHasBooking(poliName, dataSnapshot.child("date").getValue(String.class).replace(" ","").split(","),
                                                        doctorName, time);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else Helper.requireLogin(activity);

                    }
                }
            });
        }

        return convertView;
    }

    private void setTextBookingListLv3Doctor(final TextView textView, String service, String date, final String time) {
        //TODO BALIK DATE
//        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
//                .child(date).child(service).child(time)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String patientName = dataSnapshot.child("name").getValue(String.class);
//                        textView.setText(time+" - "+patientName);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    private void alertBookingOffset(int i) {
        float offsetF = (float)i/3600000;
        int offsetH = i/3600000;
        String hour = "";

        if((i%3600000)==0) hour = String.valueOf(offsetH);
        else String.valueOf(offsetF);

        new AlertDialog.Builder(activity)
                .setTitle("Perhatian")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("booking hanya dapat dilakukan dari "+hour+" jam sebelum waktu yang dipilih")
                .setNegativeButton("KEMBALI",null)
                .show();

    }

    private void showBooking(final String poliName, final String[] dates, final String doctorName, final String time) {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .child(poliName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        successDialog(poliName,
//                                dataSnapshot.child("date").getValue(String.class).replace(" ","").split(","),
//                                dataSnapshot.child("time").getValue(String.class),
//                                dataSnapshot.child("doctorName").getValue(String.class),
//                                dataSnapshot.child("name").getValue(String.class));

                        successDialog(poliName,
                                dates,
                                dataSnapshot.child("time").getValue(String.class),
                                doctorName,
                                dataSnapshot.child("name").getValue(String.class));
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setThirdLvBgColor(String date, String poliName, String time, final View thirdLvView) {
        //TODO balik date
        String[] dMy = date.split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poliName)
                .child(time)
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        Log.e("add child",dataSnapshot.toString()+"-"+s);
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Log.e("add child",dataSnapshot.toString()+"-"+s);
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        Log.e("add child",dataSnapshot.toString());
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(User.isDoctor(activity)||dataSnapshot.getValue()==null)
                        if(dataSnapshot.getValue()!=null) {
                            if (User.isDoctor(activity)) {

                                //booking
                                //                            if (Config.BOOKING_STATUS_TYPE_EX[0].equals(dataSnapshot.child("status").getValue(String.class)))
                                if (Config.BOOKING_STATUS[0].equals(dataSnapshot.child("status").getValue(String.class)))
                                    thirdLvView.setBackgroundColor(-1);
                                    //waiting
                                    //                           else if (Config.BOOKING_STATUS_TYPE_EX[1].equals(dataSnapshot.child("status").getValue(String.class)))
                                else if (Config.BOOKING_STATUS[1].equals(dataSnapshot.child("status").getValue(String.class)))
                                    thirdLvView.setBackgroundColor(Color.parseColor("#400DFF00"));
                                    //done
                                    //                            else if (Config.BOOKING_STATUS_TYPE_EX[2].equals(dataSnapshot.child("status").getValue(String.class)))
                                else if (Config.BOOKING_STATUS[3].equals(dataSnapshot.child("status").getValue(String.class)))
                                    thirdLvView.setBackgroundColor(Color.GRAY);
                                    //expired
                                    //                            else if (Config.BOOKING_STATUS_TYPE_EX[4].equals(dataSnapshot.child("status").getValue(String.class)))
                                else if (Config.BOOKING_STATUS[5].equals(dataSnapshot.child("status").getValue(String.class)))
                                    thirdLvView.setBackgroundColor(Color.DKGRAY);
                                //                        if (dataSnapshot.getValue()!=null){
                            } else {
                                Log.e("check time", dataSnapshot.getRef().toString());
                                if (dataSnapshot.child("uid").getValue(String.class).equals(User.getUid())) {
                                    //                                if(dataSnapshot.child("status").getValue(String.class).equals(Config.BOOKING_STATUS_TYPE_EX[0]))
                                    thirdLvView.setBackgroundColor(Color.parseColor("#400DFF00"));
                                    //                                else{
                                    //                                    thirdLvView.setBackgroundColor(Color.GRAY);
                                    //                                }

                                } else {
                                    thirdLvView.setBackgroundColor(Color.GRAY);
                                    //                                thirdLvView.setClickable(false);
                                }
                            }
                        }
    //                        else thirdLvView.setBackgroundColor(-1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void alertHasBooking(final String poliName, final String[] dates, final String doctor, final String time) {
        new AlertDialog.Builder(activity)
                .setTitle(parent)
                .setMessage("anda sudah memesan, " +
                        "untuk merubah waktu pesanan silahkan hapus pesanan sebelumnya")
                .setPositiveButton("LIHAT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showBooking(poliName,dates,doctor,time);
                    }
                })
                .setNegativeButton("KEMBALI",null)
                .show();
    }

    private void alertSelectedItemHasBooked() {
        new AlertDialog.Builder(activity)
                .setTitle("Sudah dipesan")
                .setMessage("waktu yang anda pilih sudah dipesan orang lain." +
                        "silahkan pilih waktu lain")
                .show();
    }

    private void dialogBooking(final String[] dates, final String time, final String doctorName) {
        // dates = dayName, dd-MM-yyyy
        LayoutInflater li = LayoutInflater.from(activity);
        View promptView = li.inflate(R.layout.prompt_booking,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setView(promptView);

        final EditText userInput = (EditText)promptView.findViewById(R.id.reserver);
        final TextView tvDate = (TextView)promptView.findViewById(R.id.date);
        final TextView tvTime = (TextView)promptView.findViewById(R.id.time);
        final TextView tvDoctor = (TextView)promptView.findViewById(R.id.doctor);

        tvTime.setText(time);
        tvDate.setText(dates[0]+", "+ dates[1]);
        tvDoctor.setText(doctorName);

//        ArrayList<String> spinnerItem = new ArrayList<String>();
//        for (int i = 0; i< daysCode.size(); i++) {
////
//            String[] val = daysCode.get(i).replace(" ","").split(",");
////
////            Log.e("dspinner", val[0] + " " + Arrays.asList(headers).contains(val[0])+ " " +daysCode.get(i));
//
////            Calendar cal = Calendar.getInstance();
////            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
////            Date tvDate = null;
////            try {
////                tvDate = sdf.parse(val[1]);
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
////
////            if(tvDate.getTime()>System.currentTimeMillis()) {
//
////                if (Arrays.asList(headers).contains(val[0])) {
////                    spinnerItem.add(daysCode.get(i));
////
//                    if (dates.equals(val[0])) {
//                        tvDate.setText(daysCode.get(i));
//                    }
////                }
////            }
//        }

//        spinnerD.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, spinnerItem));
//        spinnerD.setSelection(setSelected);
        dialogBuilder.setTitle(parent)
//                .setMessage("terima kasih telah menggunakan layanan kami")
                .setPositiveButton("pesan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(userInput.getText().toString())) {
                            userInput.setError("tidak");
                            userInput.requestFocus();
                            return;

                        }else {
                            checkingEntry(parent,dates,time, doctorName,userInput.getText().toString());

//                            checkOwnTime(parent, dates, time, doctorName, secondGroupCode, userInput.getText().toString());
                        }

                        hideKeyboard(userInput);

                    }
                })
                .setNegativeButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        hideKeyboard(userInput);

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideKeyboard(EditText et) {
        InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void checkOwnTime(final String poli, final String[] date, final String time, final String doctorName) {

//        Toast.makeText(activity,"checkOwnTIme",Toast.LENGTH_LONG).show();

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .orderByChild("date_time").equalTo(date[0]+", "+date[1]+"_"+time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){

                            dialogBooking(date,time,doctorName);
//                            checkingEntry(poli,date,time,doctorName,reserverName);
                        }
                        else {

                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Toast.makeText(activity, data.child("time").getValue(String.class)+time, Toast.LENGTH_LONG).show();
                                if (time.equals(data.child("time").getValue(String.class))) {
                                    Toast.makeText(activity, "not available", Toast.LENGTH_LONG).show();
                                    alertNotAvailable(data.getKey(),
                                            data.child("date").getValue(String.class),
                                            data.child("time").getValue(String.class),
//                                            data.child("doctorName").getValue(String.class),
                                            data.child("doctor").getValue(String.class),
                                            data.child("name").getValue(String.class));
                                }
                                else {
                                    String unipDoctor = dataSnapshot.child("doctor").getValue(String.class);

                                    JSONObject json = null;
                                    String doctorName = "";
                                    try {
                                        json = new JSONObject(SharedPrefManager.getInstance(activity).getDoctorList());
                                        Log.e("nipdoctor",unipDoctor+SharedPrefManager.getInstance(activity).getDoctorList());
                                        doctorName = json.getString(unipDoctor);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    dialogBooking(date,time, doctorName);
//                                    checkingEntry(poli, date, time, doctorName, reserverName);
                                    Toast.makeText(activity, "available" +
                                            "", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void alertNotAvailable(final String poliName, final String dates, final String time, final String doctorName, final String reserverName) {

        final String[] date = dates.replace(" ","").split(",");

        LayoutInflater li = LayoutInflater.from(activity);
        View promptView = li.inflate(R.layout.prompt_booking_poli_success,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setView(promptView);

        final TextView poli = (TextView) promptView.findViewById(R.id.poli);
        poli.setText(poliName);
        final TextView tvDate = (TextView) promptView.findViewById(R.id.date);
        tvDate.setText(date[0]+", "+date[1].replace("-","/")+" - "+time);
        final TextView tvDocter = (TextView) promptView.findViewById(R.id.doctor);
        tvDocter.setText(doctorName);
        final TextView tvName = (TextView) promptView.findViewById(R.id.hospitale_name);
        tvName.setText(reserverName);

        ((TextView)promptView.findViewById(R.id.msg)).setText("anda tidak diijinkan melakukan dua booking dalam waktu yang sama");

        dialogBuilder.setTitle("Peringatan")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCancelWarning(poliName, date, time, doctorName, reserverName);

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void setBooking(final String poli, final String[] date, final String time, final String doctorNip, final String reserverName) {


        HashMap<String, String> val = new HashMap<String, String>();
//        try {
        val.put("uid",User.getUid());
        val.put("time", String.valueOf(System.currentTimeMillis()));
        val.put("name", reserverName);
//        val.put("status", Config.BOOKING_STATUS_TYPE_EX[0]);
        val.put("status", Config.BOOKING_STATUS[0]);
//        val.put("doctorUnip", doctorNip);
        val.put("doctor", doctorNip);

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        Log.e("checkOwnTime",val.toString());

        //TODO balik date
        String[] dMy = date[1].split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poli)
                .child(time)
                .setValue(val)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

//                        setNotification(parent +"_"+ secondGroupCode);
                        setNotification(parent+"_"+date[0]+", "+date[1]+"_"+time);

                        JSONObject json =null;
                        String doctorName = "-";
                        try {
                            json = new JSONObject(SharedPrefManager.getInstance(activity).getDoctorList());
                            doctorName = json.getString(doctorNip);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        successDialog(parent,date,time, doctorName, reserverName);
                        Toast.makeText(activity, "proses pemesanan sukses",Toast.LENGTH_LONG).show();

//                        storeBooking(parent,date,time,reserverName,firstRowPosition+","+secondGroupCode);
                        storeAtCurrentUserBookingList(parent,date,time, doctorNip, reserverName);

                        //set alarm
                        Log.e("pemesanan sukses","inisiasi alaram");
//                        Remainder.setAlarm(activity,parent,date,time,Config.BOOKING_STATUS[0]);
                        Remainder.setAlarm(activity,parent,date,time);
//                        ((MainActivityDrawer)activity).setAlarm(activity,parent,date,time,Config.BOOKING_STATUS[0]);
                        notifyDoctor(parent,date[0]+", "+date[1],time, doctorNip);
                    }
                });

    }

    private void notifyDoctor(final String service, final String dates, final String bookingTime, String doctorNip) {
        // s = serviceName_day, date_time
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_DOCTORS)
                .orderByChild("unip").equalTo(doctorNip)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()==null);
                        else{

                            for(DataSnapshot data: dataSnapshot.getChildren()){

                                String uidDoctor = data.child("uid").getValue(String.class);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                                        .child(uidDoctor)
                                        .child("notification")
                                        .push();

                                Calendar cal = Calendar.getInstance();
                                Date dateTime = cal.getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                String time = sdf.format(dateTime);

                                NotifHandler notifData = new NotifHandler(Config.BOOKING_STATUS[0], service+"_"+dates+"_"+bookingTime,
                                        "booking", false, ref.getKey(), time);

                                Log.e("notifydoctor","succes");
                                ref.setValue(notifData);

//                                //TODO send push notification for related doctor throught API
//                                HashMap<String,String> params=new HashMap<>();
//
//                                params.put("rspku_mobile","notify doctor");
//                                params.put("uid_doctor",uidDoctor);
//                                params.put("service",service);
//                                params.put("date",dates);
//                                params.put("time",bookingTime);
//
//                                RequestHandler reqH=new RequestHandler();
//                                String response = reqH.sendPostRequest(Config.NOTIFY, params);
//
////                                HashMap<String,String>params=new HashMap<>();
////                                params.put("rspku_mobile","notify doctor");
////                                params.put("uid_doctor",uidDoctor);
////                                params.put("service",service);
////                                params.put("date",dates);
////                                params.put("time",bookingTime);
////
////                                String apiUrl = Config.NOTIFY;
////
////                                RequestHandler reqH=new RequestHandler();
////                                String response = reqH.sendPostRequest(apiUrl, params);
//
//                                Log.e("responeNotifyDoctor", String.valueOf(response));
                                new doctorsNotifiction(uidDoctor,service,dates,bookingTime).execute();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public class doctorsNotifiction extends AsyncTask<Void,Void,Void> {


        private String uidDoctor,service,dates,bookingTime;

        public doctorsNotifiction(String u,String s,String d,String b){
            this.uidDoctor = u;
            this.service = s;
            this.dates = d;
            this.bookingTime = b;
        }

        @Override
        protected Void doInBackground(Void... param) {

            //TODO send push notification for related doctor throught API
            HashMap<String,String> params=new HashMap<>();

            params.put("rspku_mobile","notify doctor");
            params.put("uid_doctor",uidDoctor);
            params.put("service",service);
            params.put("date",dates);
            params.put("time",bookingTime);

            RequestHandler reqH=new RequestHandler();
            String response = reqH.sendPostRequest(Config.NOTIFY, params);

//                                HashMap<String,String>params=new HashMap<>();
//                                params.put("rspku_mobile","notify doctor");
//                                params.put("uid_doctor",uidDoctor);
//                                params.put("service",service);
//                                params.put("date",dates);
//                                params.put("time",bookingTime);
//
//                                String apiUrl = Config.NOTIFY;
//
//                                RequestHandler reqH=new RequestHandler();
//                                String response = reqH.sendPostRequest(apiUrl, params);

            Log.e("responeNotifyDoctor", String.valueOf(response));



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    private void checkingEntry(final String poli, final String[] date, final String time, final String doctorName, final String reserverName) {
        //TODO balik date
        String[] dMy = date[1].split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(poli)
                .child(time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            // TODO change doctorName nip to doctorName name
                            String doctorNip = getDoctorNip(doctorName);
                            setBooking(poli,date,time, doctorNip,reserverName);
                        }
                        else dialogEntryInteruppted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void dialogEntryInteruppted() {
        new AlertDialog.Builder(activity)
                .setTitle("Booking gagal")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("seseorang sudah lebih dulu memilih booking yang sama dengan anda, silahkan memilih waktu yang lain.")
                .show();
    }

    private void storeAtCurrentUserBookingList(String poliName, String[] date, String time, String doctorNip, String reserverName) {

//        String[] d = date[1].split("-");

        HashMap<String,String> value = new HashMap<>();
        value.put("date",date[0]+", "+date[1]);
        value.put("time",time);
        value.put("name",reserverName);
//        value.put("status",Config.BOOKING_STATUS_TYPE_EX[0]);
        value.put("status",Config.BOOKING_STATUS[0]);
        value.put("doctor", doctorNip);
        value.put("date_time", date[0]+", "+date[1]+"_"+time);

        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("booking")
                .child(poliName)
                .setValue(value);
    }

    private void storeBooking(String poli, String[] date, String time, String name, String position) {
//        SharedPrefManager.getInstance(activity).storeBooking(poli,date[1],time,name,position);
    }

    private void setNotification(String service) {
        // s = parent(POLI NAME)+","+firstRowPosition+","+secondLevelPosition
        // to directly open the list when on click notification
        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(dateTime);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Config.FURL_USERS)
                .child(User.getUid())
                .child("notification")
                .push();

        NotifHandler notifData = new NotifHandler(Config.BOOKING_STATUS[0], service, "booking", false, ref.getKey(), time);

        ref.setValue(notifData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }


    private void successDialog(final String firstRow, final String[] date, final String time, final String doctor, final String reserverName) {


        LayoutInflater li = LayoutInflater.from(activity);
        View promptView = li.inflate(R.layout.prompt_booking_poli_success,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setView(promptView);

        final TextView poli = (TextView) promptView.findViewById(R.id.poli);
        poli.setText(firstRow);
        final TextView tvDate = (TextView) promptView.findViewById(R.id.date);
        tvDate.setText(date[0]+", "+date[1].replace("-","/")+" - "+time);
        final TextView tvDocter = (TextView) promptView.findViewById(R.id.doctor);
        tvDocter.setText(doctor);
        final TextView tvName = (TextView) promptView.findViewById(R.id.hospitale_name);
        tvName.setText(reserverName);

        ((TextView)promptView.findViewById(R.id.msg)).setText("mohon konfirmasi kedatangan di resepsionis beberapa menit sebelum waktu yang dipilih ");

        final String poliName = firstRow;
        dialogBuilder.setTitle("pesanan anda")
                .setCancelable(false)
                .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCancelWarning(poliName, date, time, doctor, reserverName);
                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }

    private void showCancelWarning(final String poliName, final String[] date, final String time, final String doctorName, String reserverName) {
        LayoutInflater li = LayoutInflater.from(activity);
        View promptView = li.inflate(R.layout.prompt_booking_poli_success,null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setView(promptView);

        final TextView poli = (TextView) promptView.findViewById(R.id.poli);
        poli.setText(poliName);
        final TextView tvDate = (TextView) promptView.findViewById(R.id.date);
        tvDate.setText(date[0]+", "+date[1].replace("-","/")+" - "+time);
        final TextView tvDocter = (TextView) promptView.findViewById(R.id.doctor);
        tvDocter.setText(doctorName);
        final TextView tvName = (TextView) promptView.findViewById(R.id.hospitale_name);
        tvName.setText(reserverName);

        ((TextView)promptView.findViewById(R.id.msg)).setText("apa anda yakin ingin menghapus pesanan tersebut?");

        dialogBuilder.setTitle("Peringatan")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteBooking(date,poliName,time);

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deleteBooking(final String[] date, final String service, final String time) {
        String[] dMy = date[1].split("-");
        String yMd = dMy[2]+"-"+dMy[1]+"-"+dMy[0];
        Log.e("deleting",yMd+ service +time);
        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_RESERVATION)
                .child(yMd)
                .child(service)
                .child(time)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        SharedPrefManager.getInstance(activity).clearBooking(service);
                        Toast.makeText(activity,"pesanan anda berhasil di hapus",Toast.LENGTH_LONG).show();

                        //cancle alarm
                        Remainder.setAlarmCancel(activity,service,date[0]+", "+date[1],time);
//                        ((MainActivityDrawer)activity).setAlarmCancel(date[0]+", "+date[1],time);

                        FirebaseDatabase.getInstance().getReferenceFromUrl(Config.FURL_USERS)
                                .child(User.getUid())
                                .child("booking")
                                .child(service).removeValue();

                        //TODO notification deleting

                    }
                });

    }


    private boolean isExpired(String dates, String time, int offset) {

        Calendar cal = Calendar.getInstance();

        String currentDayCode = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));

        String secondLevelItem = null;

        try {
            secondLevelItem =  daysCode.getString(currentDayCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] currentDates = secondLevelItem.replace(" ","").split(",");

        Log.e("check expired",dates+" "+currentDayCode+" "+currentDates[0]+" "+currentDates[1]);

        if(currentDates[1].equals(dates)) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Log.e("check expired",currentDates[1]+" "+sdf.format(cal.getTime()));

            if (currentDates[1].equals(sdf.format(cal.getTime()))) {

                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = null;
                try {
                    date = sdf2.parse(currentDates[1] + " " + time);
//                    Log.e("MILIES", date.getTime() + " " + System.currentTimeMillis());
                    //if greater than 1 hour (minute*second*mili) 60 * 60 * 1000
                    if (System.currentTimeMillis() - date.getTime() > -offset) {
//                        Log.e("MILIES", System.currentTimeMillis() + "-" + date.getTime() + "=" + (System.currentTimeMillis() - date.getTime()) + ", " + (60 * 60 * 1000));
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String[] children = data.get(groupPosition);


        return children.length;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getDoctorNip(String doctorName){
        JSONObject json = null;
        String doctorNip = null;
        try {
            json = new JSONObject(SharedPrefManager.getInstance(activity).getDoctorList());
            final Iterator doctorList=json.keys();
            while(doctorList.hasNext()) {
                String dNip = (String) doctorList.next();
                if(doctorName.equals(json.getString(dNip))){
                    doctorNip = dNip;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return doctorNip;
    }

}