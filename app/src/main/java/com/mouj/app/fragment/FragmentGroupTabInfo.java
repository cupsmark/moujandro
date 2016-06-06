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
import com.mouj.app.view.ViewText;

/**
 * Created by ekobudiarto on 2/29/16.
 */
public class FragmentGroupTabInfo extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_GROUP_TAB_INFO = "tag:fragment-group-tab-info";

    String desc, creator, date_created;
    ViewText caption_desc, caption_date, caption_creator, value_desc, value_date, value_creator;

    public void setDesc(String d)
    {
        this.desc = d;
    }

    public void setCreator(String c)
    {
        this.creator = c;
    }

    public void setCreated(String cr)
    {
        this.date_created = cr;
    }

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
        caption_creator = (ViewText) activity.findViewById(R.id.group_list_post_info_caption_owner);
        caption_desc = (ViewText) activity.findViewById(R.id.group_list_post_info_caption_desc);
        caption_date = (ViewText) activity.findViewById(R.id.group_list_post_info_caption_created);
        value_creator = (ViewText) activity.findViewById(R.id.group_list_post_info_value_owner);
        value_desc = (ViewText) activity.findViewById(R.id.group_list_post_info_value_desc);
        value_date = (ViewText) activity.findViewById(R.id.group_list_post_info_value_created);

        caption_creator.setSemiBold();
        caption_date.setSemiBold();
        caption_desc.setSemiBold();

        value_creator.setText(creator);
        value_desc.setText(desc);
        value_date.setText(date_created);

    }
}
