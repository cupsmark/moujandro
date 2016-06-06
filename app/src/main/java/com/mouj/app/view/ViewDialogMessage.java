package com.mouj.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mouj.app.R;

/**
 * Created by ekobudiarto on 12/18/15.
 */
public class ViewDialogMessage extends Dialog {

    Context mContext;
    ViewButton button_ok, button_cancel;
    ViewText textview_message, textview_title;
    String text_ok, text_cancel, text_title, text_message;
    RelativeLayout container_fragment;

    public ViewDialogMessage(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ViewDialogMessage(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }

    protected ViewDialogMessage(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_dialog_message);

        button_ok = (ViewButton) findViewById(R.id.base_dialog_ok);
        button_cancel = (ViewButton) findViewById(R.id.base_dialog_cancel);
        textview_title = (ViewText) findViewById(R.id.base_dialog_title);
        textview_message = (ViewText) findViewById(R.id.base_dialog_message);
        container_fragment = (RelativeLayout) findViewById(R.id.base_dialog_fragment);


        textview_title.setSemiBold();
        textview_title.setText(text_title);
        textview_message.setText(text_message);

    }

    private void init()
    {
        text_ok = mContext.getResources().getString(R.string.base_string_ok);
        text_cancel = mContext.getResources().getString(R.string.base_string_cancel);
        text_title = mContext.getResources().getString(R.string.app_name);
        text_message = "";
    }


    public ViewButton getButtonOK()
    {
        return this.button_ok;
    }

    public ViewButton getButtonCancel()
    {
        return this.button_cancel;
    }

    public void setTitle(String title)
    {
        this.text_title = title;
    }

    public void setMessage(String msg)
    {
        this.text_message = msg;
    }

    public void setTextButtonOK(String text)
    {
        this.text_ok = text;
    }

    public void setTextButtonCancel(String text)
    {
        this.text_cancel = text;
    }
}
