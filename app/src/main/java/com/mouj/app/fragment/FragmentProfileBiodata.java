package com.mouj.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;

/**
 * Created by ekobudiarto on 3/15/16.
 */
public class FragmentProfileBiodata extends BaseFragment {


    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_GROUP_TAB_INFO = "tag:fragment-profile-post";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_group_list_post_info, container, false);
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
            init();
        }
    }

    private void init()
    {

    }

}
