package com.android.mouj.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.activity.ActivityPostDetail;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.FragmentInterface;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionSearch;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ekobudiarto on 3/2/16.
 */
public class FragmentSearchPost extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_SEARCH_TAB_POST = "tag:fragment-search-tab-post";

    String selected_keyword = "", param, token;
    ListView main_list;
    ArrayList<String> post_id, post_title, post_date, post_users;
    ArrayList<String> tempid, tempname, tempdate, tempusers;
    DisplayImageOptions opt;
    ImageLoader loader;
    PostAdapter adapter;
    int l = 10, o = 0;
    boolean isSuccess = false;
    String msg;
    boolean searchRunning = false;
    Thread searchThread;
    boolean isFirst = true;
    Tracker tracker;

    public void setKeyword(String keyword, boolean isFirst)
    {
        this.isFirst = isFirst;
        if(keyword.length() > 2)
        {
            if(!isFirst)
            {
                o = 0;
                adapter.clear();
                adapter.notifyDataSetChanged();
                post_id = new ArrayList<String>();
                post_title = new ArrayList<String>();
                post_date = new ArrayList<String>();
                post_users = new ArrayList<String>();
                searchRunning = true;
                this.selected_keyword = keyword;
                runThread();
            }
            else {
                o = 0;
                searchRunning = false;
                this.selected_keyword = keyword;
            }
        }
        else{
            o = 0;
            searchRunning = false;
            this.selected_keyword = "";
            if(!isFirst)
            {
                searchRunning = true;
                adapter.clear();
                adapter.notifyDataSetChanged();
                runThread();
            }

        }
    }

    public void setParam(String p, String t)
    {
        this.param = p;
        this.token = t;
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) activity.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Cari Artikel");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_search_tab_post, container, false);
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
            runThread();
            GoogleAnalyticsInit();
        }
    }

    private void init()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
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
        searchRunning = true;
        o = 0;

        post_id = new ArrayList<String>();
        post_title = new ArrayList<String>();
        post_date = new ArrayList<String>();
        post_users = new ArrayList<String>();


        adapter = new PostAdapter();
        main_list = (ListView) activity.findViewById(R.id.search_tab_listview_post);
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(activity, ActivityPostDetail.class);
                i.putExtra("pid",post_id.get(position));
                i.putExtra("src","2");
                i.putExtra("subSrc", "search");
                startActivity(i);
                activity.finish();
            }
        });
        main_list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //loadMore();
                searchRunning = true;
                runThread();
            }
        });
        if(adapter != null)
        {
            main_list.setAdapter(adapter);
        }
    }

    private void runThread()
    {
        searchThread = null;
        searchThread = new Thread(runnableSearch);
        searchThread.start();
    }



    private Runnable runnableSearch = new Runnable() {
        @Override
        public void run() {

            if(searchRunning)
            {
                searchRunning = false;
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                tempusers = new ArrayList<String>();
                ActionSearch search = new ActionSearch(activity);
                search.setLimit(l);
                search.setOffset(o);
                search.setParameter(token, param);
                search.setKeyword(selected_keyword);
                search.executeSearchPost();
                if(search.getSuccess())
                {
                    isSuccess = true;
                    if(search.getPID().size() > 0)
                    {
                        tempid.addAll(search.getPID());
                        tempname.addAll(search.getPTitle());
                        tempdate.addAll(search.getPDate());
                        tempusers.addAll(search.getPUsers());
                        o = search.getOffset();
                    }
                }
                searchHandler.sendEmptyMessage(0);
            }

        }
    };

    private Handler searchHandler = new Handler(){

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if(isSuccess)
            {
                if(tempid.size() > 0)
                {
                    for(int i = 0;i < tempid.size();i++)
                    {
                        post_id.add(tempid.get(i));
                        post_title.add(tempname.get(i));
                        post_date.add(tempdate.get(i));
                        post_users.add(tempusers.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
                isSuccess = false;
                searchRunning = true;
            }
        }
    };

    public class PostAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public PostAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_title, base_date, base_users;

        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.search_post_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.search_post_list_item_title);
            holder.base_users = (ViewText) convertView.findViewById(R.id.search_post_list_item_users);
            holder.base_date = (ViewText) convertView.findViewById(R.id.search_post_list_item_desc);


            holder.base_title.setRegular();
            holder.base_title.setText(post_title.get(position));
            holder.base_users.setRegular();
            holder.base_users.setText(post_users.get(position));
            holder.base_date.setText(post_date.get(position));

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            post_id.clear();
            post_title.clear();
            post_date.clear();
            post_users.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return post_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[4];
            object[0] = post_id.get(arg0);
            object[1] = post_title.get(arg0);
            object[2] = post_date.get(arg0);
            object[3] = post_users.get(arg0);

            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
