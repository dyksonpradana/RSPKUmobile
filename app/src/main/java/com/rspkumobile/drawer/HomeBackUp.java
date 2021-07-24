package com.rspkumobile.drawer;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;
import com.rspkumobile.fragment.HomeContentArticle;
import com.rspkumobile.fragment.HomeContentTips;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.other.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


public class HomeBackUp extends Fragment {

    private List<ArticleModel> articles;
    private ViewFlipper flipper;
    private ImageView next,previous;
    private Handler myHandler;
    private Runnable flipContoller;

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

        setHasOptionsMenu(true);

        //make flipper delay after touch
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

        fm.beginTransaction().replace(R.id.container_tips, new HomeContentTips()).commit();
        fm.beginTransaction().replace(R.id.container_article, new HomeContentArticle()).commit();

//        }

        flipper = (ViewFlipper) view.findViewById(R.id.flipper);
        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out));

        String uri = SharedPrefManager.getInstance(getActivity()).getContentSlide();
//        Log.e("uri image0",uri);
        JSONArray uriList = new JSONArray();
        if(uri!=null) {
            try {
                uriList = new JSONArray(uri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < uriList.length(); i++) {
            ImageView imageView = new ImageView(getActivity());

            try {
//                Uri.parse(uriList.get(i).toString())
//                Log.e("uri image1",uriList.toString());
                String img_uri = uriList.get(i).toString();
//                Log.e("uri image2",img_uri);
//                imageView.setImageURI(Uri.parse(img_uri));
                Glide.with(getActivity()).load(img_uri)
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
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(
                    new ViewFlipper.LayoutParams(ViewFlipper.LayoutParams.MATCH_PARENT,
                            ViewFlipper.LayoutParams.MATCH_PARENT));

            flipper.addView(imageView);
        }

        Runnable flipController = new Runnable() {
            @Override
            public void run() {
                flipper.startFlipping();
            }
        };

        next = (ImageView) view.findViewById(R.id.btn_next);
        previous = (ImageView) view.findViewById(R.id.btn_previous);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.setAutoStart(false);
                flipper.showNext();
                myHandler.postDelayed(flipContoller, 3000);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.setAutoStart(false);
                flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_prev_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_prev_out));
                flipper.showPrevious();

                flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out));
                myHandler.postDelayed(flipContoller, 3000);


            }
        });

        return view;
    }

//    private List<ArticleModel> getAllItemObject(){
//        List<ArticleModel> items = new ArrayList<>();
//        items.add(new ArticleModel(R.drawable.foto_rs,"Didfp Isdfst Lfgdfow", "Csdhrisdasdstina Mialsfaian"));
//        items.add(new ArticleModel(R.drawable.a1,"D", "asdffhg"));
//        items.add(new ArticleModel(R.drawable.a2,"Sdfg dfg", "fhtyhyjgtdf"));
//        items.add(new ArticleModel(R.drawable.a3,"Radgdhfda adfh", "adgjekjghjhgh"));
//        items.add(new ArticleModel(R.drawable.a4,"Pcmdjgje", "uyltyhtf"));
//        items.add(new ArticleModel(R.drawable.a5,"Fodsvd", "erqgadgdfg"));
//        items.add(new ArticleModel(R.drawable.a6,"Ssd sdgasdg sdgasdf sadf", "rtuhegdfg"));
//        items.add(new ArticleModel(R.drawable.a7,"Marsdgsdek", "ytkjgrfjt"));
//        return items;
//    }

}
