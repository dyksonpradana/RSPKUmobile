package com.rspkumobile.adapter;

/**
 * Created by DK on 12/30/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;
import com.rspkumobile.activity.ArticleReadActivity;
import com.rspkumobile.model.ArticleModel;
import com.rspkumobile.model.ArticleModelBackUp;

import java.util.List;

public class ArticleMenuAdapterBackUp extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<ArticleModelBackUp> listStorage;
    private Context context;

    public ArticleMenuAdapterBackUp(Context context, List<ArticleModelBackUp> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.holder_activity_article_menu_card, parent, false);
            listViewHolder.card = (CardView)convertView.findViewById(R.id.card_view);
            listViewHolder.cover = (ImageView)convertView.findViewById(R.id.cover);
            listViewHolder.title = (TextView)convertView.findViewById(R.id.title);
            listViewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Log.e("card",list.get(position).getTitle());
//                startActivity(new Intent(ctx, ArticleReadActivity.class));
                    Intent intent = new Intent(v.getContext(),ArticleReadActivity.class);
                    intent.putExtra("articleCover", listStorage.get(position).getCover())
                            .putExtra("articleTitle", listStorage.get(position).getTitle())
                            .putExtra("articleContent", listStorage.get(position).getContent());
                    v.getContext().startActivity(intent);
                }
            });
//            listViewHolder.musicAuthor = (TextView)convertView.findViewById(R.id.music_author);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
//        listViewHolder.cover.setImageResource(listStorage.get(position).getCover());
        Glide.with(context).load(listStorage.get(position).getCover())
                .placeholder(R.drawable.foto_rs)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(listViewHolder.cover);
        listViewHolder.title.setText(listStorage.get(position).getTitle());
//        listViewHolder.musicAuthor.setText(listStorage.get(position).getMusicAuthor());

        return convertView;
    }

    static class ViewHolder{
        ImageView cover;
        TextView title;
        CardView card;
    }
}
