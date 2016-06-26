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

/**
 * Created by developer on 6/25/16.
 */
public class FragmentScheduleConnectionGetUstadz extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_SCH_CONNECTION_GET_USTADZ = "tag:fragment-sch-conn-get-ustadz";

    ImageButton imagebuttonBack;
    ViewEditText edittextSearch;
    FragmentSearchUstadz ustadz;
    String selected_keyword = "";
    BaseFragment fragmentSrc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_connection_ustadz, container, false);
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
            addFragmentSearchUstadz();
        }
    }

    private void init()
    {
        imagebuttonBack = (ImageButton) activity.findViewById(R.id.fragment_schedule_connection_ustadz_back);
        edittextSearch = (ViewEditText) activity.findViewById(R.id.fragment_schedule_connection_ustadz_search);

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
                ustadz.setKeyword(selected_keyword, false);
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

    private void addFragmentSearchUstadz()
    {
        ustadz = new FragmentSearchUstadz();
        ustadz.setParam(activity.getParam(), activity.getToken());
        ustadz.setKeyword(selected_keyword, true);
        ustadz.setFragmentNotify(fragmentSrc);
        ustadz.setShowAddComponent(true);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_schedule_connection_ustadz_fragchild, ustadz);
        ft.commit();
    }


}
