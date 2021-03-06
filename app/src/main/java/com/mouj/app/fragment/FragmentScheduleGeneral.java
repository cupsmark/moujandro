package com.mouj.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.activity.ActivitySchedule;
import com.mouj.app.activity.ActivityScheduleTimelineDetail;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSchedule;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class FragmentScheduleGeneral extends BaseFragment {

    public static final String TAG_SCHEDULE_GENERAL = "tag:fragment-schedule-general";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;

    String value_title, value_desc, value_date, value_time, value_location, value_repeat, value_id, value_users, value_usersid, value_rep_id;
    ViewText text_date, text_time, text_location, text_repeat;
    ViewEditText schedule_title, schedule_desc;
    ImageButton button_pick_date, button_pick_time, button_pick_location, button_pick_repeat, button_back;
    int day,month,year, hour, minute;
    RelativeLayout button_save;
    int counterDateSet;
    String extra_mode, extra_target, extra_src;
    ViewLoadingDialog dialog;
    Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_general, container, false);
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
            initParameter();
        }
    }

    private void initParameter()
    {
        extra_mode = activity.getIntent().getExtras().getString("mode-action");
        initComponent();
        if(extra_mode.equals("edit"))
        {
            extra_target = activity.getIntent().getExtras().getString("target");
            extra_src = activity.getIntent().getExtras().getString("mode-src");
            fetchEdit();
        }
        else {
            setDefault();
        }
    }

    private void fetchEdit()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(activity);
                schedule.setParam(activity.getParam(), activity.getToken());
                schedule.setSingleID(extra_target);
                schedule.executeScheduleDetail();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
                    value_id = schedule.getSchID().get(0);
                    value_title = schedule.getSchTitle().get(0);
                    value_desc = schedule.getSchDesc().get(0);
                    value_date = schedule.getSchDate().get(0);
                    value_users = schedule.getSchUser().get(0);
                    value_usersid = schedule.getSchUserID().get(0);
                    value_rep_id = schedule.getRepostFromID().get(0);
                }
                else {
                    isSuccess = false;
                    msg = schedule.getMessage();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(isSuccess)
                {
                    schedule_title.setText(value_title);
                    schedule_desc.setText(value_desc);
                    String dates = textDateConverted(value_date, false);
                    String times = textDateConverted(value_date, true);
                    text_date.setText(dates);
                    text_time.setText(times);

                }
                if(!isSuccess)
                {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void initComponent()
    {
        value_id = "";
        value_title = "";
        value_desc = "";
        value_date = "";
        value_time = "";
        value_location = "";
        value_repeat = "";
        value_users = "";
        value_usersid = "";
        value_rep_id = "0";

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = (c.get(Calendar.MONTH)) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        counterDateSet = 0;

        schedule_title = (ViewEditText) activity.findViewById(R.id.schedule_new_editfield_title);
        schedule_desc = (ViewEditText) activity.findViewById(R.id.schedule_new_editfield_desc);
        text_date = (ViewText) activity.findViewById(R.id.schedule_new_value_date);
        text_time = (ViewText) activity.findViewById(R.id.schedule_new_value_time);
        text_location = (ViewText) activity.findViewById(R.id.schedule_new_value_location);
        text_repeat = (ViewText) activity.findViewById(R.id.schedule_new_value_repeat);
        button_pick_date = (ImageButton) activity.findViewById(R.id.schedule_new_imagebutton_pick_date);
        button_pick_time = (ImageButton) activity.findViewById(R.id.schedule_new_imagebutton_pick_time);
        button_pick_location = (ImageButton) activity.findViewById(R.id.schedule_new_imagebutton_pick_location);
        button_pick_repeat = (ImageButton) activity.findViewById(R.id.schedule_new_imagebutton_pick_repeat);
        button_save = (RelativeLayout) activity.findViewById(R.id.schedule_new_relative_button_save);
        button_back = (ImageButton) activity.findViewById(R.id.schedule_new_imagebutton_back);

        button_pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });
        button_pick_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePicker();
            }
        });
        button_pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });
        button_pick_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeat();
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });
        text_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePicker();
            }
        });
        text_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });
        text_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeat();
            }
        });
    }


    private void setDefault()
    {
        String d = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
        String dates = textDateConverted(d, false);
        String times = textDateConverted(d, true);
        text_date.setText(dates);
        text_time.setText(times);
        String[] repeat_value = getResources().getStringArray(R.array.schedule_repeat_value);
        String[] repeat_title = getResources().getStringArray(R.array.schedule_repeat_title);
        text_repeat.setText(repeat_title[0]);
        value_repeat = repeat_value[0];
    }

    private void setDatePicker()
    {
        final DatePickerDialog dialogs = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int years, int monthOfYears, int dayOfMonths) {
                if(counterDateSet % 2 == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dNow = new Date();
                    try {
                        String nows = sdf.format(new Date());
                        dNow = sdf.parse(nows);
                        Date dSet = sdf.parse(Integer.toString(years)+"-"+Integer.toString(monthOfYears + 1)+"-"+
                                Integer.toString(dayOfMonths));

                        if(dSet.before(dNow))
                        {
                            Toast.makeText(activity, getResources().getString(R.string.v1_schedule_new_invalid_date), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            year = years;
                            month = monthOfYears + 1;
                            day = dayOfMonths;
                            String dates = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
                            dates = textDateConverted(dates,false);
                            value_date = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day);
                            text_date.setText(dates);
                        }
                    } catch (ParseException e) {
                        //e.printStackTrace();
                        Toast.makeText(activity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                counterDateSet++;
            }
        },year, month - 1, day);
        dialogs.setCancelable(true);
        dialogs.show();
        dialogs.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                counterDateSet = 0;
            }
        });
    }

    private void setTimePicker()
    {
        TimePickerDialog timePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                if(counterDateSet % 2 == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    Date dNow = new Date();
                    try {
                        String nows = sdf.format(new Date());
                        dNow = sdf.parse(nows);
                        Date dSet = sdf.parse(Integer.toString(year)+"-"+Integer.toString(month)+"-"+
                                Integer.toString(day) + " " + Integer.toString(hourOfDay) + ":" + Integer.toString(minutes) +
                                ":00");

                        if(dSet.before(dNow))
                        {
                            Toast.makeText(activity, getResources().getString(R.string.v1_schedule_new_invalid_date), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            hour = hourOfDay;
                            minute = minutes;

                            String dates = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
                            dates = textDateConverted(dates, true);
                            value_time = Integer.toString(hour) + ":" + Integer.toString(minute)+":00";
                            text_time.setText(dates);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                counterDateSet++;
            }
        }, hour, minute, true);
        timePicker.setCancelable(false);
        timePicker.show();
        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                counterDateSet = 0;
            }
        });
    }

    private void setLocation()
    {
        final Dialog d = new Dialog(activity);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.schedule_new_dialog_datepicker);
        d.setCancelable(true);

        LinearLayout layouts = (LinearLayout) d.findViewById(R.id.schedule_new_dialog_relative_location);
        layouts.setVisibility(View.VISIBLE);

        final ViewEditText dialog_location = (ViewEditText) d.findViewById(R.id.schedule_new_dialog_editlocation);

        ViewButton btn_yes = (ViewButton) d.findViewById(R.id.schedule_new_dialog_button_ok);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String vals = dialog_location.getText().toString();
                value_location = vals;
                text_location.setText(vals);
                d.dismiss();
            }
        });

        ViewButton btn_cancel = (ViewButton) d.findViewById(R.id.schedule_new_dialog_button_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
            }
        });
        d.show();
    }

    private void setRepeat()
    {

        final String[] repeat_title = getResources().getStringArray(R.array.schedule_repeat_title);
        final String[] repeat_value = getResources().getStringArray(R.array.schedule_repeat_value);
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setIcon(R.mipmap.ic_launcher);
        b.setTitle(getResources().getString(R.string.v1_schedule_new_textfield_repeat));
        b.setSingleChoiceItems(repeat_title, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(ActivityScheduleNew.this, repeat_value[which], Toast.LENGTH_SHORT).show();
                value_repeat = repeat_value[which];
                text_repeat.setText(repeat_title[which]);
            }
        });
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text_repeat.setText(getResources().getString(R.string.v1_schedule_new_textfield_repeat));
            }
        }).create().show();
        value_repeat = repeat_value[0];

    }



    private String textDateConverted(String date, boolean isTime)
    {
        try{
            String zone = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateNormal = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.US);
            if(isTime)
            {
                sdf2 = new SimpleDateFormat("hh:mm aa");
                zone = "";
            }
            String currents = sdf2.format(dateNormal);
            return currents + zone;
        }catch (ParseException ex)
        {
            return "";
        }
    }

    private void save()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg = "", ntype = "", ntitle = "", nmessage = "", nticker = "";
            ArrayList<String> dateList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();

                dateList = new ArrayList<String>();
                value_title = schedule_title.getText().toString();
                value_desc = schedule_desc.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {
                String field_target = "";
                String values_target = "";
                if(extra_mode.equals("edit"))
                {
                    field_target = "target";
                    values_target = extra_target;
                }
                String[] field = new String[]{"token","param","ttl","typ","usc","dst","dnd","des","hed","med","rpt","loc","mode-action",field_target};
                String[] value = new String[]{activity.getToken(),activity.getParam(),value_title,"0",value_rep_id,value_date + " " + value_time,"",value_desc,"","",value_repeat, value_location,extra_mode,values_target};

                ActionSchedule schedule = new ActionSchedule(activity);
                schedule.setParam(activity.getParam(),activity. getToken());
                schedule.setArrayPOST(field,value);
                schedule.executeSave();
                if(schedule.getSuccess())
                {
                    isSuccess = schedule.getSuccess();
                    ntype = schedule.getNType();
                    ntitle = schedule.getNTitle();
                    nmessage = schedule.getNMessage();
                    nticker = schedule.getNTicker();
                    if(schedule.getNDateList().size() > 0)
                    {
                        dateList.addAll(schedule.getNDateList());
                    }
                }
                else {
                    isSuccess = false;
                }
                msg = schedule.getMessage();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(isSuccess)
                {
                    if(dateList.size() > 0)
                    {
                        String token = activity.getToken();
                        String param = activity.getParam();
                        for(int i = 0;i < dateList.size();i++)
                        {
                            HelperGlobal.setAlarmManager(activity,1000+i, ntype, ntitle, nmessage, nticker, token, param, dateList.get(i), "run.schedule");
                        }
                    }
                    back();
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

            }
        }.execute();
    }

    private void back()
    {
        Intent i= null;
        if(extra_mode.equals("edit"))
        {
            i = new Intent(activity, ActivityScheduleTimelineDetail.class);
            i.putExtra("mode-src", extra_src);
            i.putExtra("target",extra_target);
        }
        else {
            i = new Intent(activity, ActivitySchedule.class);
        }
        startActivity(i);
        activity.finish();
    }



}
