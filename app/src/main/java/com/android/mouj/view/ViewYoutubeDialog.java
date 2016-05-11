package com.android.mouj.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.fragment.FragmentYoutube;
import com.android.mouj.helper.HelperGlobal;

/**
 * Created by ekobudiarto on 3/30/16.
 */
public class ViewYoutubeDialog extends DialogFragment {

    View main_view;
    BaseActivity activity;
    ViewButton button_ok, button_cancel;
    ViewText textview_message, textview_title;
    String text_ok, text_cancel, text_title, text_message;
    RelativeLayout container_fragment;
    String youtube_id;
    int wScreen;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.base_dialog_message, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return main_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null)
        {
            this.activity = (BaseActivity) activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(activity != null)
        {
            init();
        }
    }

    private void init()
    {
        wScreen = HelperGlobal.GetDimension("w", activity);
        button_ok = (ViewButton) main_view.findViewById(R.id.base_dialog_ok);
        button_cancel = (ViewButton) main_view.findViewById(R.id.base_dialog_cancel);
        textview_title = (ViewText) main_view.findViewById(R.id.base_dialog_title);
        textview_message = (ViewText) main_view.findViewById(R.id.base_dialog_message);
        container_fragment = (RelativeLayout) main_view.findViewById(R.id.base_dialog_fragment);

        button_ok.setVisibility(View.INVISIBLE);
        button_cancel.setVisibility(View.INVISIBLE);
        textview_message.setVisibility(View.GONE);
        textview_title.setSemiBold();
        textview_title.setText(text_title);
        container_fragment.getLayoutParams().width = wScreen - 30;

        FragmentYoutube youtube = FragmentYoutube.newInstance(youtube_id);
        getChildFragmentManager().beginTransaction().replace(R.id.base_dialog_fragment, youtube).commit();
    }

    public void setYoutubeID(String yid)
    {
        this.youtube_id = yid;
    }
    public void setTitle(String title)
    {
        this.text_title = title;
    }
}

