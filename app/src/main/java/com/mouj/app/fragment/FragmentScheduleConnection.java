package com.mouj.app.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class FragmentScheduleConnection extends BaseFragment {

    public static final String TAG_SCHEDULE_CONNECTION = "tag:fragment-schedule-connection";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;

    ViewText form_title, caption_users_target;
    ViewEditText value_title, value_address, value_description;
    ViewButton value_time, value_target, value_date;
    RelativeLayout button_save;
    String selected_target = "", selected_date = "", selected_time = "", extra_mode, extra_target, extra_src,value_rep_id = "0";
    int day,month,year, hour, minute, counterDateSet;
    Map<String, String> param;
    String edit_id;

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
        param = new HashMap<String, String>();
        form_title = (ViewText) activity.findViewById(R.id.schedule_connection_users_fullname);
        caption_users_target = (ViewText) activity.findViewById(R.id.schedule_connection_caption_users_target);
        value_title = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_title);
        value_target = (ViewButton) activity.findViewById(R.id.schedule_connection_value_users_target);
        value_address = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_address);
        value_time = (ViewButton) activity.findViewById(R.id.schedule_connection_value_time);
        value_date = (ViewButton) activity.findViewById(R.id.schedule_connection_value_date);
        value_description = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_description);
        button_save = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_save);


        form_title.setBold();

        executeAccess();
        if(extra_mode.equals("edit"))
        {
            fetchEdit();
        }
        else {
            setDefault();
        }
    }

    private void executeAccess()
    {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = (c.get(Calendar.MONTH)) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        counterDateSet = 0;
        form_title.setText(activity.getUserData().get(3));
        if(activity.getUgid().equals("5"))
        {
            caption_users_target.setText(activity.getResources().getString(R.string.schedule_connection_target_masjid_caption));
            value_target.setText(activity.getResources().getString(R.string.schedule_connection_target_masjid));
            value_target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentScheduleConnectionGetMasjid masjid = new FragmentScheduleConnectionGetMasjid();
                    masjid.setFragmentSource(FragmentScheduleConnection.this);
                    iFragment.onNavigate(masjid,FragmentSearchMasjid.TAG_SEARCH_TAB_MASJID, false, "");
                }
            });
        }
        else
        {
            caption_users_target.setText(activity.getResources().getString(R.string.schedule_connection_target_ustadz));
            value_target.setText(activity.getResources().getString(R.string.schedule_connection_target_ustadz));
            value_target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentScheduleConnectionGetUstadz ustadz = new FragmentScheduleConnectionGetUstadz();
                    ustadz.setFragmentSource(FragmentScheduleConnection.this);
                    iFragment.onNavigate(ustadz,FragmentSearchUstadz.TAG_SEARCH_TAB_USTADZ, false, "");
                }
            });
        }
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        value_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePicker();
            }
        });
        value_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });
    }

    public void setExtra(String mode, String target, String src)
    {
        this.extra_mode = mode;
        this.extra_target = target;
        this.extra_src = src;
    }

    private void fetchEdit()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg;
            String edit_title, edit_desc, edit_date, edit_users_target, edit_address;

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
                    edit_id = schedule.getSchID().get(0);
                    edit_title = schedule.getSchTitle().get(0);
                    edit_desc = schedule.getSchDesc().get(0);
                    edit_date = schedule.getSchDate().get(0);
                    selected_target = schedule.getSchUser().get(0);
                    selected_target = schedule.getSchUserID().get(0);
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
                    value_title.setText(edit_title);
                    value_target.setText(selected_target);
                    value_address.setText(edit_address);
                    value_description.setText(edit_desc);
                    String dates = textDateConverted(selected_date, false);
                    String times = textDateConverted(selected_date, true);
                    value_date.setText(dates);
                    value_time.setText(times);

                }
                if(!isSuccess)
                {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void save()
    {
        new AsyncTask<Void, Integer, String>()
        {
            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg = "", ntype = "", ntitle = "", nmessage = "", nticker = "";
            String finalTitle, finalUsersTarget, finalAddress, finalTime, finalDesc;
            ArrayList<String> dateList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();

                dateList = new ArrayList<String>();
                finalTitle = value_title.getText().toString();
                finalUsersTarget = selected_target;
                finalAddress = value_address.getText().toString();
                finalTime = selected_date + " " +selected_time;
                finalDesc = value_description.getText().toString();
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
                String[] field = new String[]{"token","param","ttl","usc","dst","des","loc","mode-action",field_target,"creator_type","cut"};
                String[] value = new String[]{activity.getToken(),activity.getParam(),finalTitle,value_rep_id,finalTime,finalDesc, finalAddress,extra_mode,values_target, "connection", selected_target};

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
                            HelperGlobal.setAlarmManager(activity, 1000 + i, ntype, ntitle, nmessage, nticker, token, param, dateList.get(i), "run.schedule");
                        }
                    }
                    activity.onBackPressed();
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

            }
        }.execute();
    }

    private void setDefault()
    {
        String d = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
        String dates = textDateConverted(d, false);
        String times = textDateConverted(d, true);
        value_date.setText(dates);
        value_time.setText(times);
        selected_date = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day);
        selected_time = Integer.toString(hour)+":"+Integer.toString(minute);
    }

    private void setDatePicker()
    {
        final DatePickerDialog dialogs = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int years, int monthOfYears, int dayOfMonths) {
                if(counterDateSet % 2 == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dNow = null;
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
                            selected_date = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day);
                            value_date.setText(dates);
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
                            selected_time = Integer.toString(hour) + ":" + Integer.toString(minute)+":00";
                            value_time.setText(dates);
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

    @Override
    public void onUpdateUI() {
        super.onUpdateUI();
        param = getParameter();
        selected_target = param.get("idTarget");
        value_target.setText(param.get("nameTarget"));
        value_address.setText(param.get("addressTarget"));
    }
}
