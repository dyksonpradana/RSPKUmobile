package com.rspkumobile.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;
import com.rspkumobile.activity.Conversation;
import com.rspkumobile.activity.PhotoZoom;
import com.rspkumobile.fragment.HomeContentArticle;
import com.rspkumobile.fragment.HomeContentTips;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.other.SharedPrefManager;
import com.rspkumobile.other.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


public class Home extends Fragment {

    private List<ArticleModel> articles;
    private ViewFlipper flipper;
    private ImageView next,previous;
    private Handler myHandler;
    private Runnable flipController;

    @Override
    public void onResume() {
        super.onResume();

        SharedPrefManager.getInstance(getActivity()).expandFirstRowAt(-1);
        SharedPrefManager.getInstance(getActivity()).expandSecondRowAt(-1);
//        flipper.setAutoStart(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        final MenuItem actionLogIn = menu.findItem(R.id.masuk);
//        if (User.isLogedIn()) actionLogIn.setTitle("Keluar");
//        else actionLogIn.setTitle("Masuk");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setHasOptionsMenu(true);

        myHandler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.drawer_home, container, false);

        // article pager
        FragmentManager fm = getActivity().getSupportFragmentManager();
//        Fragment fragmentArticles = fm.findFragmentById(R.id.container_article);
//        Fragment fragmentTips = fm.findFragmentById(R.id.container_tips);

//        if (fragment == null) {
//        fragmentArticles = new HomeContentArticle();

//        fm.beginTransaction().replace(R.id.container_tips, new HomeContentTips()).commit();
        fm.beginTransaction().replace(R.id.container_article, new HomeContentArticle()).commit();

//        }

        // initializing slide show view
        flipper = (ViewFlipper) view.findViewById(R.id.flipper);

        // set action on flipper touch
        MyTouchListener touchListener = new MyTouchListener();
        flipper.setOnTouchListener(touchListener);

        // set sliding animation
        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out));

        // call stored gallery data from shared preferences (string)
        String uri = SharedPrefManager.getInstance(getActivity()).getGallery();
        Log.e("getGallery",uri);

        // convert gallery data (string) into json array "uriList"
        // img url
        JSONArray uriList = new JSONArray();
        if(uri!=null) {
            try {
                uriList = new JSONArray(uri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // create image view as many as uriList range
        for (int i = 0; i < uriList.length(); i++) {

            // exception stop if range is greater than 5
            if(i>5) break;

            ImageView imageView = new ImageView(getActivity());

            try {
//                Uri.parse(uriList.get(i).toString())
//                Log.e("uri image1",uriList.toString());
                // get the link respectively on uriList
                final String img_uri = uriList.get(i).toString();
                Log.e("slideImage",img_uri);
//                Log.e("uri image2",img_uri);
//                imageView.setImageURI(Uri.parse(img_uri));

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getActivity(), PhotoZoom.class);

                        i.putExtra("url",img_uri);
                        startActivity(i);

                    }
                });

                // fetch the actual image from server using GLIDE library
                Glide.with(getActivity()).load(img_uri)
                        // set default image if the image can not be found
                        .placeholder(R.drawable.foto_rs)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);

            } catch (JSONException e) {
                Glide.with(getActivity()).load("kosong")
                        .placeholder(R.drawable.foto_rs)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
//            imageView.setImageResource(getAllItemObject().get(i).getCover());

            // set image scaling
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(
                    new ViewFlipper.LayoutParams(ViewFlipper.LayoutParams.MATCH_PARENT,
                            ViewFlipper.LayoutParams.MATCH_PARENT));

            // put image view inside flipper (moving/slide show)
            flipper.addView(imageView);
        }

        next = (ImageView) view.findViewById(R.id.btn_next);
        previous = (ImageView) view.findViewById(R.id.btn_previous);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(!flipper.isFlipping()) {

                        flipper.stopFlipping();
                        flipper.startFlipping();
                        flipper.showNext();
//                }

//                flipper.setAutoStart(false);
//                flipper.setAutoStart(true);
//                flipper.showNext();
//                flipper.startFlipping();
//                flipper.setFlipInterval(5000);
//                flipper.stopFlipping();
                // prepare runnable method
//                Runnable flipController = new Runnable() {
//                    @Override
//                    public void run() {
//                        flipper.startFlipping();
//                    }
//                };
//                flipper.postDelayed(flipController, 3000);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!flipper.isFlipping()) {

                        flipper.stopFlipping();
                        flipper.startFlipping();

//                flipper.setAutoStart(false);
//                flipper.setAutoStart(true);
                        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_prev_in));
                        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_prev_out));
                        flipper.showPrevious();
//                flipper.setFlipInterval(5000);
//                flipper.stopFlipping();

                        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in));
                        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out));
//                flipper.stopFlipping();
//                flipper.startFlipping();
//                Runnable flipController = new Runnable() {
//                    @Override
//                    public void run() {
//                        flipper.startFlipping();
//                    }
//                };
//                flipper.postDelayed(flipController, 3000);
//                }



            }
        });

        return view;
    }

    // make flipper stop flipping on touch and star flipping on release
    public class MyTouchListener implements View.OnTouchListener{
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // on touch action
                case MotionEvent.ACTION_DOWN:
                    // stop flipping
                    flipper.stopFlipping();
                    break;

                // on release action
                case MotionEvent.ACTION_UP:
                    // start flipping
                    flipper.startFlipping();
                    break;
            }
            return false;
        }
    }

}
