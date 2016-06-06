package com.mouj.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.helper.FragmentInterface;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 3/15/16.
 */
public class FragmentProfilePost extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_PROFILE_TAB_POST = "tag:fragment-profile-post";


    ArrayList<String> pid, ptitle, pdesc, pdate, ptype, pfiles, pusers,puserid,pdon;
    DisplayImageOptions opt;
    ImageLoader loader;
    ListView lv_post;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_profile_post, container, false);
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
