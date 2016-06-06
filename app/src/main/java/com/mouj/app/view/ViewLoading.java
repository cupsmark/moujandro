package com.mouj.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mouj.app.R;

/**
 * Created by ekobudiarto on 8/5/15.
 */
public class ViewLoading extends ImageView {

    Context context;
    Animation rotation;

    public ViewLoading(Context context) {
        super(context);
        this.context = context;
    }

    public ViewLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void show()
    {
        rotation = AnimationUtils.loadAnimation(context, R.anim.view_loading);
        rotation.setRepeatMode(Animation.RESTART);
        rotation.setRepeatCount(Animation.INFINITE);
        this.startAnimation(rotation);
        this.setVisibility(View.VISIBLE);
        invalidate();
    }

    public void dismiss()
    {
        rotation.setRepeatMode(0);
        rotation.setRepeatCount(0);
        this.setVisibility(View.GONE);
    }
}
