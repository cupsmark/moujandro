package com.mouj.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.R;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSchedule;
import com.mouj.app.util.TimeUtils;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivityNotificationService extends BaseActivity {


    ListView listView;
    ViewLoadingDialog dialog;
    int l = 2, o = 0;
    String defaultSorting, src;
    String[] assetType, assetTitle;
    ArrayList<String> pid,ptitle,pdate,puser,puserid,pdesc, rep_from_id, rep_from_name, rep_status;
    ScheduleAdapter adapter;
    Tracker tracker;
    ViewText pagetitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_service);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Schedule Timeline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        src = getIntent().getStringExtra("src");
        pid = new ArrayList<String>();
        ptitle = new ArrayList<String>();
        pdesc = new ArrayList<String>();
        pdate = new ArrayList<String>();
        puser = new ArrayList<String>();
        puserid = new ArrayList<String>();
        rep_from_id = new ArrayList<String>();
        rep_from_name = new ArrayList<String>();
        rep_status = new ArrayList<String>();
        assetType = getResources().getStringArray(R.array.schedule_timeline_sort_key);
        assetTitle = getResources().getStringArray(R.array.schedule_timeline_sort_value);
        defaultSorting = assetType[0];
        pagetitle = (ViewText) findViewById(R.id.main_textview_pagetitle);
        pagetitle.setText("Notifikasi Jadwal");
        listView = (ListView) findViewById(R.id.schedule_timeline_listview);
        executeList();
    }

    private void executeList(){
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid,temptitle,tempuser,tempdesc, tempdate,tempuserid, temp_rep_id, temp_rep_name,temp_rep_status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityNotificationService.this);
                dialog.setCancelable(false);
                dialog.show();

                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempuser = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                tempuserid = new ArrayList<String>();
                temp_rep_status = new ArrayList<String>();
                temp_rep_id = new ArrayList<String>();
                temp_rep_name = new ArrayList<String>();

                if(adapter != null)
                {
                    adapter.clear();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(ActivityNotificationService.this);
                schedule.setParam(getParam(), getToken());
                schedule.setL(l);
                schedule.setO(o);
                schedule.setSortType(defaultSorting);
                schedule.executeScheduleTimeline();
                if(schedule.getSuccess())
                {
                    tempid.addAll(schedule.getSchID());
                    temptitle.addAll(schedule.getSchTitle());
                    tempdesc.addAll(schedule.getSchDesc());
                    tempdate.addAll(schedule.getSchDate());
                    tempuser.addAll(schedule.getSchUser());
                    tempuserid.addAll(schedule.getSchUserID());
                    temp_rep_status.addAll(schedule.getRepostStatus());
                    temp_rep_id.addAll(schedule.getRepostFromID());
                    temp_rep_name.addAll(schedule.getRepostFromName());

                    isSuccess = true;
                    o = schedule.getOffset();
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
                    pid.addAll(tempid);
                    ptitle.addAll(temptitle);
                    pdesc.addAll(tempdesc);
                    pdate.addAll(tempdate);
                    puserid.addAll(tempuserid);
                    puser.addAll(tempuser);
                    rep_status.addAll(temp_rep_status);
                    rep_from_id.addAll(temp_rep_id);
                    rep_from_name.addAll(temp_rep_name);
                    adapter = new ScheduleAdapter();
                    notifyListView();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityNotificationService.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyListView()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        if(adapter != null)
        {
            if(pid.size() > 0)
            {
                listView.setAdapter(adapter);
            }
        }
    }


    private void loadMore(){
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid,temptitle,tempuser,tempdesc, tempdate,tempuserid, temp_rep_id, temp_rep_name, temp_rep_status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempuser = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                tempuserid = new ArrayList<String>();
                temp_rep_id = new ArrayList<String>();
                temp_rep_name = new ArrayList<String>();
                temp_rep_status = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(ActivityNotificationService.this);
                schedule.setParam(getParam(), getToken());
                schedule.setL(l);
                schedule.setO(o);
                schedule.setSortType(defaultSorting);
                schedule.executeScheduleTimeline();
                if(schedule.getSuccess())
                {
                    tempid.addAll(schedule.getSchID());
                    temptitle.addAll(schedule.getSchTitle());
                    tempdesc.addAll(schedule.getSchDesc());
                    tempdate.addAll(schedule.getSchDate());
                    tempuser.addAll(schedule.getSchUser());
                    tempuserid.addAll(schedule.getSchUserID());
                    temp_rep_status.addAll(schedule.getRepostStatus());
                    temp_rep_id.addAll(schedule.getRepostFromID());
                    temp_rep_name.addAll(schedule.getRepostFromName());

                    isSuccess = true;
                    o = schedule.getOffset();
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
                if(isSuccess)
                {
                    if(tempid.size() > 0)
                    {
                        for(int i = 0;i < tempid.size();i++)
                        {
                            pid.add(tempid.get(i));
                            ptitle.add(temptitle.get(i));
                            pdesc.add(tempdesc.get(i));
                            pdate.add(tempdate.get(i));
                            puserid.add(tempuserid.get(i));
                            puser.add(tempuser.get(i));
                            rep_status.add(temp_rep_status.get(i));
                            rep_from_id.add(temp_rep_id.get(i));
                            rep_from_name.add(temp_rep_name.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }




    public class ScheduleAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ScheduleAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_section, base_date_day, base_date_month, base_title, base_desc, base_date_time, repost_from_name;
            LinearLayout repost_container;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.schedule_timeline_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_username);
            holder.base_date_day = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_datenumber);
            holder.base_date_month = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_datename);
            holder.base_desc = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_desc);
            holder.base_date_time = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_time);
            holder.repost_container = (LinearLayout) convertView.findViewById(R.id.schedule_timeline_list_item_repost_container);
            if(rep_status.get(position).equals("1"))
            {
                holder.repost_container.setVisibility(View.VISIBLE);
                holder.repost_from_name = (ViewText) convertView.findViewById(R.id.schedule_timeline_list_item_repost_value);
                holder.repost_from_name.setText(rep_from_name.get(position));
            }

            holder.base_title.setRegular();
            holder.base_title.setText(ptitle.get(position));
            holder.base_desc.setText(pdesc.get(position));
            holder.base_date_day.setRegular();
            holder.base_date_day.setText(TimeUtils.convertFormatDate(pdate.get(position), "d"));
            holder.base_date_month.setText(TimeUtils.convertFormatDate(pdate.get(position), "m"));
            holder.base_date_time.setText(TimeUtils.convertFormatDate(pdate.get(position), "H"));
            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            pid.clear();
            ptitle.clear();
            pdate.clear();
            pdesc.clear();
            puser.clear();
            puserid.clear();
            rep_from_name.clear();
            rep_from_id.clear();
            rep_status.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pid.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[9];
            object[0] = pid.get(arg0);
            object[1] = ptitle.get(arg0);
            object[2] = pdesc.get(arg0);
            object[3] = pdate.get(arg0);
            object[4] = puser.get(arg0);
            object[5] = puserid.get(arg0);
            object[6] = rep_status.get(arg0);
            object[7] = rep_from_id.get(arg0);
            object[8] = rep_from_name.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }



}
