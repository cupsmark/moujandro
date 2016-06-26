package com.mouj.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.view.ViewEditText;

import java.util.Map;

/**
 * Created by developer on 6/25/16.
 */
public class FragmentScheduleConnectionGetMasjid extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_SCH_CONNECTION_GET_MSJD = "tag:fragment-sch-conn-get-msjd";

    ImageButton imagebuttonBack;
    ViewEditText edittextSearch;
    String selected_keyword = "";
    FragmentSearchMasjid masjid;
    BaseFragment fragmentSrc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_connection_masjid, container, false);
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
            addFragmentSearchMasjid();
        }
    }

    private void init()
    {
        imagebuttonBack = (ImageButton) activity.findViewById(R.id.fragment_schedule_connection_masjid_back);
        edittextSearch = (ViewEditText) activity.findViewById(R.id.fragment_schedule_connection_masjid_search);

        imagebuttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    selected_keyword = edittextSearch.getText().toString();
                } else {
                    selected_keyword = "";
                }
                masjid.setKeyword(selected_keyword, false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setFragmentSource(BaseFragment src)
    {
        this.fragmentSrc = src;
    }

    private void addFragmentSearchMasjid()
    {
        masjid = new FragmentSearchMasjid();
        masjid.setParam(activity.getParam(), activity.getToken());
        masjid.setKeyword(selected_keyword, true);
        masjid.setFragmentNotify(fragmentSrc);
        masjid.setShowAddComponent(true);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_schedule_connection_masjid_fragchild, masjid);
        ft.commit();
    }
}
