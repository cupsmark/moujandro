package com.mouj.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import com.mouj.app.R;

/**
 * Created by ekobudiarto on 12/10/15.
 */
public class ViewOptionItemDialog extends Dialog {
    Context mContext;
    LinearLayout item_repost, item_hide, item_report;

    public ViewOptionItemDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ViewOptionItemDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    protected ViewOptionItemDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_dialog_option);
        item_repost = (LinearLayout) findViewById(R.id.home_dialog_option_repost);
        item_hide = (LinearLayout) findViewById(R.id.home_dialog_option_hide);
        item_report = (LinearLayout) findViewById(R.id.home_dialog_option_report);
    }

    public LinearLayout getItemRepost()
    {
        return this.item_repost;
    }

    public LinearLayout getItemHide()
    {
        return this.item_hide;
    }

    public LinearLayout getItemReport()
    {
        return this.item_report;
    }
}
