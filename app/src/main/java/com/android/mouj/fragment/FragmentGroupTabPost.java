package com.android.mouj.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.activity.ActivityGroupForm;
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
import com.android.mouj.models.ActionGroup;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewDialogMessage;
import com.android.mouj.view.ViewLoading;
import com.android.mouj.view.ViewText;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ekobudiarto on 2/29/16.
 */
public class FragmentGroupTabPost extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_GROUP_TAB_POST = "tag:fragment-group-tab-post";

    PostAdapter adapter;
    ArrayList<String> p_id, p_title, p_desc, p_date, p_thumb;
    String token, param, gpid, title;
    int l = 10, o = 0;
    ListView mainList;
    DisplayImageOptions opt;
    ImageLoader loader;
    boolean isEdit = false;


    public void setParam(String p, String t)
    {
        this.param = p;
        this.token = t;
    }

    public void setGPID(String gpid)
    {
        this.gpid = gpid;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setEditable(boolean isEditable)
    {
        this.isEdit = isEditable;
    }

    public FragmentGroupTabPost()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_group_list_post_article, container, false);
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

        p_id = new ArrayList<String>();
        p_title = new ArrayList<String>();
        p_desc = new ArrayList<String>();
        p_date = new ArrayList<String>();
        p_thumb = new ArrayList<String>();
        mainList = (ListView) activity.findViewById(R.id.group_post_list_listview);
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc, tempdate, tempthumb;
            ViewLoading loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = (ViewLoading) activity.findViewById(R.id.group_list_post_loading);
                loading.setImageResource(R.drawable.custom_loading);
                loading.show();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                tempthumb = new ArrayList<String>();

            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.setGPID(gpid);
                group.executeListPost();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(group.getPostID());
                    temptitle.addAll(group.getPostTitle());
                    tempdesc.addAll(group.getPostDesc());
                    tempdate.addAll(group.getPostDate());
                    tempthumb.addAll(group.getPostThumb());
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
                if(loading.isShown())
                {
                    loading.dismiss();
                }
                if(isSuccess)
                {
                    p_id.addAll(tempid);
                    p_title.addAll(temptitle);
                    p_desc.addAll(tempdesc);
                    p_date.addAll(tempdate);
                    p_thumb.addAll(tempthumb);
                    adapter = new PostAdapter();
                    sendToListView();

                }
                if(!isSuccess){
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void sendToListView()
    {

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(activity, ActivityPostDetail.class);
                i.putExtra("pid",p_id.get(position));
                i.putExtra("src","post-group");
                i.putExtra("subSrc","post-group");
                i.putExtra("gpid",gpid);
                i.putExtra("gptitle",title);
                startActivity(i);
                activity.finish();
            }
        });
        mainList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(isEdit)
                {
                    deletePost(position);
                }
                return false;
            }
        });
        if(adapter != null)
        {
            if(p_id.size() > 0)
            {
                mainList.setAdapter(adapter);
            }
        }
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc, tempdate, tempthumb;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.setGPID(gpid);
                group.executeListPost();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(group.getPostID());
                    temptitle.addAll(group.getPostTitle());
                    tempdesc.addAll(group.getPostDesc());
                    tempdate.addAll(group.getPostDate());
                    tempthumb.addAll(group.getPostThumb());
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
                        for(int i = 0;i < tempid.size();i++)
                        {
                            p_id.add(tempid.get(i));
                            p_title.add(temptitle.get(i));
                            p_desc.add(tempdesc.get(i));
                            p_date.add(tempdate.get(i));
                            p_thumb.add(tempthumb.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                if(!isSuccess) {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void deletePost(final int i)
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

                doDelete(p_id.get(i));
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
                String[] field = new String[]{"token","param","gpid","pid"};
                String[] value = new String[]{token,param,gpid,i};
                ActionGroup group = new ActionGroup(activity);
                group.setPostValue(field, value);
                group.delPost();
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
                    for(int x = 0;x < p_id.size();x++)
                    {
                        if(p_id.get(x).equals(i))
                        {
                            p_id.remove(x);
                            p_title.remove(x);
                            p_desc.remove(x);
                            p_date.remove(x);
                            p_thumb.remove(x);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public class PostAdapter extends BaseAdapter {

        private LayoutInflater inflater;


        public PostAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText texttitle, textdesc, texttime;
            CircularImageView image_thumb;
            ImageButton btn_delete;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_group_list_post, null);
            }

            String pathImage = p_thumb.get(position);

            final ViewHolder holder = new ViewHolder();
            holder.texttitle = (ViewText) convertView.findViewById(R.id.group_list_post_item_title);
            holder.textdesc = (ViewText) convertView.findViewById(R.id.group_list_post_item_desc);
            holder.texttime = (ViewText) convertView.findViewById(R.id.group_list_post_item_date);
            holder.image_thumb = (CircularImageView) convertView.findViewById(R.id.group_list_post_item_thumb);
            holder.btn_delete = (ImageButton) convertView.findViewById(R.id.group_detail_post_delete);


            holder.texttitle.setText(p_title.get(position));
            holder.texttitle.setSemiBold();
            holder.textdesc.setText(p_desc.get(position));
            holder.textdesc.setRegular();
            holder.texttime.setText(p_date.get(position));
            if(isEdit)
            {
                holder.btn_delete.setVisibility(View.VISIBLE);
                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePost(position);
                    }
                });
            }

            loader.displayImage(HelperGlobal.BASE_UPLOAD + pathImage, holder.image_thumb, opt);

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            p_id.clear();
            p_title.clear();
            p_desc.clear();
            p_date.clear();
            p_thumb.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return p_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return p_id.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
