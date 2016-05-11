package com.android.mouj.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by ekobudiarto on 10/13/15.
 */
public class ViewButton extends Button {

    Context context;

    public ViewButton(Context context) {
        super(context);
        this.context = context;
        setFont();
    }

    public ViewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFont();
    }

    public ViewButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setFont();
    }

    private void setFont()
    {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/PNLight.otf");
        setTypeface(font);
    }

    public void setSemiBold()
    {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/PNSemibold.otf");
        setTypeface(font);
    }

    public void setBold()
    {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/PNBold.otf");
        setTypeface(font);
    }

    public void setRegular()
    {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/PNRegular.otf");
        setTypeface(font);
    }
}
