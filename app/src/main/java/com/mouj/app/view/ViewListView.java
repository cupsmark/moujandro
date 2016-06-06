package com.mouj.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by ekobudiarto on 3/15/16.
 */
public class ViewListView extends ListView {

    Context context;

    public ViewListView(Context context) {
        super(context);
    }

    public ViewListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
    }
}
