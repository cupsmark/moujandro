package com.android.mouj;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.mouj.activity.ActivityCreatePost;
import com.android.mouj.activity.ActivityNotification;
import com.android.mouj.activity.ActivityPostDetail;
import com.android.mouj.activity.ActivityProfile;
import com.android.mouj.activity.ActivityReportPost;
import com.android.mouj.activity.ActivityScheduleTimeline;
import com.android.mouj.activity.ActivitySearch;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.FailReason;
import com.android.mouj.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.android.mouj.fragment.FragmentGroupList;
import com.android.mouj.fragment.FragmentYoutube;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionGroup;
import com.android.mouj.models.ActionPost;
import com.android.mouj.models.ActionSearch;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewDialogList;
import com.android.mouj.view.ViewDialogMessage;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;
import com.android.mouj.view.ViewYoutubeDialog;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity{

    ImageButton button_menu, button_search,button_create, button_notification;
    ListView main_listview;
    ArrayList<String> pid,ptitle,pusers,ptype,pdesc,pfile,ptime,puserid,pdonlot,prepcap,pcontenttype;
    ArrayList<Boolean> prep, pfav, showrep, pfollowed;
    int l,o;
    ViewLoadingDialog dialog;
    MainAdapter adapter;
    MediaPlayer media;
    Handler handler_mp3;
    Tracker tracker;
    FragmentGroupList groupList;
    ViewText pageTitle;
    ImageView logo;
    RelativeLayout form_filter;
    String paramGetAll = "a", message;

    String category_timeline = "a"; //a untuk public, f untuk followed
    String category_users = "a"; //a untuk masjid dan ustadz, u untuk ustadz, m untuk masjid
    String category_posting = "a"; //a untuk semua, p untuk artikel, s untuk jadwal
    ImageButton imagebutton_public, imagebutton_following, imagebutton_all_users, imagebutton_masjid, imagebutton_ustadz,
            imagebutton_all_posting, imagebutton_article, imagebutton_schedule, imagebutton_arrow;
    ArrayList<String> tempid, temptitle, tempdesc,temptime,tempusers,temptype,tempfile,tempusersid, tempdonlot, temprepcap,tempcontenttype;
    ArrayList<Boolean> temprep, tempfav, tempshowrep, tempfollowed;
    boolean searchRunning = false, success = false, showSecondMenu = true;
    Thread searchThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Home");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        l = 2;
        o = 0;
        pid = new ArrayList<String>();
        ptitle = new ArrayList<String>();
        pusers = new ArrayList<String>();
        ptype = new ArrayList<String>();
        pdesc = new ArrayList<String>();
        pfile = new ArrayList<String>();
        ptime = new ArrayList<String>();
        puserid = new ArrayList<String>();
        pdonlot = new ArrayList<String>();
        prep = new ArrayList<Boolean>();
        pfav = new ArrayList<Boolean>();
        prepcap = new ArrayList<String>();
        showrep = new ArrayList<Boolean>();
        pfollowed = new ArrayList<Boolean>();
        pcontenttype = new ArrayList<String>();
        handler_mp3 = new Handler();
        media = new MediaPlayer();
        adapter = new MainAdapter(true);
        category_timeline = HelperGlobal.getFilterPreference(MainActivity.this).get(0);
        category_users = HelperGlobal.getFilterPreference(MainActivity.this).get(1);
        category_posting = HelperGlobal.getFilterPreference(MainActivity.this).get(2);
        main_listview = (ListView) findViewById(R.id.main_listview_post);
        button_menu = (ImageButton) findViewById(R.id.main_imagebutton_menu);
        button_search = (ImageButton) findViewById(R.id.main_imagebutton_search);
        button_create = (ImageButton) findViewById(R.id.main_imagebutton_create);
        button_notification = (ImageButton) findViewById(R.id.main_imagebutton_notification);
        pageTitle = (ViewText) findViewById(R.id.main_textview_pagetitle);
        logo = (ImageView) findViewById(R.id.main_imageview_logo);
        form_filter = (RelativeLayout) findViewById(R.id.main_relative_filter);

        imagebutton_public = (ImageButton) findViewById(R.id.main_imagebutton_public);
        imagebutton_following = (ImageButton) findViewById(R.id.main_imagebutton_following);
        imagebutton_all_users = (ImageButton) findViewById(R.id.main_imagebutton_all_users);
        imagebutton_masjid = (ImageButton) findViewById(R.id.main_imagebutton_masjid);
        imagebutton_ustadz = (ImageButton) findViewById(R.id.main_imagebutton_ustadz);
        imagebutton_all_posting = (ImageButton) findViewById(R.id.main_imagebutton_all_posting);
        imagebutton_article = (ImageButton) findViewById(R.id.main_imagebutton_article);
        imagebutton_schedule = (ImageButton) findViewById(R.id.main_imagebutton_timer);
        imagebutton_arrow = (ImageButton) findViewById(R.id.main_imagebutton_nav_secondmenu);

        if(getUgid().equals("6") || getUgid().equals("5"))
        {
            button_create.setVisibility(View.VISIBLE);
            button_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ActivityCreatePost.class);
                    i.putExtra("mode-action","add");
                    i.putExtra("src","4");
                    startActivity(i);
                    finish();
                }
            });
        }

        initFilterTab();
        button_search.setVisibility(View.VISIBLE);
        pageTitle.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        imagebutton_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSecondMenu();
            }
        });
        button_menu.setImageResource(R.drawable.v1_icon_sidemenu_toggle);
        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu(true);
            }
        });
        checkSecondMenu();
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ActivitySearch.class);
                startActivity(i);
                finish();
            }
        });
        button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ActivityNotification.class);
                startActivity(i);
                finish();
            }
        });
        if(adapter != null)
        {
            main_listview.setAdapter(adapter);
        }
        imagebutton_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_timeline = "a";
                initFilterTab();
            }
        });
        imagebutton_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_timeline = "f";
                initFilterTab();
            }
        });

        imagebutton_all_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_users = "a";
                initFilterTab();
            }
        });
        imagebutton_masjid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_users = "m";
                initFilterTab();
            }
        });
        imagebutton_ustadz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_users = "u";
                initFilterTab();
            }
        });
        imagebutton_all_posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_posting = "a";
                initFilterTab();
            }
        });
        imagebutton_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_posting = "p";
                initFilterTab();
            }
        });
        imagebutton_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_posting = "s";
                initFilterTab();
            }
        });


    }

    private void initFilterTab()
    {
        if(category_timeline.equals("a"))
        {
            imagebutton_public.setImageResource(R.drawable.icon_globe_hover);
            imagebutton_following.setImageResource(R.drawable.icon_following_normal);
        }
        if(category_timeline.equals("f"))
        {
            imagebutton_public.setImageResource(R.drawable.icon_globe_normal);
            imagebutton_following.setImageResource(R.drawable.icon_following_hover);
        }
        if(category_users.equals("a"))
        {
            imagebutton_all_users.setImageResource(R.drawable.icon_timeline_home_hover);
            imagebutton_masjid.setImageResource(R.drawable.icon_mosque_home_normal);
            imagebutton_ustadz.setImageResource(R.drawable.icon_ustadz_normal);
        }
        if(category_users.equals("m"))
        {
            imagebutton_all_users.setImageResource(R.drawable.icon_timeline_home_normal);
            imagebutton_masjid.setImageResource(R.drawable.icon_mosque_home_hover);
            imagebutton_ustadz.setImageResource(R.drawable.icon_ustadz_normal);
        }
        if(category_users.equals("u"))
        {
            imagebutton_all_users.setImageResource(R.drawable.icon_timeline_home_normal);
            imagebutton_masjid.setImageResource(R.drawable.icon_mosque_home_normal);
            imagebutton_ustadz.setImageResource(R.drawable.icon_ustadz_hover);
        }
        if(category_posting.equals("a"))
        {
            imagebutton_all_posting.setImageResource(R.drawable.icon_posting_home_hover);
            imagebutton_article.setImageResource(R.drawable.icon_article_home_normal);
            imagebutton_schedule.setImageResource(R.drawable.icon_schedule_home_normal);
        }
        if(category_posting.equals("p"))
        {
            imagebutton_all_posting.setImageResource(R.drawable.icon_posting_home_normal);
            imagebutton_article.setImageResource(R.drawable.icon_article_home_hover);
            imagebutton_schedule.setImageResource(R.drawable.icon_schedule_home_normal);
        }
        if(category_posting.equals("s"))
        {
            imagebutton_all_posting.setImageResource(R.drawable.icon_posting_home_normal);
            imagebutton_article.setImageResource(R.drawable.icon_article_home_normal);
            imagebutton_schedule.setImageResource(R.drawable.icon_schedule_home_hover);
        }

        HelperGlobal.setFilterPreference(MainActivity.this, category_timeline, category_users, category_posting);
        fetch();
    }

    private void checkSecondMenu()
    {
        if(showSecondMenu)
        {
            showSecondMenu = false;
            imagebutton_arrow.setImageResource(R.drawable.icon_arrow_up_green);
            form_filter.setVisibility(View.GONE);
        }
        else
        {
            showSecondMenu = true;
            imagebutton_arrow.setImageResource(R.drawable.icon_arrow_down_green);
            form_filter.setVisibility(View.VISIBLE);
        }
    }

    private void runThread()
    {
        searchThread = null;
        searchThread = new Thread(runnableHome);
        searchThread.start();
    }

    private Runnable runnableHome = new Runnable() {
        @Override
        public void run() {

            if(searchRunning)
            {
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                temptime = new ArrayList<String>();
                tempusers = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempfile = new ArrayList<String>();
                tempusersid = new ArrayList<String>();
                tempdonlot = new ArrayList<String>();
                temprep = new ArrayList<Boolean>();
                tempfav = new ArrayList<Boolean>();
                temprepcap = new ArrayList<String>();
                tempshowrep = new ArrayList<Boolean>();
                tempfollowed = new ArrayList<Boolean>();
                tempcontenttype = new ArrayList<String>();
                ActionPost post = new ActionPost(MainActivity.this);
                post.setParam(getParam(), getToken());
                post.setLimit(l);
                post.setOffset(o);
                post.setMethod("home");
                post.setTag(getTag());
                post.setModeAllData(all_data);
                post.setFilterParameter(paramGetAll);
                post.setCategoryPosting(category_posting);
                post.setCategoryTimeline(category_timeline);
                post.setCategoryUsers(category_users);
                post.executeList();
                if(post.getSuccess())
                {
                    success = true;
                    tempid.addAll(post.getPID());
                    temptitle.addAll(post.getPTitle());
                    tempdesc.addAll(post.getPDesc());
                    temptime.addAll(post.getPDate());
                    tempusers.addAll(post.getPUsers());
                    temptype.addAll(post.getPType());
                    tempfile.addAll(post.getPFiles());
                    tempusersid.addAll(post.getPUserid());
                    tempdonlot.addAll(post.getPDon());
                    tempfav.addAll(post.getPFav());
                    temprep.addAll(post.getPRep());
                    temprepcap.addAll(post.getPRepCaption());
                    tempshowrep.addAll(post.getPShowRep());
                    tempfollowed.addAll(post.getPFollowed());
                    tempcontenttype.addAll(post.getPContentType());
                    o = post.getOffset();
                }
                else
                {
                    message = post.getMessage();
                }
                searchHandler.sendEmptyMessage(0);
            }

        }
    };

    private Handler searchHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(success)
            {
                if(tempid.size() > 0)
                {
                    for(int i = 0;i <tempid.size();i++)
                    {
                        pid.add(tempid.get(i));
                        ptitle.add(temptitle.get(i));
                        pdesc.add(tempdesc.get(i));
                        ptime.add(temptime.get(i));
                        pusers.add(tempusers.get(i));
                        ptype.add(temptype.get(i));
                        pfile.add(tempfile.get(i));
                        puserid.add(tempusersid.get(i));
                        pdonlot.add(tempdonlot.get(i));
                        prep.add(temprep.get(i));
                        pfav.add(tempfav.get(i));
                        prepcap.add(temprepcap.get(i));
                        showrep.add(tempshowrep.get(i));
                        pfollowed.add(tempfollowed.get(i));
                        pcontenttype.add(tempcontenttype.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    success = false;
                    searchRunning = false;
                    main_listview.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            loadMore();
                        }
                    });
                }

            }
            else {
                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void fetch()
    {
        adapter.clear();
        adapter.notifyDataSetChanged();
        o = 0;
        searchRunning = true;
        runThread();
    }

    private void loadMore()
    {
        searchRunning = true;
        runThread();
    }

    private void loadDetail(int position)
    {
        Intent i = new Intent(MainActivity.this, ActivityPostDetail.class);
        i.putExtra("pid",pid.get(position));
        i.putExtra("src","2");
        i.putExtra("subSrc","home");
        startActivity(i);
        finish();
    }

    private void showItemOption(final int pos, final String title, final String u, final String don, final String desc, boolean rep)
    {
        ArrayList<String> data_id = new ArrayList<String>();
        data_id.add("1");
        data_id.add("2");
        data_id.add("3");
        ArrayList<String> data_title = new ArrayList<String>();
        if(!rep)
        {
            data_title.add(getResources().getString(R.string.home_dialog_option_share_timeline));
        }
        if(rep)
        {
            data_title.add(getResources().getString(R.string.home_dialog_option_delete_favorit));
        }

        data_title.add(getResources().getString(R.string.home_dialog_option_share_socmed));
        data_title.add(getResources().getString(R.string.home_dialog_option_share_report));
        if(getUgid().equals("5") || getUgid().equals("6"))
        {
            int grCount = Integer.parseInt(getHasGR());
            if(grCount > 0)
            {
                data_id.add("4");
                data_title.add(getResources().getString(R.string.home_dialog_option_post_group_title));
            }
        }
        final ViewDialogList dialog = new ViewDialogList(MainActivity.this);
        dialog.setHiddenTitle(true);
        dialog.setHiddenButtonCancel(true);
        dialog.setHiddenButtonOK(true);
        dialog.setHiddenDesc(true);
        dialog.setHiddenThumb(true);
        dialog.setDataID(data_id);
        dialog.setDataTitle(data_title);
        dialog.setDataThumb(new ArrayList<String>());
        dialog.setDataDesc(new ArrayList<String>());
        dialog.setCancelable(true);
        dialog.show();
        dialog.executeList();
        dialog.getListViewData().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(dialog.getListViewData().getAdapter().getItem(position) == null)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                    if(object[0].equals("3"))
                    {
                        Intent i = new Intent(MainActivity.this, ActivityReportPost.class);
                        i.putExtra("pi",pid.get(pos));
                        i.putExtra("pt",title);
                        i.putExtra("u",u);
                        startActivity(i);
                        finish();
                    }
                    else if(object[0].equals("2"))
                    {
                        share(desc,don);
                    }
                    else if(object[0].equals("4"))
                    {
                        //share(desc,don);
                        addFragmentGroupList(pid.get(pos));
                    }
                    else {

                        ImageButton btnFlag = new ImageButton(MainActivity.this);
                        repost(pos, btnFlag, "1");
                    }
                    dialog.dismiss();
                }
            }
        });
    }


    private void back()
    {
        if(getTag().equals(""))
        {
            if(groupList != null && groupList.isAdded())
            {
                removeFragmentGroupList();
            }
            else {
                if(getAllData())
                {
                    setAllData(false);
                    Intent s = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(s);
                    finish();
                }
                else {
                    HelperGlobal.ExitApp(MainActivity.this,getResources().getString(R.string.base_string_exit_title));
                }
            }
        }
        else {
            Intent s = new Intent(MainActivity.this, ActivitySearch.class);
            startActivity(s);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addFragmentGroupList(String ids)
    {
        groupList = new FragmentGroupList();
        groupList.setParam(getParam(), getToken());
        groupList.setUGroup(getUgid());
        groupList.setPostID(ids);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                .replace(R.id.main_relative_fragment, groupList, FragmentGroupList.TAG_GROUP_LIST)
                .addToBackStack(null)
                .commit();
    }

    private void removeFragmentGroupList()
    {
        if(groupList != null)
        {
            getSupportFragmentManager().beginTransaction().remove(groupList).commit();
            groupList = null;
        }
    }

    private void repost(final int position, final ImageButton btn_favorit, final String action)
    {
        new AsyncTask<Void, Integer, String>()
        {
            boolean success = false;
            String msg;
            ViewLoadingDialog dialog;
            String modeRepost = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(MainActivity.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param", "pid", "act"};
                String[] value = new String[]{token,param, pid.get(position), action};
                ActionPost post = new ActionPost(MainActivity.this);
                post.setArrayPOST(field, value);
                post.executeRepost();
                if(post.getSuccess())
                {
                    success = true;
                    modeRepost = post.getModeRepost();
                }
                msg = post.getMessage();
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
                    if(modeRepost.equals("add"))
                    {
                        prep.set(position, true);
                        pfav.set(position, true);
                        //btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        prep.set(position, false);
                        pfav.set(position, false);
                        //btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
                        adapter.notifyDataSetChanged();
                    }
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public class MainAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private int mLastPosition = 0;
        private boolean isScroll;
        YouTubeThumbnailLoader loaders;

        public MainAdapter(boolean isScroll) {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.isScroll = isScroll;
        }

        class ViewHolder{
            ViewText textusers, texttitle, texttime, textdesc, textrepost;
            ImageButton btn_menu, btn_favorit;
            ImageView image_headline, icontype;
            ViewButton btn_more;
            LinearLayout containerItem, timelineFollowed;
            RelativeLayout containerRepost;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            final ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.main_listview_item, null);
            }

            String pathImage = "";
            holder.textusers = (ViewText) convertView.findViewById(R.id.main_lv_item_textview_users_fullname);
            holder.texttitle = (ViewText) convertView.findViewById(R.id.main_lv_item_textview_title);
            holder.textdesc = (ViewText) convertView.findViewById(R.id.main_lv_item_textview_desc);
            holder.texttime = (ViewText) convertView.findViewById(R.id.main_lv_item_textview_time_uploaded);
            holder.btn_menu = (ImageButton) convertView.findViewById(R.id.main_lv_item_imagebutton_menu_dialog);
            holder.btn_more = (ViewButton) convertView.findViewById(R.id.main_lv_item_textview_more);
            holder.image_headline = (ImageView) convertView.findViewById(R.id.main_lv_item_imageview_headline);
            holder.containerItem = (LinearLayout) convertView.findViewById(R.id.main_linear_container_item);
            holder.btn_favorit = (ImageButton) convertView.findViewById(R.id.main_lv_item_imagebutton_menu_favorit);
            holder.containerRepost = (RelativeLayout) convertView.findViewById(R.id.listview_item_main_container_repost);
            holder.textrepost = (ViewText) convertView.findViewById(R.id.listview_item_main_textview_repost);
            holder.timelineFollowed = (LinearLayout) convertView.findViewById(R.id.timeline_linear_followed);
            holder.icontype = (ImageView) convertView.findViewById(R.id.main_lv_item_imageview_icontype);
            //holder.headerContainer = (RelativeLayout) convertView.findViewById(R.id.main_listview_item_header);

            holder.containerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pid.get(position).equals("0"))
                    {
                        loadDetail(position);
                    }
                }
            });

            holder.textusers.setRegular();
            holder.textusers.setText(pusers.get(position));
            holder.texttitle.setText(ptitle.get(position));
            holder.texttitle.setRegular();
            holder.textdesc.setText(pdesc.get(position));
            holder.texttime.setText(ptime.get(position));


            holder.btn_favorit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repost(position, holder.btn_favorit, "1");
                }
            });
            if(!pfav.get(position))
            {
                if(pid.get(position).equals("0"))
                {
                    holder.btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
                    holder.btn_favorit.setEnabled(false);
                }
                else
                {
                    holder.btn_favorit.setImageResource(R.drawable.icon_favorite_normal);
                }
            }
            if(pfav.get(position))
            {
                holder.btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
            }

            if(!showrep.get(position))
            {
                holder.containerRepost.setVisibility(View.GONE);
                holder.textrepost.setText("");
            }
            if(showrep.get(position))
            {
                holder.containerRepost.setVisibility(View.VISIBLE);
                holder.textrepost.setText(prepcap.get(position));
            }
            if(pcontenttype.get(position).equals("article"))
            {
                holder.icontype.setImageResource(R.drawable.v1_icon_home_quote);
            }
            if(pcontenttype.get(position).equals("schedule"))
            {
                holder.icontype.setImageResource(R.drawable.icon_timeline_type_schedule);
            }

            holder.btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pid.get(position).equals("0"))
                    {
                        holder.btn_menu.setEnabled(false);
                    }
                    else
                    {
                        showItemOption(position, ptitle.get(position), puserid.get(position), pdonlot.get(position), pdesc.get(position), prep.get(position));
                    }
                }
            });
            holder.btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pid.get(position).equals("0"))
                    {
                        loadDetail(position);
                    }
                }
            });
            holder.textusers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pid.get(position).equals("0"))
                    {
                        Intent intent = new Intent(MainActivity.this, ActivityProfile.class);
                        intent.putExtra("target", puserid.get(position));
                        intent.putExtra("mode", "view");
                        intent.putExtra("src", "2");
                        startActivity(intent);
                        finish();
                    }
                }
            });

            if(ptype.get(position).equals("img"))
            {

                pathImage = HelperGlobal.BASE_UPLOAD + pfile.get(position);
                loader.displayImage(pathImage, holder.image_headline, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage.getHeight() > loadedImage.getWidth()) {
                            holder.image_headline.getLayoutParams().height = loadedImage.getHeight() / 2;
                            holder.image_headline.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
            else if(ptype.get(position).equals("youtube"))
            {
                pathImage = "https://img.youtube.com/vi/" + pfile.get(position)+"/0.jpg";
                holder.image_headline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item_youtube_player(pfile.get(position));
                    }
                });
                loader.displayImage(pathImage, holder.image_headline, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage.getHeight() > loadedImage.getWidth()) {
                            holder.image_headline.getLayoutParams().height = loadedImage.getHeight() / 2;
                            holder.image_headline.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
            else {
                holder.image_headline.setImageResource(R.drawable.bg_timeline_transparent);
            }





            convertView.setTag(holder);
            if(isScroll) {
                /*float initialTranslation = (mLastPosition <= position ? 500f : -500f);

                convertView.setTranslationY(initialTranslation);
                convertView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);

                // Keep track of the last position we loaded
                mLastPosition = position;*/
            }

            return convertView;
        }

        public void clear()
        {
            pid.clear();
            ptitle.clear();
            pdesc.clear();
            ptime.clear();
            pusers.clear();
            ptype.clear();
            pfile.clear();
            puserid.clear();
            pdonlot.clear();
            prep.clear();
            pfav.clear();
            prepcap.clear();
            showrep.clear();
            pcontenttype.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pid.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return pid.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    private void share(String description, String downloadLink)
    {
        String desc = description + "<br/><br/>Attachment : " + downloadLink;
        Intent specific_intent = new Intent();
        specific_intent.setAction(Intent.ACTION_SEND);
        specific_intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(desc).toString());
        specific_intent.putExtra(Intent.EXTRA_TITLE, ptitle);
        specific_intent.putExtra(Intent.EXTRA_SUBJECT, ptitle);
        specific_intent.setType("text/plain");
        specific_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(specific_intent);
    }

    private void item_youtube_player(final String yid)
    {
        ViewYoutubeDialog dialog = new ViewYoutubeDialog();
        dialog.setYoutubeID(yid);
        dialog.setTitle("Youtube Player");
        dialog.setFullscreen(false);
        dialog.show(getSupportFragmentManager(), "youtube_player");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
