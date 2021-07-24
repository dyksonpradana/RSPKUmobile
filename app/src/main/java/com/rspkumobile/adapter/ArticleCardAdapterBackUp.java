package com.rspkumobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;
import com.rspkumobile.activity.ArticleReadActivity;
import com.rspkumobile.model.ArticleModelBackUp;
import com.rspkumobile.other.ArticleCardHolder;

import java.util.ArrayList;

/**
 * Created by DK on 12/28/2017.
 */

public class ArticleCardAdapterBackUp extends RecyclerView.Adapter<ArticleCardHolder> {

    private ArrayList<ArticleModelBackUp> list;
    private Context ctx;

//    public ArticleCardAdapter(ArrayList<ArticleModel> data){
//        list = data;
//    }

    public ArticleCardAdapterBackUp(ArrayList<ArticleModelBackUp> items, Context applicationContext) {
        list = items;
        ctx = applicationContext;
    }

//    public ArticleCardAdapter(ArrayList<ArticleModel> data, FragmentActivity activity) {
//        list = data;
//    }

    @Override
    public ArticleCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder_article_frame_card, parent, false);
        ArticleCardHolder holder = new ArticleCardHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArticleCardHolder holder, final int position) {

        holder.title.setText(list.get(position).getTitle());
//        holder.coverImage.setImageResource(list.get(position).getCover());
        Glide.with(ctx).load(list.get(position).getCover())
                .placeholder(R.drawable.foto_rs)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.coverImage);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("card",list.get(position).getTitle());
//                startActivity(new Intent(ctx, ArticleReadActivity.class));
                Intent intent = new Intent(v.getContext(),ArticleReadActivity.class);
                intent.putExtra("articleCover", list.get(position).getCover())
                        .putExtra("articleTitle", list.get(position).getTitle())
                        .putExtra("articleContent", list.get(position).getContent());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
