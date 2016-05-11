package com.android.mouj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionSchedule;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewDialogMessage;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityScheduleTimelineDetail extends BaseActivity {

    ImageButton btn_back;
    ViewText txt_user, txt_date, txt_title, txt_desc, txt_time;
    RelativeLayout btn_share, btn_delete, btn_edit, btn_add_tome;
    ViewLoadingDialog dialog;
    String value_id, value_title, value_date, value_desc, value_user, value_userid, extra_id, extra_mode;
    boolean isEdit = false, isDelete = false, isAddToMe = false;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_timeline_detail);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Schedule Timeline Detail");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        value_id = "";
        value_title = "";
        value_desc = "";
        value_date = "";
        value_user = "";
        value_userid = "";
        extra_id = getIntent().getExtras().getString("target");
        extra_mode = getIntent().getExtras().getString("mode-src");
        btn_back = (ImageButton) findViewById(R.id.schedule_timeline_detail_imagebutton_back);
        txt_user = (ViewText) findViewById(R.id.schedule_timeline_detail_txt_user);
        txt_date = (ViewText) findViewById(R.id.schedule_timeline_detail_txt_date);
        txt_time = (ViewText) findViewById(R.id.schedule_timeline_detail_txt_time);
        txt_title = (ViewText) findViewById(R.id.schedule_timeline_detail_txt_title);
        txt_desc = (ViewText) findViewById(R.id.schedule_timeline_detail_txt_desc);
        btn_share = (RelativeLayout) findViewById(R.id.schedule_timeline_detail_btn_share);
        btn_delete = (RelativeLayout) findViewById(R.id.schedule_timeline_detail_btn_delete);
        btn_edit = (RelativeLayout) findViewById(R.id.schedule_timeline_detail_btn_edit);
        btn_add_tome = (RelativeLayout) findViewById(R.id.schedule_timeline_detail_btn_addtome);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        fetch();
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityScheduleTimelineDetail.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(ActivityScheduleTimelineDetail.this);
                schedule.setParam(getParam(), getToken());
                schedule.setSingleID(extra_id);
                schedule.executeScheduleDetail();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
                    value_id = schedule.getSchID().get(0);
                    value_title = schedule.getSchTitle().get(0);
                    value_desc = schedule.getSchDesc().get(0);
                    value_date = schedule.getSchDate().get(0);
                    value_user = schedule.getSchUser().get(0);
                    value_userid = schedule.getSchUserID().get(0);
                    isEdit = schedule.getEdit();
                    isDelete = schedule.getDelete();
                    isAddToMe = schedule.getAddToMe();
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
                    String d = TimeUtils.convertFormatDate(value_date,"dt")+", " + TimeUtils.convertFormatDate(value_date,"m")+
                            " " + TimeUtils.convertFormatDate(value_date,"d") + ", "+ TimeUtils.convertFormatDate(value_date,"y");
                    String t = TimeUtils.convertFormatDate(value_date, "H") + " WIB";
                    txt_title.setText(value_title);
                    txt_title.setRegular();
                    txt_user.setText(value_user);
                    txt_user.setRegular();
                    txt_desc.setText(value_desc);
                    txt_time.setText(t);
                    txt_time.setRegular();
                    txt_date.setText(d.toUpperCase(Locale.US));
                    txt_date.setRegular();
                    btn_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            share();
                        }
                    });
                    if(isEdit)
                    {
                        btn_edit.setVisibility(View.VISIBLE);
                        btn_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goEdit();
                            }
                        });
                    }
                    if(isDelete)
                    {
                        btn_delete.setVisibility(View.VISIBLE);
                        btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDelete();
                            }
                        });
                    }
                    if(isAddToMe)
                    {
                        btn_add_tome.setVisibility(View.VISIBLE);
                        btn_add_tome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmAddToMe();
                            }
                        });
                    }
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityScheduleTimelineDetail.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void share()
    {
        String desc = "<strong>"+value_title+"</strong><br/><br/>";
        desc += value_desc +"<br/><br/>"+TimeUtils.convertFormatDate(value_date,"d")+" ";
        desc += TimeUtils.convertFormatDate(value_date,"m")+" "+TimeUtils.convertFormatDate(value_date,"y")+" ";
        desc += "Pkl "+TimeUtils.convertFormatDate(value_date,"H")+"<br/>";
        desc += "oleh : "+value_user;
        Intent specific_intent = new Intent();
        specific_intent.setAction(Intent.ACTION_SEND);
        specific_intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(desc).toString());
        specific_intent.putExtra(Intent.EXTRA_TITLE, value_title);
        specific_intent.putExtra(Intent.EXTRA_SUBJECT, value_title);
        specific_intent.setType("text/plain");
        specific_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(specific_intent);
    }

    private void goEdit()
    {
        Intent i = new Intent(ActivityScheduleTimelineDetail.this, ActivityScheduleNew.class);
        i.putExtra("mode-src",extra_mode);
        i.putExtra("mode-action","edit");
        i.putExtra("target",extra_id);
        startActivity(i);
        finish();
    }

    private void confirmDelete()
    {
        final ViewDialogMessage dialog = new ViewDialogMessage(ActivityScheduleTimelineDetail.this);
        dialog.setTitle(getResources().getString(R.string.base_string_delete_title));
        dialog.setMessage(getResources().getString(R.string.base_string_delete_message));
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                doDelete();
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void doDelete()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","target"};
                String[] value = new String[]{getToken(), getParam(), extra_id};
                ActionSchedule schedule = new ActionSchedule(ActivityScheduleTimelineDetail.this);
                schedule.setArrayPOST(field, value);
                schedule.executeDelete();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
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
                Toast.makeText(ActivityScheduleTimelineDetail.this, msg, Toast.LENGTH_SHORT).show();
                if(isSuccess)
                {
                    back();
                }
            }
        }.execute();
    }

    private void confirmAddToMe()
    {
        final ViewDialogMessage dialog = new ViewDialogMessage(ActivityScheduleTimelineDetail.this);
        dialog.setTitle(getResources().getString(R.string.schedule_timeline_detail_addtome_title));
        dialog.setMessage(getResources().getString(R.string.schedule_timeline_detail_addtome_message));
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                doAddTome();
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void doAddTome()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg, ntype = "", ntitle = "", nmessage = "", nticker = "";
            ArrayList<String> dateList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dateList = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","target"};
                String[] value = new String[]{getToken(), getParam(), extra_id};
                ActionSchedule schedule = new ActionSchedule(ActivityScheduleTimelineDetail.this);
                schedule.setArrayPOST(field, value);
                schedule.executeRepost();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
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
                Toast.makeText(ActivityScheduleTimelineDetail.this, msg, Toast.LENGTH_SHORT).show();
                if(isSuccess)
                {
                    btn_add_tome.setEnabled(false);
                    if(dateList.size() > 0)
                    {
                        String token = getToken();
                        String param = getParam();
                        for(int i = 0;i < dateList.size();i++)
                        {
                            HelperGlobal.setAlarmManager(ActivityScheduleTimelineDetail.this,5000+i, ntype, ntitle, nmessage, nticker, token, param, dateList.get(i), "run.schedule");
                        }
                    }
                }
            }
        }.execute();
    }



    private void back()
    {
        Intent i = null;
        if(extra_mode.equals("timeline"))
        {
            i = new Intent(ActivityScheduleTimelineDetail.this, ActivityScheduleTimeline.class);
        }
        else if(extra_mode.equals("notification"))
        {
            i = new Intent(ActivityScheduleTimelineDetail.this, ActivityNotification.class);
        }
        else {
            i = new Intent(ActivityScheduleTimelineDetail.this, ActivitySchedule.class);
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
