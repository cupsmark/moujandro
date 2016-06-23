package com.mouj.app.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.R;
import com.mouj.app.fragment.FragmentScheduleConnection;
import com.mouj.app.fragment.FragmentScheduleGeneral;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSchedule;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityScheduleNew extends BaseActivity {


    String extra_mode, extra_target, extra_src;
    Tracker tracker;
    FragmentScheduleGeneral scheduleGeneral;
    FragmentScheduleConnection scheduleConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Create Schedule");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initParameter();
    }

    private void initParameter()
    {
        extra_mode = getIntent().getExtras().getString("mode-action");

        if(extra_mode.equals("edit"))
        {
            extra_target = getIntent().getExtras().getString("target");
            extra_src = getIntent().getExtras().getString("mode-src");
        }
        switchFragment();
    }

    private void switchFragment()
    {
        if(HelperGlobal.getTurnOnFeatureConnection(ActivityScheduleNew.this))
        {
            addFragmentScheduleConnection();
        }
        else
        {
            addFragmentScheduleGeneral();
        }
    }

    private void addFragmentScheduleGeneral()
    {
        scheduleGeneral = new FragmentScheduleGeneral();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.schedule_new_fragment, scheduleGeneral);
        ft.commit();
    }

    private void addFragmentScheduleConnection()
    {
        scheduleConnection = new FragmentScheduleConnection();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.schedule_new_fragment, scheduleConnection);
        ft.commit();
    }

    private void back()
    {
        Intent i= null;
        if(extra_mode.equals("edit"))
        {
            i = new Intent(ActivityScheduleNew.this, ActivityScheduleTimelineDetail.class);
            i.putExtra("mode-src", extra_src);
            i.putExtra("target",extra_target);
        }
        else {
            i = new Intent(ActivityScheduleNew.this, ActivitySchedule.class);
        }
        startActivity(i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }
}
