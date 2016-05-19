package com.android.mouj.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.FragmentInterface;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.models.ActionSchedule;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewText;

import java.util.ArrayList;

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
    boolean success = false, isBackActivity = true, isThreadRunning = false;
    ViewText pagetitle;
    ArrayList<String> sch_id, sch_title, sch_location, sch_content, sch_date;
    ArrayList<String> tempid, temptitle, templocation, tempcontent, tempdate;
    int l = 3, o = 0;
    ScheduleAdapter adapter;


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

    public void notifyFragment()
    {
        fetch();
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
        sch_id = new ArrayList<String>();
        sch_title = new ArrayList<String>();
        sch_location = new ArrayList<String>();
        sch_content = new ArrayList<String>();
        sch_date = new ArrayList<String>();
        adapter = new ScheduleAdapter();
        imagebuttonBack = (ImageButton) activity.findViewById(R.id.fragment_schedule_list_back);
        pagetitle = (ViewText) activity.findViewById(R.id.fragment_schedule_list_pagetitle);
        listView = (ListView) activity.findViewById(R.id.fragment_schedule_list_listview);

        pagetitle.setText(targetName);
        listView.setAdapter(adapter);
        imagebuttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .remove(FragmentScheduleList.this).commit();
            }
        });
        fetch();
    }

    private void fetch()
    {
        o = 0;
        isThreadRunning = true;
        startThread();
    }

    private void loadMore()
    {
        isThreadRunning = true;
        startThread();
    }

    private void startThread()
    {
        threadSchedule = null;
        threadSchedule = new Thread(runnableSchedule);
        threadSchedule.start();
    }

    private Runnable runnableSchedule = new Runnable() {
        @Override
        public void run() {

            if(isThreadRunning)
            {
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                templocation = new ArrayList<String>();
                tempcontent = new ArrayList<String>();
                tempdate = new ArrayList<String>();

                ActionSchedule schedule = new ActionSchedule(activity);
                schedule.setParam(activity.getParam(), activity.getToken());
                schedule.setL(l);
                schedule.setO(o);
                schedule.setParameterTarget(targetID);
                schedule.executeScheduleSingleUsers();
                if(schedule.getSuccess())
                {
                    success = true;
                    tempid.addAll(schedule.getSchID());
                    temptitle.addAll(schedule.getSchTitle());
                    templocation.addAll(schedule.getSchLocation());
                    tempcontent.addAll(schedule.getSchDesc());
                    tempdate.addAll(schedule.getSchDate());
                    o = schedule.getOffset();
                }
                else
                {
                    success = false;
                    message = schedule.getMessage();
                }
                handlerSchedule.sendEmptyMessage(0);
            }
        }
    };

    private Handler handlerSchedule = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(success)
            {
                isThreadRunning = false;
                if(tempid.size() > 0)
                {
                    for(int i = 0;i < tempid.size();i++)
                    {
                        sch_id.add(tempid.get(i));
                        sch_title.add(temptitle.get(i));
                        sch_location.add(templocation.get(i));
                        sch_content.add(tempcontent.get(i));
                        sch_date.add(tempdate.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    listView.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            loadMore();
                        }
                    });
                }
                success = false;
            }
            else
            {
                success = false;
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        }
    };


    public class ScheduleAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ScheduleAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText item_title, item_location, item_month, item_day, item_year, item_content, item_time;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_maps_fragment_schedule, null);
            }
            holder = new ViewHolder();
            holder.item_month = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_month);
            holder.item_day = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_day);
            holder.item_year = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_year);
            holder.item_title = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_title);
            holder.item_content = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_content);
            holder.item_location = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_location);
            holder.item_time = (ViewText) convertView.findViewById(R.id.map_fragment_schedule_item_time);

            String date = sch_date.get(position);
            holder.item_month.setSemiBold();
            holder.item_month.setText(TimeUtils.convertFormatDate(date, "M"));
            holder.item_day.setSemiBold();
            holder.item_day.setText(TimeUtils.convertFormatDate(date, "d"));
            holder.item_year.setText(TimeUtils.convertFormatDate(date, "y"));
            holder.item_year.setSemiBold();
            holder.item_title.setSemiBold();
            holder.item_title.setText(sch_title.get(position));
            holder.item_content.setText(sch_content.get(position));
            holder.item_location.setText(sch_location.get(position));
            holder.item_time.setText(TimeUtils.convertFormatDate(date, "H"));

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            sch_id.clear();
            sch_title.clear();
            sch_location.clear();
            sch_content.clear();
            sch_date.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return sch_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[5];
            object[0] = sch_id.get(arg0);
            object[1] = sch_title.get(arg0);
            object[2] = sch_content.get(arg0);
            object[3] = sch_location.get(arg0);
            object[5] = sch_date.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
