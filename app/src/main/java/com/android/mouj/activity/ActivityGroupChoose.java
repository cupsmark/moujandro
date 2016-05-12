package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.FailReason;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.models.ActionGroup;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityGroupChoose extends BaseActivity {

    ImageButton imagebutton_back;
    ViewButton button_done;
    String file, name, desc, foll_count;
    ArrayList<String> foll_id, foll_name, foll_desc, foll_thumb;
    int l = 10, o = 0;
    ListView userList;
    FollAdapter adapter;
    DisplayImageOptions opt;
    ImageLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_choose);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        file = getIntent().getExtras().getString("file");
        name = getIntent().getExtras().getString("name");
        desc = getIntent().getExtras().getString("desc");
        imagebutton_back = (ImageButton) findViewById(R.id.group_imagebutton_back);
        button_done = (ViewButton) findViewById(R.id.group_btn_next);
        userList = (ListView) findViewById(R.id.group_listview_choose);

        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityGroupChoose.this, ActivityGroup.class);
                startActivity(i);
                finish();
            }
        });
        imagebutton_back.setOnClickListener(new View.OnClickListener() {
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

            ArrayList<String> tempid, tempname, tempdesc, tempthumb;
            String msg;
            boolean isSuccess = false;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                foll_id = new ArrayList<String>();
                foll_name = new ArrayList<String>();
                foll_desc = new ArrayList<String>();
                foll_thumb = new ArrayList<String>();
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                msg = "";
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(ActivityGroupChoose.this);
                group.setParam(getParam(), getToken());
                group.setL(l);
                group.setO(o);
                group.executeListFollower();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(group.getFollID());
                    tempname.addAll(group.getFollName());
                    tempdesc.addAll(group.getFollDesc());
                    tempthumb.addAll(group.getFollThumb());
                    foll_count = Integer.toString(group.getNumRows());
                    o = group.getOffset();
                }
                else
                {
                    msg = group.getMessage();
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
                        foll_id.addAll(tempid);
                        foll_name.addAll(tempname);
                        foll_desc.addAll(tempdesc);
                        foll_thumb.addAll(tempthumb);
                        foll_count = Integer.toString(tempid.size());
                        adapter = new FollAdapter();
                        notifyListView();
                    }
                }
                else {
                    Toast.makeText(ActivityGroupChoose.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyListView()
    {
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        userList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        if(adapter != null)
        {
            if(foll_id.size() > 0)
            {
                userList.setAdapter(adapter);
            }
        }
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            ArrayList<String> tempid, tempname, tempdesc, tempthumb;
            String msg;
            boolean isSuccess = false;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                msg = "";
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(ActivityGroupChoose.this);
                group.setParam(getParam(), getToken());
                group.setL(l);
                group.setO(o);
                group.executeListFollower();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(group.getFollID());
                    tempname.addAll(group.getFollName());
                    tempdesc.addAll(group.getFollDesc());
                    tempthumb.addAll(group.getFollThumb());
                    o = group.getOffset();
                }
                else
                {
                    msg = group.getMessage();
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
                        for(int i = 0;i < tempid.size();i++) {
                            foll_id.addAll(tempid);
                            foll_name.addAll(tempname);
                            foll_desc.addAll(tempdesc);
                            foll_thumb.addAll(tempthumb);
                            foll_count = Integer.toString(tempid.size());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }.execute();
    }

    private void back()
    {
        Intent i = new Intent(ActivityGroupChoose.this, ActivityGroupForm.class);
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

    public class FollAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public FollAdapter() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText txt_name;
            ViewText txt_desc;
            ImageView image_ava;
            ImageButton imagebutton_switcher;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            //ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_group_choose, null);
            }


            final ViewHolder holder = new ViewHolder();
            holder.txt_name = (ViewText) convertView.findViewById(R.id.group_choose_item_textview_name);
            holder.txt_desc = (ViewText) convertView.findViewById(R.id.group_choose_item_textview_desc);
            holder.image_ava = (ImageView) convertView.findViewById(R.id.group_choose_item_imageview_thumb);


            holder.txt_name.setText(foll_name.get(position));
            holder.txt_desc.setText(foll_desc.get(position));


            loader.displayImage(HelperGlobal.BASE_UPLOAD + foll_thumb, holder.image_ava, opt);


            convertView.setTag(holder);


            return convertView;
        }

        public void clear()
        {
            foll_id.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return foll_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return foll_id.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
