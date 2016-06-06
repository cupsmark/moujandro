package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.activity.ActivityPostDetail;
import com.mouj.app.activity.ActivityScheduleTimelineDetail;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionSearch;
import com.mouj.app.util.TimeUtils;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 11/2/15.
 */
public class FragmentNotification extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_NOTIF = "tag:fragment-notif";

    ImageButton btn_back;
    ListView listviewNotification;
    ViewLoadingDialog dialog;
    ArrayList<String> notif_id,notif_thumb, notif_desc, notif_title, notif_date, notif_type, notif_primary;
    int l = 5,o = 0;
    NotifAdapter adapter;
    DisplayImageOptions opt;
    ImageLoader loader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.activity_notification, container, false);
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
        notif_id = new ArrayList<String>();
        notif_thumb = new ArrayList<String>();
        notif_title = new ArrayList<String>();
        notif_desc = new ArrayList<String>();
        notif_date = new ArrayList<String>();
        notif_type = new ArrayList<String>();
        notif_primary = new ArrayList<String>();

        btn_back = (ImageButton) activity.findViewById(R.id.notification_imagebutton_back);
        listviewNotification = (ListView) activity.findViewById(R.id.notification_listview);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fetch();
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess;
            String msg;

            ArrayList<String> tempid, temptitle, tempthumb, tempdesc, tempdate, temptype, tempprimary;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();

                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempprimary = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSearch search = new ActionSearch(activity);
                search.setParameter(activity.getToken(),activity.getParam());
                search.setLimit(l);
                search.setOffset(o);
                search.executeNotification();
                if(search.getSuccess())
                {
                    tempid.addAll(search.getNotifID());
                    temptitle.addAll(search.getNotifUsers());
                    tempthumb.addAll(search.getNotifThumb());
                    tempdesc.addAll(search.getNotifTitle());
                    tempdate.addAll(search.getNotifDate());
                    temptype.addAll(search.getNotifType());
                    tempprimary.addAll(search.getNotifPrimary());
                    o = search.getOffset();
                    isSuccess = true;
                }
                else {
                    isSuccess = false;
                    msg = search.getMessage();
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
                    notif_id.addAll(tempid);
                    notif_title.addAll(temptitle);
                    notif_desc.addAll(tempdesc);
                    notif_thumb.addAll(tempthumb);
                    notif_date.addAll(tempdate);
                    notif_type.addAll(temptype);
                    notif_primary.addAll(tempprimary);
                    adapter = new NotifAdapter();
                    notifyListView();
                }
                if(!isSuccess)
                {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyListView()
    {
        listviewNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if(notif_type.get(position).equals("post"))
                {
                    i = new Intent(activity, ActivityPostDetail.class);
                    i.putExtra("pid",notif_primary.get(position));
                    i.putExtra("src","2");
                    i.putExtra("subSrc","notification");
                }
                else
                {
                    i = new Intent(activity, ActivityScheduleTimelineDetail.class);
                    i.putExtra("target",notif_primary.get(position));
                    i.putExtra("mode-src","notification");
                }
                startActivity(i);
                activity.finish();
            }
        });
        listviewNotification.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        if(adapter != null)
        {
            if(notif_id.size() > 0)
            {
                listviewNotification.setAdapter(adapter);
            }
        }
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess;
            String msg;

            ArrayList<String> tempid, temptitle, tempthumb, tempdesc, tempdate, temptype, tempprimary;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempprimary = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSearch search = new ActionSearch(activity);
                search.setParameter(activity.getToken(),activity.getParam());
                search.setLimit(l);
                search.setOffset(o);
                search.executeNotification();
                if(search.getSuccess())
                {
                    tempid.addAll(search.getNotifID());
                    temptitle.addAll(search.getNotifUsers());
                    tempthumb.addAll(search.getNotifThumb());
                    tempdesc.addAll(search.getNotifTitle());
                    tempdate.addAll(search.getNotifDate());
                    temptype.addAll(search.getNotifType());
                    tempprimary.addAll(search.getNotifPrimary());
                    o = search.getOffset();
                    isSuccess = true;
                }
                else {
                    isSuccess = false;
                    msg = search.getMessage();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    for(int i = 0;i < tempid.size();i++)
                    {
                        notif_id.add(tempid.get(i));
                        notif_title.add(temptitle.get(i));
                        notif_desc.add(tempdesc.get(i));
                        notif_thumb.add(tempthumb.get(i));
                        notif_date.add(tempdate.get(i));
                        notif_type.add(temptype.get(i));
                        notif_primary.add(tempprimary.get(i));
                    }
                    adapter = new NotifAdapter();
                }
            }
        }.execute();
    }

    public class NotifAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public NotifAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_title, base_date, base_users;
            ImageView base_thumb;

        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.notification_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.notif_list_item_title);
            holder.base_users = (ViewText) convertView.findViewById(R.id.notif_list_item_users);
            holder.base_date = (ViewText) convertView.findViewById(R.id.notif_list_item_date);
            holder.base_thumb = (ImageView) convertView.findViewById(R.id.notif_list_item_thumb);

            String dates = TimeUtils.convertFormatDate(notif_date.get(position), "dt")+", " +
                    TimeUtils.convertFormatDate(notif_date.get(position),"d") +" " +
                    TimeUtils.convertFormatDate(notif_date.get(position),"m") + " " +
                    TimeUtils.convertFormatDate(notif_date.get(position),"y");
            holder.base_title.setRegular();
            holder.base_title.setText(notif_desc.get(position));
            holder.base_users.setRegular();
            holder.base_users.setText(notif_title.get(position));
            holder.base_date.setRegular();
            holder.base_date.setText(dates);

            loader.displayImage(HelperGlobal.BASE_UPLOAD + notif_thumb.get(position), holder.base_thumb, opt);
            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            notif_id.clear();
            notif_title.clear();
            notif_desc.clear();
            notif_thumb.clear();
            notif_date.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return notif_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[5];
            object[0] = notif_id.get(arg0);
            object[1] = notif_title.get(arg0);
            object[2] = notif_desc.get(arg0);
            object[3] = notif_thumb.get(arg0);
            object[4] = notif_date.get(arg0);

            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
