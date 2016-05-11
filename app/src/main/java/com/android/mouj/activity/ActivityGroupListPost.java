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
import com.android.mouj.fragment.FragmentGroupTabInfo;
import com.android.mouj.fragment.FragmentGroupTabMember;
import com.android.mouj.fragment.FragmentGroupTabNotif;
import com.android.mouj.fragment.FragmentGroupTabPost;
import com.android.mouj.fragment.FragmentGroupTabSearch;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionGroup;
import com.android.mouj.models.ActionPost;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewLoading;
import com.android.mouj.view.ViewText;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityGroupListPost extends BaseActivity {


    ImageButton imagebutton_back, imagebutton_create;
    ViewButton button_detail;


    DisplayImageOptions opt;
    ImageLoader loader;
    int wScreen, hScreen;
    String gpid, title, desc, cover, ava, time, owner;
    ViewText pagetitle;
    LinearLayout btn_post, btn_member, btn_search, btn_info, btn_notif;
    ImageView image_cover;
    CircularImageView image_avatar;
    boolean isEdit = false;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list_post);
        setShowingMenu(false);
        wScreen = HelperGlobal.GetDimension("w", ActivityGroupListPost.this);
        hScreen = HelperGlobal.GetDimension("h",ActivityGroupListPost.this);

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

    private void GoogleAnalyticsInit(String title)
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        fetch_detail();
    }

    private void init()
    {

        gpid = getIntent().getExtras().getString("gpid");
        title = getIntent().getExtras().getString("gptitle");

        pagetitle = (ViewText) findViewById(R.id.main_textview_pagetitle);

        imagebutton_back = (ImageButton) findViewById(R.id.group_imagebutton_back);
        button_detail = (ViewButton) findViewById(R.id.group_btn_detail);
        imagebutton_create = (ImageButton) findViewById(R.id.group_list_post_imagebutton_create);
        btn_post = (LinearLayout) findViewById(R.id.group_list_post_linear_tab_post);
        btn_member = (LinearLayout) findViewById(R.id.group_list_post_linear_tab_member);
        btn_search = (LinearLayout) findViewById(R.id.group_list_post_linear_tab_search);
        btn_info = (LinearLayout) findViewById(R.id.group_list_post_linear_tab_info);
        btn_notif = (LinearLayout) findViewById(R.id.group_list_post_linear_tab_notif);
        image_avatar = (CircularImageView) findViewById(R.id.group_list_post_image_ava);
        image_cover = (ImageView) findViewById(R.id.group_list_post_imageview_cover);



        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void switchTab()
    {
        if(isEdit)
        {
            btn_post.getLayoutParams().width = wScreen / 5;
            btn_member.getLayoutParams().width = wScreen / 5;
            btn_search.getLayoutParams().width = wScreen / 5;
            btn_info.getLayoutParams().width = wScreen / 5;
            btn_notif.getLayoutParams().width = wScreen / 5;
            btn_member.setVisibility(View.VISIBLE);
            btn_notif.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_post.getLayoutParams().width = wScreen / 3;
            btn_search.getLayoutParams().width = wScreen / 3;
            btn_info.getLayoutParams().width = wScreen / 3;
        }
        btn_post.setVisibility(View.VISIBLE);
        btn_search.setVisibility(View.VISIBLE);
        btn_info.setVisibility(View.VISIBLE);

        btn_post.setBackgroundResource(R.drawable.group_list_post_tab_hover);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.setBackgroundResource(R.drawable.group_list_post_tab_hover);
                btn_member.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_search.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_info.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_notif.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                addTabPost();
            }
        });
        btn_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_member.setBackgroundResource(R.drawable.group_list_post_tab_hover);
                btn_search.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_info.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_notif.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                addTabSearch();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_member.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_search.setBackgroundResource(R.drawable.group_list_post_tab_hover);
                btn_info.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_notif.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                addTabMember();
            }
        });
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_member.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_search.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_info.setBackgroundResource(R.drawable.group_list_post_tab_hover);
                btn_notif.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                addTabInfo();
            }
        });
        btn_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_member.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_search.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_info.setBackgroundResource(R.drawable.group_list_post_tab_normal);
                btn_notif.setBackgroundResource(R.drawable.group_list_post_tab_hover);
                addTabNotif();
            }
        });
    }

    private void fetch_detail()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;

            ViewLoading loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = (ViewLoading) findViewById(R.id.group_list_post_loading);
                loading.setImageResource(R.drawable.custom_loading);
                loading.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(ActivityGroupListPost.this);
                group.setParam(getParam(), getToken());
                group.setGPID(gpid);
                group.executeDetailGroup();
                if(group.getSuccess())
                {
                    isSuccess = true;

                    title = group.getGRName().get(0);
                    desc = group.getGRDesc().get(0);
                    ava = group.getGRThumb().get(0);
                    cover = group.getGRCover().get(0);
                    owner = group.getGROwner().get(0);
                    time = group.getGRTime().get(0);
                    isEdit = group.getEdited();
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
                    GoogleAnalyticsInit(title);
                    if(isEdit)
                    {
                        button_detail.setVisibility(View.VISIBLE);
                        button_detail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ActivityGroupListPost.this, ActivityGroupForm.class);
                                i.putExtra("mode", "edit");
                                i.putExtra("gpid", gpid);
                                startActivity(i);
                                finish();
                            }
                        });
                        imagebutton_create.setVisibility(View.VISIBLE);
                        imagebutton_create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ActivityGroupListPost.this, ActivityCreatePost.class);
                                i.putExtra("mode-action","post-group");
                                i.putExtra("gpid",gpid);
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                    pagetitle.setText(title);
                    loader.displayImage(HelperGlobal.BASE_UPLOAD + ava, image_avatar, opt);
                    loader.displayImage(HelperGlobal.BASE_UPLOAD + cover, image_cover, opt);
                    switchTab();
                    addTabPost();
                }
                if(!isSuccess){
                    Toast.makeText(ActivityGroupListPost.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void addTabPost()
    {
        FragmentGroupTabPost fragmentPost = new FragmentGroupTabPost();
        fragmentPost.setParam(getParam(), getToken());
        fragmentPost.setEditable(isEdit);
        fragmentPost.setGPID(gpid);
        fragmentPost.setTitle(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_list_post_relative_fragment, fragmentPost, FragmentGroupTabPost.TAG_GROUP_TAB_POST)
                .commit();
    }

    private void addTabInfo()
    {
        FragmentGroupTabInfo info = new FragmentGroupTabInfo();
        info.setDesc(desc);
        info.setCreator(owner);
        info.setCreated(time);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_list_post_relative_fragment, info, FragmentGroupTabInfo.TAG_GROUP_TAB_INFO)
                .commit();
    }

    private void addTabSearch()
    {
        FragmentGroupTabSearch search = new FragmentGroupTabSearch();
        search.setGPID(gpid);
        search.setGPTitle(title);
        search.setEdit(isEdit);
        search.setParam(getParam(), getToken());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_list_post_relative_fragment, search, FragmentGroupTabSearch.TAG_GROUP_TAB_SEARCH)
                .commit();
    }

    private void addTabMember()
    {
        FragmentGroupTabMember member = new FragmentGroupTabMember();
        member.setGPID(gpid);
        member.setGPTitle(title);
        member.setParam(getParam(), getToken());
        member.setEdit(isEdit);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_list_post_relative_fragment, member, FragmentGroupTabMember.TAG_GROUP_TAB_MEMBER)
                .commit();
    }

    private void addTabNotif()
    {
        FragmentGroupTabNotif notif = new FragmentGroupTabNotif();
        notif.setGPID(gpid);
        notif.setGPTitle(title);
        notif.setParam(getParam(), getToken());
        notif.setEdit(isEdit);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_list_post_relative_fragment, notif, FragmentGroupTabNotif.TAG_GROUP_TAB_NOTIF)
                .commit();
    }


    private void back()
    {
        Intent i = new Intent(ActivityGroupListPost.this, ActivityGroup.class);
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


}
