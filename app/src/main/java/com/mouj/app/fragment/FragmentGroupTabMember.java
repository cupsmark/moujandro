package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mouj.app.activity.ActivityProfile;
import com.mouj.app.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionGroup;
import com.mouj.app.view.ViewDialogMessage;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewText;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 2/29/16.
 */
public class FragmentGroupTabMember extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_GROUP_TAB_MEMBER = "tag:fragment-group-tab-member";

    ListView mainList;
    DisplayImageOptions opt;
    ImageLoader loader;
    String token, param, gpid, gptitle;
    int l = 1, o = 0;
    FollAdapter adapter;
    ArrayList<String> foll_id, foll_name, foll_desc, foll_thumb;
    boolean isEdit = false;
    ViewEditText edittext_keyword;
    Thread searchThread;
    String messages,selected_value;
    boolean searchRunning = true;
    boolean isSuccess = false;

    public void setParam(String p, String t)
    {
        this.param = p;
        this.token = t;
    }

    public void setGPID(String gpid)
    {
        this.gpid = gpid;
    }

    public void setGPTitle(String t)
    {
        this.gptitle = t;
    }



    public void setEdit(boolean edit)
    {
        this.isEdit = edit;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_group_list_post_member, container, false);
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
            fetch();
        }
    }


    private void init()
    {
        foll_id = new ArrayList<String>();
        foll_name = new ArrayList<String>();
        foll_desc = new ArrayList<String>();
        foll_thumb = new ArrayList<String>();
        selected_value = "";

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

        mainList = (ListView) activity.findViewById(R.id.group_list_post_listview_member);
        adapter = new FollAdapter();
        edittext_keyword = (ViewEditText) activity.findViewById(R.id.group_list_post_searchbox);
        edittext_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    o = 0;
                    searchRunning = true;
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    selected_value = edittext_keyword.getText().toString();
                    searchThread = null;
                    searchThread = new Thread(runnableSearch);
                    searchThread.start();
                }
                if(s.length() <= 0){
                    selected_value = "";
                    searchRunning = false;
                    fetch();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        notifyListView();
    }

    private void runThread()
    {
        searchThread = null;
        searchThread = new Thread(runnableSearch);
        searchThread.start();
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            ArrayList<String> tempid, tempname, tempdesc, tempthumb;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter.clear();
                adapter.notifyDataSetChanged();
                o = 0;
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                msg = "";
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.setGPID(gpid);
                group.setKeyword(selected_value);
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
                        foll_id.addAll(tempid);
                        foll_name.addAll(tempname);
                        foll_desc.addAll(tempdesc);
                        foll_thumb.addAll(tempthumb);
                        searchHandler.sendEmptyMessage(0);
                    }
                }
                else {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyListView()
    {
        if(adapter != null)
        {
            mainList.setAdapter(adapter);
        }
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isEdit)
                {
                    Intent intent = new Intent(activity, ActivityProfile.class);
                    intent.putExtra("target",foll_id.get(position));
                    intent.putExtra("mode","view");
                    intent.putExtra("src","4");
                    intent.putExtra("gpid",gpid);
                    intent.putExtra("gptitle",gptitle);
                    startActivity(intent);
                    activity.finish();
                }
            }
        });
        mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(isEdit)
                {
                    deleteMember(position);
                }
                return false;
            }
        });
        mainList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            ArrayList<String> tempid, tempname, tempdesc, tempthumb;
            String msg;
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
                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.setGPID(gpid);
                group.setKeyword(selected_value);
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
                            foll_id.add(tempid.get(i));
                            foll_name.add(tempname.get(i));
                            foll_desc.add(tempdesc.get(i));
                            foll_thumb.add(tempthumb.get(i));
                        }
                        searchHandler.sendEmptyMessage(0);
                    }
                }
            }
        }.execute();
    }

    private void deleteMember(final int i)
    {
        final ViewDialogMessage dialog = new ViewDialogMessage(activity);
        dialog.setTitle(activity.getResources().getString(R.string.member_title_delete));
        dialog.setMessage(activity.getResources().getString(R.string.base_string_delete_message));
        dialog.setCancelable(true);
        dialog.show();


        dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                doDelete(foll_id.get(i));
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void doDelete(final String i)
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg = "";
            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","gpid","foll_id"};
                String[] value = new String[]{token,param,gpid,i};
                ActionGroup group = new ActionGroup(activity);
                group.setPostValue(field, value);
                group.delMember();
                if(group.getSuccess())
                {
                    isSuccess = group.getSuccess();
                }
                msg = group.getMessage();

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    for(int x = 0;x < foll_id.size();x++)
                    {
                        if(foll_id.get(x).equals(i))
                        {
                            foll_id.remove(x);
                            foll_name.remove(x);
                            foll_desc.remove(x);
                            foll_thumb.remove(x);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private Runnable runnableSearch = new Runnable() {
        @Override
        public void run() {

            if(searchRunning)
            {
                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.setGPID(gpid);
                group.executeListFollower();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    for(int i = 0;i < group.getFollID().size();i++)
                    {
                        foll_id.add(group.getFollID().get(i));
                        foll_name.add(group.getFollName().get(i));
                        foll_desc.add(group.getFollDesc().get(i));
                        foll_thumb.add(group.getFollThumb().get(i));
                    }
                    o = group.getOffset();
                }
                else
                {
                    messages = group.getMessage();
                }
                searchHandler.sendEmptyMessage(0);
            }

        }
    };


    private Handler searchHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isSuccess)
            {
                adapter.notifyDataSetChanged();
                isSuccess = false;
            }
            else {
                Toast.makeText(activity, messages, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class FollAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public FollAdapter() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


            loader.displayImage(HelperGlobal.BASE_UPLOAD + foll_thumb.get(position), holder.image_ava, opt);


            convertView.setTag(holder);


            return convertView;
        }

        public void clear()
        {
            foll_id.clear();
            foll_name.clear();
            foll_desc.clear();
            foll_thumb.clear();
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
