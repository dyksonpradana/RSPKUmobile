package com.rspkumobile.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

;import com.rspkumobile.R;
import com.rspkumobile.app.Config;

public class AboutUsActivity extends AppCompatActivity {


    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;
    private CardView profileCard;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;
    private boolean isImageFitToScreen;
    private RelativeLayout container2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        profileCard = (CardView)findViewById(R.id.profileCard);
//        container2 = (RelativeLayout)findViewById(R.id.container2);

//        ((TextView)findViewById(R.id.webAddress)).setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.alamat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("geo:"+Config.LOCATION));
                startActivity(i);
            }
        });
        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel",Config.TELEPHONE,null))
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // Hook up clicks on the thumbnail views.

//        final View thumb1View = findViewById(R.id.profilePicture);
//        thumb1View.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                zoom1();
//                zoomImageFromThumb(thumb1View, R.drawable.foto_rs);
//
//            }
//        });

        /*final View thumb2View = findViewById(R.id.thumb_button_2);
        thumb2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(thumb2View, R.drawable.image2);
            }
        });*/

        // Retrieve and cache the system's default "short" animation time.
//        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    //    private void zoom1() {
//        ImageView articleCover =(ImageView)findViewById(R.id.profilePicture);
//        if(isImageFitToScreen){
//
//            getSupportActionBar().show();
//
//            isImageFitToScreen = false;
//            articleCover.setLayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT));
//            articleCover.setAdjustViewBounds(true);
//
//            ScaleAnimation scale = new ScaleAnimation(0.7f,1,0.7f,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
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
//            getSupportActionBar().hide();
//
//            isImageFitToScreen = true;
////            originalWidth = articleCover.getLayoutParams().width;
////            originalHeight = articleCover.getLayoutParams().height;
//
////            width = articleCover.getLayoutParams().width;
////            height = articleCover.getLayoutParams().height;
//
////            Log.e("scale",width+","+height);
//
//            articleCover.setLayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT));
//            articleCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
//
////            originalWidth = articleCover.getLayoutParams().width;
////            originalHeight= articleCover.getLayoutParams().height;
////
////            float rx = (float) originalWidth/width;
////            float ry = (float) originalHeight/height;
////
////            Log.e("scale",originalWidth+","+originalHeight);
//
//            zoom(articleCover);
////            ScaleAnimation scale = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
////            scale.setDuration(1000);
////
////            AnimationSet grow = new AnimationSet(true);
////            grow.setInterpolator(new LinearInterpolator());
////            grow.addAnimation(scale);
////            articleCover.startAnimation(grow);
//        }
//    }
//    public void zoom(ImageView x){
//        Animation zoom = AnimationUtils.loadAnimation(this,R.anim.zoom);
//        x.startAnimation(zoom);
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

    public void openWeb(View view){
//        Uri uriUrl=Uri.parse(Config.WEB_ADDRESS);
        Intent launchBrowser=new Intent();
        launchBrowser.setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(Config.WEB_ADDRESS));
        startActivity(launchBrowser);
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.Home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivityToken.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    *//**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     *
     * <ol>
     *   <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     *   <li>Calculate the starting and ending bounds for the expanded view.</li>
     *   <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     *       simultaneously, from the starting bounds to the ending bounds.</li>
     *   <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     *
     * @param thumbView  The thumbnail view to zoom in.
     * @param imageResId The high-resolution version of the image represented by the thumbnail.
     */
//    private void zoomImageFromThumb(final View thumbView, int imageResId) {
//        // If there's an animation in progress, cancel it immediately and proceed with this one.
//        if (mCurrentAnimator != null) {
//            mCurrentAnimator.cancel();
//        }
//
//        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
//        expandedImageView.setImageResource(imageResId);
//
//        // Calculate the starting and ending bounds for the zoomed-in image. This step
//        // involves lots of math. Yay, math.
//        final Rect startBounds = new Rect();
//        final Rect finalBounds = new Rect();
//        final Point globalOffset = new Point();
//
//        // The start bounds are the global visible rectangle of the thumbnail, and the
//        // final bounds are the global visible rectangle of the container view. Also
//        // set the container view's offset as the origin for the bounds, since that's
//        // the origin for the positioning animation properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBounds);
//        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
//        startBounds.offset(-globalOffset.x, -globalOffset.y);
//        finalBounds.offset(-globalOffset.x, -globalOffset.y);
//
//        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
//        // "center crop" technique. This prevents undesirable stretching during the animation.
//        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
//        float startScale;
//        if ((float) finalBounds.width() / finalBounds.height()
//                > (float) startBounds.width() / startBounds.height()) {
//            // Extend start bounds horizontally
//            startScale = (float) startBounds.height() / finalBounds.height();
//            float startWidth = startScale * finalBounds.width();
//            float deltaWidth = (startWidth - startBounds.width()) / 2;
//            startBounds.left -= deltaWidth;
//            startBounds.right += deltaWidth;
//        } else {
//            // Extend start bounds vertically
//            startScale = (float) startBounds.width() / finalBounds.width();
//            float startHeight = startScale * finalBounds.height();
//            float deltaHeight = (startHeight - startBounds.height()) / 2;
//            startBounds.top -= deltaHeight;
//            startBounds.bottom += deltaHeight;
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
//        // it will position the zoomed-in view in the place of the thumbnail.
//        thumbView.setAlpha(0f);
//        expandedImageView.setVisibility(View.VISIBLE);
//        container2.setVisibility(View.INVISIBLE);
////        profileCard.setVisibility(View.INVISIBLE);
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
//        // the zoomed-in view (the default is the center of the view).
//        expandedImageView.setPivotX(0f);
//        expandedImageView.setPivotY(0f);
//
//        // Construct and run the parallel animation of the four translation and scale properties
//        // (X, Y, SCALE_X, and SCALE_Y).
//        AnimatorSet set = new AnimatorSet();
//        set
//                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
//                        finalBounds.left))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
//                        finalBounds.top))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
//        set.setDuration(mShortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mCurrentAnimator = null;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                mCurrentAnimator = null;
//            }
//        });
//        set.start();
//        mCurrentAnimator = set;
//
//        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
//        // and show the thumbnail instead of the expanded image.
//        final float startScaleFinal = startScale;
//        expandedImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCurrentAnimator != null) {
//                    mCurrentAnimator.cancel();
//                }
//
//                // Animate the four positioning/sizing properties in parallel, back to their
//                // original values.
//                AnimatorSet set = new AnimatorSet();
//                set
//                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
//                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
//                set.setDuration(mShortAnimationDuration);
//                set.setInterpolator(new DecelerateInterpolator());
//                set.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
////                        profileCard.setVisibility(View.VISIBLE);
//                        container2.setVisibility(View.VISIBLE);
//                        mCurrentAnimator = null;
//                        getSupportActionBar().show();
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
////                        profileCard.setVisibility(View.VISIBLE);
//                        container2.setVisibility(View.VISIBLE);
//                        mCurrentAnimator = null;
//                        getSupportActionBar().show();
//                    }
//                });
//                set.start();
//                mCurrentAnimator = set;
//            }
//        });
//    }
}
