package com.rspkumobile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.rspkumobile.R;

public class PhotoZoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zome);

        PhotoView pv =(PhotoView)findViewById(R.id.pz);
//        pv.setImageResource(R.drawable.logo192x192);

        String url = getIntent().getExtras().getString("url");

        Glide.with(this).load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(pv);

    }
}
