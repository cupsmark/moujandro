package com.mouj.app.fragment;

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
import android.widget.ImageView;
import android.widget.ListView;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSearch;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 3/2/16.
 */
public class FragmentSearchHashtag extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_SEARCH_TAB_HASHTAG = "tag:fragment-search-tab-hashtag";


    String selected_keyword = "", param, token;
    ListView main_list;
    ArrayList<String> tag_id, tag_name, tag_count;
    ArrayList<String> tempid, tempname, tempcount;
    DisplayImageOptions opt;
    ImageLoader loader;
    TagAdapter adapter;
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
                tag_id = new ArrayList<String>();
                tag_name = new ArrayList<String>();
                tag_count = new ArrayList<String>();
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
                //fetch();
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
        HelperGlobal.SendAnalytic(tracker, "Page Cari Hashtag");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_search_tab_hashtag, container, false);
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

        tag_id = new ArrayList<String>();
        tag_name = new ArrayList<String>();
        tag_count = new ArrayList<String>();


        adapter = new TagAdapter();
        main_list = (ListView) activity.findViewById(R.id.search_tab_listview_hashtag);
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.setTag(tag_name.get(position));
                Intent i = new Intent(activity, MainActivity.class);
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
                tempcount = new ArrayList<String>();
                ActionSearch search = new ActionSearch(activity);
                search.setLimit(l);
                search.setOffset(o);
                search.setParameter(token, param);
                search.setKeyword(selected_keyword);
                search.executeSearchTag();
                if(search.getSuccess())
                {
                    isSuccess = true;
                    if(search.getTagID().size() > 0)
                    {
                        tempid.addAll(search.getTagID());
                        tempname.addAll(search.getTagName());
                        tempcount.addAll(search.getTagCount());
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
                        tag_id.add(tempid.get(i));
                        tag_name.add(tempname.get(i));
                        tag_count.add(tempcount.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
                isSuccess = false;
                searchRunning = true;
            }
        }
    };

    public class TagAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public TagAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_title;
            ViewText base_count;
            ImageView base_thumb;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.search_tag_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.search_tag_list_item_title);
            holder.base_count = (ViewText) convertView.findViewById(R.id.search_tag_list_item_count);
            holder.base_thumb = (ImageView) convertView.findViewById(R.id.search_tag_list_item_thumb);

            holder.base_title.setRegular();
            holder.base_title.setText(tag_name.get(position));
            holder.base_count.setText(tag_count.get(position));

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            tag_id.clear();
            tag_name.clear();
            tag_count.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tag_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[3];
            object[0] = tag_id.get(arg0);
            object[1] = tag_name.get(arg0);
            object[2] = tag_count.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
