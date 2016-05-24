package com.android.mouj.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.helper.FragmentInterface;

/**
 * Created by ekobudiarto on 5/24/16.
 */
public class FragmentMainTimeline extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_HOME = "tag:fragment-home";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_home, container, false);
        return main_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null)
        {
            this.activity = (BaseActivity) activity;
            iFragment = (FragmentInterface) this.activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(activity != null)
        {

        }
    }

    private void back()
    {
        activity.onBackPressed();
    }
}