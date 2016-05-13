package com.android.mouj.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.helper.FragmentInterface;
import com.android.mouj.view.ViewText;

/**
 * Created by ekobudiarto on 5/13/16.
 */
public class FragmentScheduleList extends BaseFragment {

    public static final String TAG_SCHEDULE_LIST = "tag:fragment-schedule-list";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;

    String targetID, message = "", targetName;
    ImageButton imagebuttonBack;
    ListView listView;
    Thread threadSchedule;
    boolean success = false, isBackActivity = true;
    ViewText pagetitle;


    public FragmentScheduleList()
    {
        targetID = "";
        targetName = "";
    }

    public void setTargetID(String target) {
        this.targetID = target;
    }

    public void setTargetName(String name)
    {
        this.targetName = name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
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
        imagebuttonBack = (ImageButton) activity.findViewById(R.id.fragment_schedule_list_back);
        pagetitle = (ViewText) activity.findViewById(R.id.fragment_schedule_list_pagetitle);
        listView = (ListView) activity.findViewById(R.id.fragment_schedule_list_listview);

        pagetitle.setText(targetName);
        imagebuttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .remove(FragmentScheduleList.this).commit();
            }
        });
    }
}
