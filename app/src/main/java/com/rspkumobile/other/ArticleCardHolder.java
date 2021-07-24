package com.rspkumobile.other;

import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspkumobile.R;

/**
 * Created by DK on 12/28/2017.
 */

public class ArticleCardHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageView coverImage;
    public CardView card;

    public ArticleCardHolder(View v){
        super(v);

        title = (TextView)v.findViewById(R.id.articleTitle);
        coverImage = (ImageView)v.findViewById(R.id.articleCover);
        card = (CardView)v.findViewById(R.id.articleCard);

    }
}
