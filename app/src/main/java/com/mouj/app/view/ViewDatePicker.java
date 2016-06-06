package com.mouj.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;

/**
 * Created by ekobudiarto on 11/30/15.
 */
public class ViewDatePicker extends DatePicker{

    Context mContext;

    public ViewDatePicker(Context context) {
        super(context);
        this.mContext = context;
    }

    public ViewDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ViewDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }


}
