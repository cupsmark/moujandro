package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
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
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 3/16/16.
 */
public class FragmentSearchGroup extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_SEARCH_TAB_GROUP = "tag:fragment-search-tab-group";

    String selected_keyword = "", param, token;
    ListView main_list;
    ArrayList<String> group_id, group_name, group_thumb, group_desc, group_join,group_join_title;
    ArrayList<String> tempid, tempname, tempthumb, tempdesc,tempjoin,tempjointitle;
    DisplayImageOptions opt;
    ImageLoader loader;
    GroupAdapter adapter;
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
                group_id = new ArrayList<String>();
                group_name = new ArrayList<String>();
                group_thumb = new ArrayList<String>();
                group_desc = new ArrayList<String>();
                group_join = new ArrayList<String>();
                group_join_title = new ArrayList<String>();
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
        HelperGlobal.SendAnalytic(tracker, "Page Cari Group");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_search_tab_group, container, false);
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

        group_id = new ArrayList<String>();
        group_name = new ArrayList<String>();
        group_thumb = new ArrayList<String>();
        group_desc = new ArrayList<String>();
        group_join = new ArrayList<String>();
        group_join_title = new ArrayList<String>();


        adapter = new GroupAdapter();
        main_list = (ListView) activity.findViewById(R.id.search_tab_listview_group);
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    private void request_invite(final ViewButton button_source, final String gpid)
    {
        new AsyncTask<Void, Integer, String>()
        {
            boolean success = false;
            String msg;
            ViewLoadingDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","gpid"};
                String[] value = new String[]{token,param,gpid};
                ActionSearch search = new ActionSearch(activity);
                search.setPostParameter(field, value);
                search.executeRequestJoin();
                if(search.getSuccess())
                {
                    success = true;
                }
                msg = search.getMessage();
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(success)
                {
                    button_source.setEnabled(false);
                    button_source.setBackgroundResource(R.drawable.bg_rounded_semiblack);
                    button_source.setTextColor(activity.getResources().getColor(R.color.base_white));
                    button_source.setText(activity.getResources().getString(R.string.group_basetitle_pending));
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
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
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempjoin = new ArrayList<String>();
                tempjointitle = new ArrayList<String>();
                ActionSearch search = new ActionSearch(activity);
                search.setLimit(l);
                search.setOffset(o);
                search.setParameter(token, param);
                search.setKeyword(selected_keyword);
                search.executeSearchGroup();
                if(search.getSuccess())
                {
                    isSuccess = true;
                    if(search.getGRID().size() > 0)
                    {
                        tempid.addAll(search.getGRID());
                        tempname.addAll(search.getGRName());
                        tempdesc.addAll(search.getGRDesc());
                        tempthumb.addAll(search.getGRThumb());
                        tempjoin.addAll(search.getGRJoin());
                        tempjointitle.addAll(search.getGRJoinTitle());
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
                        group_id.add(tempid.get(i));
                        group_name.add(tempname.get(i));
                        group_desc.add(tempdesc.get(i));
                        group_thumb.add(tempthumb.get(i));
                        group_join.add(tempjoin.get(i));
                        group_join_title.add(tempjointitle.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
                isSuccess = false;
                searchRunning = true;
            }
        }
    };


    public class GroupAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public GroupAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_title;
            ImageView base_thumb;
            ViewText base_desc;
            ViewButton base_button_follow;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.step_following_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.step_following_item_title);
            holder.base_thumb = (ImageView) convertView.findViewById(R.id.step_following_item_thumb);
            holder.base_desc = (ViewText) convertView.findViewById(R.id.step_following_item_desc);
            holder.base_button_follow = (ViewButton) convertView.findViewById(R.id.step_following_item_button_follow);

            holder.base_title.setRegular();
            holder.base_title.setText(group_name.get(position));
            holder.base_desc.setText(group_desc.get(position));
            holder.base_desc.setRegular();
            if(group_join.get(position).equals("1"))
            {
                holder.base_button_follow.setTextColor(activity.getResources().getColor(R.color.base_white));
                holder.base_button_follow.setVisibility(View.VISIBLE);
                holder.base_button_follow.setEnabled(true);
                holder.base_button_follow.setText(group_join_title.get(position));
                final ViewHolder finalHolder = holder;
                holder.base_button_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        request_invite(finalHolder.base_button_follow, group_id.get(position));
                    }
                });
            }
            else if(group_join.get(position).equals("2"))
            {
                holder.base_button_follow.setBackgroundResource(R.drawable.bg_rounded_semiblack);
                holder.base_button_follow.setTextColor(activity.getResources().getColor(R.color.base_white));
                holder.base_button_follow.setVisibility(View.VISIBLE);
                holder.base_button_follow.setEnabled(false);
                holder.base_button_follow.setText(group_join_title.get(position));
            }
            else
            {
                holder.base_button_follow.setVisibility(View.GONE);
                holder.base_button_follow.setText(group_join_title.get(position));
            }


            loader.displayImage(HelperGlobal.BASE_UPLOAD + group_thumb.get(position), holder.base_thumb, opt);

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            group_id.clear();
            group_name.clear();
            group_desc.clear();
            group_thumb.clear();
            group_join.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return group_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            Object[] object = new Object[5];
            object[0] = group_id.get(arg0);
            object[1] = group_name.get(arg0);
            object[2] = group_desc.get(arg0);
            object[3] = group_thumb.get(arg0);
            object[4] = group_join.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }

}
