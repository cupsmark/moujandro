package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.mouj.BaseActivity;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.fragment.FragmentGroupList;
import com.android.mouj.fragment.FragmentSearchGroup;
import com.android.mouj.fragment.FragmentSearchHashtag;
import com.android.mouj.fragment.FragmentSearchMasjid;
import com.android.mouj.fragment.FragmentSearchPost;
import com.android.mouj.fragment.FragmentSearchUstadz;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.models.ActionSearch;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewText;

import java.util.ArrayList;

public class ActivitySearch extends BaseActivity{

    ImageButton btn_back, btn_cleartyping;
    ViewEditText edittext_keyword;
    String tab_selected, selected_value = "";
    ViewButton btn_ustadz, btn_masjid, btn_hashtag, btn_post, btn_group;
    DisplayImageOptions opt;
    ImageLoader loader;


    FragmentSearchUstadz ustadzFragment;
    FragmentSearchMasjid fragmentMasjid;
    FragmentSearchPost fragmentPost;
    FragmentSearchHashtag fragmentHashtag;
    FragmentSearchGroup fragmentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setShowingMenu(false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ActivitySearch.this)
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
        initArrayList();
        init();
        addUstadz();
    }


    private void initArrayList()
    {
        selected_value = "";
    }

    private void init()
    {

        tab_selected = "1";
        btn_back = (ImageButton) findViewById(R.id.search_imagebutton_back);
        btn_cleartyping = (ImageButton) findViewById(R.id.search_imagebutton_cleartyping);
        edittext_keyword = (ViewEditText) findViewById(R.id.search_edittext_value);
        btn_ustadz = (ViewButton) findViewById(R.id.search_tab_ustadz);
        btn_masjid = (ViewButton) findViewById(R.id.search_tab_masjid);
        btn_hashtag = (ViewButton) findViewById(R.id.search_tab_hashtag);
        btn_post = (ViewButton) findViewById(R.id.search_tab_post);
        btn_group = (ViewButton) findViewById(R.id.search_tab_group);
        btn_ustadz.setPressed(true);

        btn_ustadz.setRegular();
        btn_masjid.setRegular();
        btn_hashtag.setRegular();
        btn_post.setRegular();
        btn_group.setRegular();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        btn_cleartyping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_keyword.setText("");
                selected_value = "";
                switchKeyword();
            }
        });
        btn_ustadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ustadz.setBackgroundResource(R.drawable.search_tab_hover);
                btn_masjid.setBackgroundResource(R.drawable.search_tab_normal);
                btn_hashtag.setBackgroundResource(R.drawable.search_tab_normal);
                btn_post.setBackgroundResource(R.drawable.search_tab_normal);
                btn_group.setBackgroundResource(R.drawable.search_tab_normal);

                btn_ustadz.setTextColor(getResources().getColor(R.color.base_green));
                btn_masjid.setTextColor(getResources().getColor(R.color.base_black));
                btn_hashtag.setTextColor(getResources().getColor(R.color.base_black));
                btn_post.setTextColor(getResources().getColor(R.color.base_black));
                btn_group.setTextColor(getResources().getColor(R.color.base_black));

                tab_selected = "1";
                switchTab();
            }
        });
        btn_masjid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ustadz.setBackgroundResource(R.drawable.search_tab_normal);
                btn_masjid.setBackgroundResource(R.drawable.search_tab_hover);
                btn_hashtag.setBackgroundResource(R.drawable.search_tab_normal);
                btn_post.setBackgroundResource(R.drawable.search_tab_normal);
                btn_group.setBackgroundResource(R.drawable.search_tab_normal);

                btn_ustadz.setTextColor(getResources().getColor(R.color.base_black));
                btn_masjid.setTextColor(getResources().getColor(R.color.base_green));
                btn_hashtag.setTextColor(getResources().getColor(R.color.base_black));
                btn_post.setTextColor(getResources().getColor(R.color.base_black));
                btn_group.setTextColor(getResources().getColor(R.color.base_black));

                tab_selected = "2";
                switchTab();
            }
        });
        btn_hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ustadz.setBackgroundResource(R.drawable.search_tab_normal);
                btn_masjid.setBackgroundResource(R.drawable.search_tab_normal);
                btn_hashtag.setBackgroundResource(R.drawable.search_tab_hover);
                btn_post.setBackgroundResource(R.drawable.search_tab_normal);
                btn_group.setBackgroundResource(R.drawable.search_tab_normal);

                btn_ustadz.setTextColor(getResources().getColor(R.color.base_black));
                btn_masjid.setTextColor(getResources().getColor(R.color.base_black));
                btn_hashtag.setTextColor(getResources().getColor(R.color.base_green));
                btn_post.setTextColor(getResources().getColor(R.color.base_black));
                btn_group.setTextColor(getResources().getColor(R.color.base_black));

                tab_selected = "3";
                switchTab();
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ustadz.setBackgroundResource(R.drawable.search_tab_normal);
                btn_masjid.setBackgroundResource(R.drawable.search_tab_normal);
                btn_hashtag.setBackgroundResource(R.drawable.search_tab_normal);
                btn_post.setBackgroundResource(R.drawable.search_tab_hover);
                btn_group.setBackgroundResource(R.drawable.search_tab_normal);

                btn_ustadz.setTextColor(getResources().getColor(R.color.base_black));
                btn_masjid.setTextColor(getResources().getColor(R.color.base_black));
                btn_hashtag.setTextColor(getResources().getColor(R.color.base_black));
                btn_post.setTextColor(getResources().getColor(R.color.base_green));
                btn_group.setTextColor(getResources().getColor(R.color.base_black));

                tab_selected = "4";
                switchTab();
            }
        });
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ustadz.setBackgroundResource(R.drawable.search_tab_normal);
                btn_masjid.setBackgroundResource(R.drawable.search_tab_normal);
                btn_hashtag.setBackgroundResource(R.drawable.search_tab_normal);
                btn_post.setBackgroundResource(R.drawable.search_tab_normal);
                btn_group.setBackgroundResource(R.drawable.search_tab_hover);

                btn_ustadz.setTextColor(getResources().getColor(R.color.base_black));
                btn_masjid.setTextColor(getResources().getColor(R.color.base_black));
                btn_hashtag.setTextColor(getResources().getColor(R.color.base_black));
                btn_post.setTextColor(getResources().getColor(R.color.base_black));
                btn_group.setTextColor(getResources().getColor(R.color.base_green));

                tab_selected = "5";
                switchTab();
            }
        });

        edittext_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 2)
                {
                    selected_value = edittext_keyword.getText().toString();
                }
                else
                {
                    selected_value = "";
                }
                switchKeyword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void switchTab()
    {
        if(tab_selected.equals("1"))
        {
            addUstadz();
        }
        else if(tab_selected.equals("2"))
        {
            addMasjid();
        }
        else if(tab_selected.equals("3"))
        {
            addHashtag();
        }
        else if(tab_selected.equals("4"))
        {
            addPost();
        }
        else{
            addGroup();
        }
    }

    private void switchKeyword()
    {
        if(tab_selected.equals("1"))
        {
            ustadzFragment.setKeyword(selected_value, false);
        }
        else if(tab_selected.equals("2"))
        {
            fragmentMasjid.setKeyword(selected_value, false);
        }
        else if(tab_selected.equals("3"))
        {
            fragmentHashtag.setKeyword(selected_value, false);
        }
        else if(tab_selected.equals("4"))
        {
            fragmentPost.setKeyword(selected_value, false);
        }
        else {
            fragmentGroup.setKeyword(selected_value, false);
        }
    }

    private void addUstadz()
    {
        ustadzFragment = new FragmentSearchUstadz();
        ustadzFragment.setParam(getParam(), getToken());
        ustadzFragment.setKeyword(selected_value, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.search_relative_container, ustadzFragment, FragmentSearchUstadz.TAG_SEARCH_TAB_USTADZ)
                .addToBackStack(null)
                .commit();
    }

    private void addMasjid()
    {
        fragmentMasjid = new FragmentSearchMasjid();
        fragmentMasjid.setParam(getParam(), getToken());
        fragmentMasjid.setKeyword(selected_value, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.search_relative_container, fragmentMasjid, FragmentSearchMasjid.TAG_SEARCH_TAB_MASJID)
                .addToBackStack(null)
                .commit();
    }

    private void addPost()
    {
        fragmentPost = new FragmentSearchPost();
        fragmentPost.setParam(getParam(), getToken());
        fragmentPost.setKeyword(selected_value, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.search_relative_container, fragmentPost, FragmentSearchPost.TAG_SEARCH_TAB_POST)
                .addToBackStack(null)
                .commit();
    }

    private void addHashtag()
    {
        fragmentHashtag = new FragmentSearchHashtag();
        fragmentHashtag.setParam(getParam(), getToken());
        fragmentHashtag.setKeyword(selected_value, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.search_relative_container, fragmentHashtag, FragmentSearchHashtag.TAG_SEARCH_TAB_HASHTAG)
                .addToBackStack(null)
                .commit();
    }

    private void addGroup()
    {
        fragmentGroup = new FragmentSearchGroup();
        fragmentGroup.setParam(getParam(), getToken());
        fragmentGroup.setKeyword(selected_value, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.search_relative_container, fragmentGroup, FragmentSearchGroup.TAG_SEARCH_TAB_GROUP)
                .addToBackStack(null)
                .commit();
    }


    private void back()
    {
        setTag("");
        Intent i = new Intent(ActivitySearch.this, MainActivity.class);
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
