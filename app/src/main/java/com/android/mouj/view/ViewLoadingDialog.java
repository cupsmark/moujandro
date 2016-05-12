package com.android.mouj.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.android.mouj.R;

/**
 * Created by ekobudiarto on 8/5/15.
 */
public class ViewLoadingDialog extends Dialog {

    Context context;
    ViewLoading loading;

    public ViewLoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ViewLoadingDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_loading);
        loading = (ViewLoading)findViewById(R.id.loading_view);
        loading.setImageResource(R.drawable.custom_loading);
        loading.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
