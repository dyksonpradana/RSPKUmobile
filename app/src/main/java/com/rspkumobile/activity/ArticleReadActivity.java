package com.rspkumobile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rspkumobile.R;


public class ArticleReadActivity extends AppCompatActivity {

    String title,content, coverUrl;
    ImageView articleCover, eCover;
    boolean isImageFitToScreen;
    private float height;
    private float width;
    private int originalHeight;
    private int originalWidth;
//    TextView title;
//    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_read);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coverUrl = getIntent().getExtras().getString("articleCover");
        title = getIntent().getExtras().getString("articleTitle");
        content = getIntent().getExtras().getString("articleContent");


//        eCover = (ImageView)findViewById(R.id.expanded_cover);
//        eCover.setImageResource(coverUrl);
        articleCover =(ImageView)findViewById(R.id.article_cover);
//        articleCover.setImageResource(coverUrl);
        Glide.with(ArticleReadActivity.this).load(coverUrl)
//                .placeholder(R.drawable.foto_rs)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(articleCover);
        articleCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ArticleReadActivity.this, PhotoZoom.class);

                i.putExtra("url",coverUrl);
                startActivity(i);
//                Intent intent = new Intent(v.getContext(),ArticleReadActivity.class);
//                intent.putExtra("articleCover", getCover());
//                v.getContext().startActivity(intent);

//                Intent intent = new Intent(ArticleReadActivity.this,ImagePreview.class);
//
//                articleCover.buildDrawingCache();
//                Bitmap img = articleCover.getDrawingCache();
//
//                intent.putExtra("imgbitmap",img);
//                startActivity(intent);
            }
        });
        ((TextView)findViewById(R.id.article_title)).setText(title);

//        byte[] data = Base64.decode(content, Base64.DEFAULT);
//        String text = new String(data, StandardCharsets.UTF_8);
//        ((TextView)findViewById(R.id.article_content)).setText(text.replace("�","").replaceAll(System.getProperty("line.separator"),""));
        ((TextView)findViewById(R.id.article_content)).setText(content);


    }

//    private void zoom2(){
//        Intent intent = new Intent(ArticleReadActivity.this,ImagePreview.class);
//
//        articleCover.buildDrawingCache();
//        Bitmap img = articleCover.getDrawingCache();
//
//        Bundle extras = new Bundle();
//        extras.putParcelable("imgbitmap",img);
//        intent.putExtra("x",extras);
//        startActivity(intent);
//    }
//
//    private void zoom3(){
////        eCover.setImageResource(coverUrl);
//    }
//
//    private void zoom1() {
//        if(isImageFitToScreen){
//
//            getSupportActionBar().show();
//
//            isImageFitToScreen = false;
//
//            articleCover.setLayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,300));
//            Log.e("scalex", String.valueOf(articleCover.getLayoutParams().height));
////            articleCover.setAdjustViewBounds(true);
//
//            ScaleAnimation scale = new ScaleAnimation(0.7f,1,0.7f,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//            scale.setDuration(100);
//
//            AnimationSet grow = new AnimationSet(true);
//            grow.setInterpolator(new LinearInterpolator());
//            grow.addAnimation(scale);
//            articleCover.startAnimation(grow);
//
////            ScaleAnimation scale = new ScaleAnimation(width,1,height,1);
////            scale.setDuration(1000);
////
////            AnimationSet shrink = new AnimationSet(true);
////            shrink.setInterpolator(new LinearInterpolator());
////            shrink.addAnimation(scale);
////            articleCover.startAnimation(shrink);
//        }else{
////            getSupportActionBar().hide();
//
//            isImageFitToScreen = true;
////            originalWidth = articleCover.getLayoutParams().width;
////            originalHeight = articleCover.getLayoutParams().height;
//
//            width = articleCover.getWidth();
//            height = articleCover.getHeight();
//
//            Log.e("scale",width+","+height);
//
////            articleCover.setLayoutParams(
////                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
////                            LinearLayout.LayoutParams.MATCH_PARENT));
////            articleCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
//
//            originalWidth = eCover.getWidth();
//            originalHeight= eCover.getHeight();
//
//            float rx = (float) width/originalWidth;
//            float ry = (float) height/originalHeight;
//
//            Log.e("scale",originalWidth+","+originalHeight);
//            Log.e("scale",rx+","+ry);
//
////            zoom();
//            ScaleAnimation scale = new ScaleAnimation(1,rx,1,rx,Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,1);
//            scale.setDuration(1000);
////            scale.setStartOffset(1000);
////
//            AnimationSet grow = new AnimationSet(true);
//            grow.setInterpolator(new LinearInterpolator());
//            grow.addAnimation(scale);
//            articleCover.startAnimation(grow);
//        }
//    }
//    public void zoom(){
//        Animation zoom = AnimationUtils.loadAnimation(this,R.anim.zoom);
//        articleCover.startAnimation(zoom);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action_bar bar item clicks here. The action_bar bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
