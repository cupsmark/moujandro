package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.mouj.BaseActivity;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.external.roomorama.caldroid.CaldroidFragment;
import com.android.mouj.external.roomorama.caldroid.CaldroidListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionSchedule;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivitySchedule extends BaseActivity {

    int wScreen;
    ListView listView;
    ArrayList<String> schedule_hour, schedule_zone, schedule_title, schedule_desc, schedule_id, schedule_date, schedule_repeat;
    ArrayList<String> choosen_date;
    CaldroidFragment caldroidFragment;
    ImageButton button_menu;
    Bundle savedInstance;
    ArrayList<Date> disableDate;
    ScheduleAdapter adapter;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        wScreen = HelperGlobal.GetDimension("w", ActivitySchedule.this);
        savedInstance = savedInstanceState;
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Schedule");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initComponent();
        applyListview();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    private void initComponent()
    {
        schedule_id = new ArrayList<String>();
        schedule_hour = new ArrayList<String>();
        schedule_zone = new ArrayList<String>();
        schedule_title = new ArrayList<String>();
        schedule_desc = new ArrayList<String>();
        schedule_date = new ArrayList<String>();
        schedule_repeat = new ArrayList<String>();
        disableDate = new ArrayList<Date>();
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        caldroidFragment = new CaldroidFragment();
        if (savedInstance != null) {
            caldroidFragment.restoreStatesFromKey(savedInstance,
                    "CALDROID_SAVED_STATE");
        }
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putInt(CaldroidFragment.MIN_DATE, cal.get(Calendar.DAY_OF_MONTH));
            args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
            //args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            caldroidFragment.setArguments(args);
        }


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                caldroidFragment.moveToDate(date);
                GetListActivityByDay(date);
            }

            @Override
            public void onChangeMonth(int month, int year) {


                GetByMonth(month, year);
            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    //Toast.makeText(getApplicationContext(), "Caldroid view is created", Toast.LENGTH_SHORT).show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        LinearLayout btn_today = (LinearLayout) findViewById(R.id.schedule_linear_button_today);
        btn_today.getLayoutParams().width = (wScreen / 3) - 10;
        LinearLayout btn_create = (LinearLayout) findViewById(R.id.schedule_linear_button_createnew);
        btn_create.getLayoutParams().width = (wScreen / 3) - 10;
        LinearLayout btn_more = (LinearLayout) findViewById(R.id.schedule_linear_button_more);
        btn_more.getLayoutParams().width = (wScreen / 3) - 10;
        button_menu = (ImageButton) findViewById(R.id.schedule_imagebutton_menu);
        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggleMenu(true);
                back();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySchedule.this, ActivityScheduleNew.class);
                i.putExtra("mode-action","create");
                startActivity(i);
                finish();
            }
        });
        btn_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caldroidFragment.moveToDate(new Date());
            }
        });
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySchedule.this, ActivityScheduleTimeline.class);
                intent.putExtra("src","sch");
                startActivity(intent);
                finish();
            }
        });

    }

    private void back()
    {
        //HelperGlobal.ExitApp(ActivitySchedule.this,getResources().getString(R.string.base_string_exit_title));
        Intent i = new Intent(ActivitySchedule.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void GetByMonth(final int month, final int year)
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            ArrayList<String> temp_choosen;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                temp_choosen = new ArrayList<String>();
                choosen_date = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(ActivitySchedule.this);
                schedule.setParam(getParam(), getToken());
                schedule.setMonth(month);
                schedule.setYear(year);
                schedule.setIsDay(false);
                schedule.getSchedule();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
                    temp_choosen.addAll(schedule.getSchDate());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    if(temp_choosen.size() > 0)
                    {
                        for(int i = 0;i < temp_choosen.size();i++)
                        {
                            try {
                                choosen_date.add(temp_choosen.get(i));
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = new Date();
                                d = dateFormat.parse(temp_choosen.get(i));
                                caldroidFragment.setSelectedDate(d);
                                caldroidFragment.refreshView();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }
            }
        }.execute();
    }

    private void GetListActivityByDay(final Date d)
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            ArrayList<String> temp_hour, temp_zone, temp_title, temp_desc, temp_id, temp_date, temp_repeat;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                temp_id = new ArrayList<String>();
                temp_hour = new ArrayList<String>();
                //schedule_zone = new ArrayList<String>();
                temp_title = new ArrayList<String>();
                temp_desc = new ArrayList<String>();
                temp_date = new ArrayList<String>();
                temp_repeat = new ArrayList<String>();
                schedule_id = new ArrayList<String>();
                schedule_hour = new ArrayList<String>();
                schedule_zone = new ArrayList<String>();
                schedule_title = new ArrayList<String>();
                schedule_desc = new ArrayList<String>();
                schedule_date = new ArrayList<String>();
                schedule_repeat = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(ActivitySchedule.this);
                schedule.setParam(getParam(), getToken());
                schedule.setIsDay(true);
                schedule.setDay(d);
                schedule.getSchedule();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
                    temp_id.addAll(schedule.getSchID());
                    temp_title.addAll(schedule.getSchTitle());
                    temp_date.addAll(schedule.getSchDate());
                    temp_desc.addAll(schedule.getSchDesc());
                    temp_repeat.addAll(schedule.getSchRepeat());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    if(temp_id.size() > 0)
                    {
                        for(int i = 0;i < temp_id.size();i++)
                        {
                            try {
                                schedule_id.add(temp_id.get(i));
                                schedule_title.add(temp_title.get(i));
                                schedule_desc.add(temp_desc.get(i));
                                schedule_date.add(temp_date.get(i));
                                schedule_repeat.add(temp_repeat.get(i));
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = sdf.parse(schedule_date.get(i));
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                String hours = sdf2.format(d);
                                SimpleDateFormat sdf3 = new SimpleDateFormat("aa");
                                String zone = sdf3.format(d);
                                schedule_zone.add(zone);
                                schedule_hour.add(hours);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter = new ScheduleAdapter();
                        applyListview();
                    }

                }
            }
        }.execute();
    }

    private void applyListview()
    {
        listView = (ListView) findViewById(R.id.schedule_listview_activity);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ActivitySchedule.this, ActivityScheduleTimelineDetail.class);
                i.putExtra("mode-src","calendar");
                i.putExtra("target", schedule_id.get(position));
                startActivity(i);
                finish();
            }
        });
        if(adapter != null)
        {
            listView.setAdapter(adapter);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class ScheduleAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public ScheduleAdapter() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText textHour;
            ViewText textZone;
            ViewText textTitle;
            ViewText textDesc;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.schedule_layout_item_activity, null);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            holder = new ViewHolder();
            holder.textHour = (ViewText) convertView.findViewById(R.id.schedule_listview_item_time);
            holder.textHour.setText(schedule_hour.get(position));
            holder.textZone = (ViewText) convertView.findViewById(R.id.schedule_listview_item_zone);
            holder.textZone.setText(schedule_zone.get(position));
            holder.textTitle = (ViewText) convertView.findViewById(R.id.schedule_listview_item_title);
            holder.textTitle.setText(schedule_title.get(position));
            holder.textDesc = (ViewText) convertView.findViewById(R.id.schedule_listview_item_desc);
            holder.textDesc.setText(schedule_desc.get(position));

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            schedule_id.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return schedule_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return schedule_id.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
