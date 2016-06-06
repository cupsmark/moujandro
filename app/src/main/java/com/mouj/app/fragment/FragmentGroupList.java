package com.mouj.app.fragment;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.activity.ActivityGroupForm;
import com.mouj.app.activity.ActivityGroupListPost;
import com.mouj.app.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionGroup;
import com.mouj.app.view.GridViewDynamic;
import com.mouj.app.view.ViewDialogMessage;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 2/27/16.
 */
public class FragmentGroupList extends BaseFragment {

    public static final String TAG_GROUP_LIST = "tag:fragment-group-list";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    ArrayList<String> gr_id, gr_name, gr_thumb;
    ArrayList<Boolean> gr_edit;
    String param, token, u;
    int l = 10, o = 0;
    DisplayImageOptions opt;
    ImageLoader loader;
    GroupAdapter adapter;
    GridViewDynamic mainGrid;
    Tracker tracker;
    String postID = "0";
    LinearLayout linearContainer;

    public FragmentGroupList()
    {

    }

    public void setParam(String param, String token) {
        this.param = param;
        this.token = token;
    }

    public void setUGroup(String u)
    {
        this.u = u;
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) activity.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page List Group");
    }

    public void setPostID(String ids)
    {
        this.postID = ids;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_group_list, container, false);
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

        gr_id = new ArrayList<String>();
        gr_name = new ArrayList<String>();
        gr_thumb = new ArrayList<String>();
        gr_edit = new ArrayList<Boolean>();

        mainGrid = (GridViewDynamic) activity.findViewById(R.id.group_list_gridview);
        linearContainer = (LinearLayout) activity.findViewById(R.id.fragment_group_list_linear);
        if(!postID.equals("0"))
        {
            linearContainer.getLayoutParams().height = 400;
        }
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>()
        {
            ArrayList<String> tempid,tempname,tempthumb;
            ArrayList<Boolean> tempedit;
            boolean isSuccess = false;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempedit = new ArrayList<Boolean>();
            }

            @Override
            protected String doInBackground(Void... params) {

                ActionGroup group = new ActionGroup(activity);
                group.setParam(param, token);
                group.setL(l);
                group.setO(o);
                group.executeListGroup();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(group.getGRID());
                    tempname.addAll(group.getGRName());
                    tempthumb.addAll(group.getGRThumb());
                    tempedit.addAll(group.getGREdit());
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
                        gr_id.addAll(tempid);
                        gr_name.addAll(tempname);
                        gr_thumb.addAll(tempthumb);
                        gr_edit.addAll(tempedit);
                        if(u.equals("5") || u.equals("6"))
                        {
                            if(gr_id.size() <= HelperGlobal.APP_MAX_GROUP && postID.equals("0"))
                            {
                                gr_id.add("0");
                                gr_name.add("Tambah Grup");
                                gr_thumb.add("");
                            }
                        }
                        adapter = new GroupAdapter();
                        notifyGridView();
                    }
                }
                else {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyGridView()
    {
        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gr_id.get(position).equals("0")) {
                    Intent i = new Intent(activity, ActivityGroupForm.class);
                    i.putExtra("mode", "add");
                    startActivity(i);
                    activity.finish();
                } else {
                    if (postID.equals("0")) {
                        Intent i = new Intent(activity, ActivityGroupListPost.class);
                        i.putExtra("gpid", gr_id.get(position));
                        i.putExtra("gptitle", gr_name.get(position));
                        startActivity(i);
                        activity.finish();
                    } else {
                        repostToGroup(postID, gr_id.get(position));
                    }
                }
            }
        });
        mainGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gr_id.get(position).equals("0")) {
                    deleteGroup(position);
                }
                return false;
            }
        });
        if(adapter != null)
        {
            mainGrid.setAdapter(adapter);
        }
    }

    private void deleteGroup(final int i)
    {
        String isLeave = "1";
        final ViewDialogMessage dialog = new ViewDialogMessage(activity);
        if(gr_edit.get(i))
        {
            dialog.setTitle(activity.getResources().getString(R.string.group_item_delete));
            isLeave = "0";
        }
        else
        {
            dialog.setTitle(activity.getResources().getString(R.string.group_item_leave));
            isLeave = "1";
        }

        dialog.setMessage(activity.getResources().getString(R.string.base_string_delete_message));
        dialog.setCancelable(true);
        dialog.show();

        final String finalIsLeave = isLeave;
        dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                doDelete(gr_id.get(i), finalIsLeave, param);
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void doDelete(final String i, final String isLeave, final String idm)
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg = "";
            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","gpid","isleave","idm"};
                String[] value = new String[]{token,param,i, isLeave, idm};
                ActionGroup group = new ActionGroup(activity);
                group.setPostValue(field, value);
                group.executeDelete();
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
                    for(int x = 0;x < gr_id.size();x++)
                    {
                        if(gr_id.get(x).equals(i))
                        {
                            gr_id.remove(x);
                            gr_name.remove(x);
                            gr_thumb.remove(x);
                            gr_edit.remove(x);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void repostToGroup(final String pid, final String gpid)
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
                String[] field = new String[]{"token","param", "pid", "gpid"};
                String[] value = new String[]{token,param, pid, gpid};
                ActionGroup group = new ActionGroup(activity);
                group.setPostValue(field, value);
                group.postToGroup();
                if(group.getSuccess())
                {
                    success = true;
                }
                msg = group.getMessage();
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
                    removeThisFragment();
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void removeThisFragment()
    {
        activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public class GroupAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GroupAdapter() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText txt_name;
            CircularImageView image_thumb;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            //ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_group_list, null);
            }


            final ViewHolder holder = new ViewHolder();
            holder.txt_name = (ViewText) convertView.findViewById(R.id.group_list_item_title);
            holder.image_thumb = (CircularImageView) convertView.findViewById(R.id.group_list_item_thumb);

            holder.txt_name.setSemiBold();
            holder.txt_name.setText(gr_name.get(position));

            if(gr_id.get(position).equals("0"))
            {
                holder.image_thumb.setImageResource(R.drawable.icon_group_add);
            }
            else {
                loader.displayImage(HelperGlobal.BASE_UPLOAD + gr_thumb.get(position), holder.image_thumb, opt);
            }


            convertView.setTag(holder);


            return convertView;
        }

        public void clear()
        {
            gr_id.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return gr_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return gr_id.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
