package com.mouj.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSearch;
import com.mouj.app.util.TimeUtils;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivityNotification extends BaseActivity {

    ImageButton btn_back;
    ListView listviewNotification;
    ViewLoadingDialog dialog;
    ArrayList<String> notif_id,notif_thumb, notif_desc, notif_title, notif_date, notif_type, notif_mod_type, notif_status, notif_read, notif_reference_post;
    int l = 5,o = 0;
    NotifAdapter adapter;
    DisplayImageOptions opt;
    ImageLoader loader;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setShowingMenu(false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .threadPoolSize(5)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);


        opt = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loader = ImageLoader.getInstance();
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Notifikasi");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        notif_id = new ArrayList<String>();
        notif_thumb = new ArrayList<String>();
        notif_title = new ArrayList<String>();
        notif_desc = new ArrayList<String>();
        notif_date = new ArrayList<String>();
        notif_type = new ArrayList<String>();
        notif_mod_type = new ArrayList<String>();
        notif_status = new ArrayList<String>();
        notif_read = new ArrayList<String>();
        notif_reference_post = new ArrayList<String>();

        btn_back = (ImageButton) findViewById(R.id.notification_imagebutton_back);
        listviewNotification = (ListView) findViewById(R.id.notification_listview);
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

            boolean isSuccess;
            String msg;

            ArrayList<String> tempid, temptitle, tempthumb, tempdesc, tempdate, temptype, tempmodtype, tempstatus, tempread, tempreferencepost;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityNotification.this);
                dialog.setCancelable(false);
                dialog.show();

                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempmodtype = new ArrayList<String>();
                tempstatus = new ArrayList<String>();
                tempread = new ArrayList<String>();
                tempreferencepost = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSearch search = new ActionSearch(ActivityNotification.this);
                search.setParameter(getToken(),getParam());
                search.setLimit(l);
                search.setOffset(o);
                search.executeNotification();
                if(search.getSuccess())
                {
                    tempid.addAll(search.getNotifID());
                    temptitle.addAll(search.getNotifUsers());
                    tempthumb.addAll(search.getNotifThumb());
                    tempdesc.addAll(search.getNotifDesc());
                    tempdate.addAll(search.getNotifDate());
                    temptype.addAll(search.getNotifType());
                    tempmodtype.addAll(search.getNotifModType());
                    tempstatus.addAll(search.getNotifStatus());
                    tempread.addAll(search.getNotifRead());
                    tempreferencepost.addAll(search.getNotifReferencePost());
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
                    notif_mod_type.addAll(tempmodtype);
                    notif_status.addAll(tempstatus);
                    notif_read.addAll(tempread);
                    notif_reference_post.addAll(tempreferencepost);
                    adapter = new NotifAdapter();
                    notifyListView();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityNotification.this, msg, Toast.LENGTH_SHORT).show();
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
                if(notif_mod_type.get(position).equals("article"))
                {
                    i = new Intent(ActivityNotification.this, ActivityPostDetail.class);
                    i.putExtra("pid",notif_reference_post.get(position));
                    i.putExtra("src","2");
                    i.putExtra("subSrc","notification");
                }
                else
                {
                    i = new Intent(ActivityNotification.this, ActivityScheduleTimelineDetail.class);
                    i.putExtra("target",notif_reference_post.get(position));
                    i.putExtra("mode-src","notification");
                }
                startActivity(i);
                finish();
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

            ArrayList<String> tempid, temptitle, tempthumb, tempdesc, tempdate, temptype, tempmodtype, tempstatus, tempread, tempreferencepost;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempmodtype = new ArrayList<String>();
                tempstatus = new ArrayList<String>();
                tempread = new ArrayList<String>();
                tempreferencepost = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSearch search = new ActionSearch(ActivityNotification.this);
                search.setParameter(getToken(),getParam());
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
                    tempmodtype.addAll(search.getNotifModType());
                    tempstatus.addAll(search.getNotifStatus());
                    tempread.addAll(search.getNotifRead());
                    tempreferencepost.addAll(search.getNotifReferencePost());
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
                        notif_mod_type.add(tempmodtype.get(i));
                        notif_status.add(tempstatus.get(i));
                        notif_read.add(tempread.get(i));
                        notif_reference_post.add(tempreferencepost.get(i));
                    }
                    adapter = new NotifAdapter();
                }
            }
        }.execute();
    }

    private void back()
    {
        Intent i = new Intent(ActivityNotification.this, MainActivity.class);
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


    public class NotifAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public NotifAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
