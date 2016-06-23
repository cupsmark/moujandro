package com.mouj.app.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewText;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class FragmentScheduleConnection extends BaseFragment {

    public static final String TAG_SCHEDULE_CONNECTION = "tag:fragment-schedule-connection";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;

    ViewText form_title;
    ViewEditText value_title, value_target, value_address, value_time, value_description;
    RelativeLayout button_save;
    String selected_target = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_connection, container, false);
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
        form_title = (ViewText) activity.findViewById(R.id.schedule_connection_users_fullname);
        value_title = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_title);
        value_target = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_users_target);
        value_address = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_address);
        value_time = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_time);
        value_description = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_description);
        button_save = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_save);

        form_title.setBold();
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }



    private void save()
    {
        new AsyncTask<Void, Integer, String>()
        {
            String selected_title, selected_address, selected_time, selected_description;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }


}
