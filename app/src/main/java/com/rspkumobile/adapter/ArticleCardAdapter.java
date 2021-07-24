package com.rspkumobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;
import com.rspkumobile.activity.ArticleReadActivity;
import com.rspkumobile.model.ArticleModelBackUp;
import com.rspkumobile.other.ArticleCardHolder;
import com.rspkumobile.model.ArticleModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DK on 12/28/2017.
 */

public class ArticleCardAdapter extends RecyclerView.Adapter<ArticleCardHolder> {

    private List<ArticleModel> list;
    private Context ctx;

//    public ArticleCardAdapter(ArrayList<ArticleModel> data){
//        list = data;
//    }

    public ArticleCardAdapter(List<ArticleModel> items, Context applicationContext) {
        list = items;
        ctx = applicationContext;
        Log.e("hot",items.toString());
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
//        Log.e("coverArticle",list.get(position).getCover().replace(" ","%20"));
//        Glide.with(ctx).load("http://192.168.43.190/rs_pku/gambar/galeri/aturan.jpg")
        Glide.with(ctx).load(list.get(position).getCover())
                .placeholder(R.drawable.foto_rs)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.coverImage);

        //exception for data downloading error
        if(list.get(position).getId()!=0000) {

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Log.e("card",list.get(position).getTitle());
//                startActivity(new Intent(ctx, ArticleReadActivity.class));
                    Intent intent = new Intent(v.getContext(), ArticleReadActivity.class);
                    intent.putExtra("articleCover", list.get(position).getCover())
                            .putExtra("articleTitle", list.get(position).getTitle())
                            .putExtra("articleContent", list.get(position).getContent());
                    v.getContext().startActivity(intent);
                }
            });
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
