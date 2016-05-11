package com.android.mouj.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.activity.ActivityGroupForm;
import com.android.mouj.helper.FragmentInterface;
import com.android.mouj.view.ViewText;

/**
 * Created by ekobudiarto on 2/27/16.
 */
public class FragmentGroupEmpty extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_GROUP_EMPTY = "tag:fragment-group-empty";

    ImageButton imagebutton_create;
    ViewText text_title;
    String u;

    public FragmentGroupEmpty()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_group_empty, container, false);
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

    public void setU(String u)
    {
        this.u = u;
    }

    private void init()
    {
        imagebutton_create = (ImageButton) activity.findViewById(R.id.group_empty_btn_create);
        text_title = (ViewText) activity.findViewById(R.id.group_empty_title_button);
        text_title.setBold();
        if(u.equals("5") || u.equals("6"))
        {
            text_title.setText(activity.getResources().getString(R.string.group_empty_title_create));
            imagebutton_create.setVisibility(View.VISIBLE);
            imagebutton_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, ActivityGroupForm.class);
                    i.putExtra("mode","add");
                    startActivity(i);
                    activity.finish();
                }
            });
        }
        else {
            text_title.setText("Anda tidak memiliki Grup");
        }

    }
}
