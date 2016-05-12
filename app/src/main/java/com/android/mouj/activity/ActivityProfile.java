package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.fragment.FragmentGroupList;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionPost;
import com.android.mouj.models.ActionProfile;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewDialogList;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewSupportMapFragment;
import com.android.mouj.view.ViewText;
import com.android.mouj.view.ViewYoutubeDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class ActivityProfile extends BaseActivity {

    Tracker tracker;
    boolean success = false, isThreadRunning = false, isTabPost = false, isTabSchedule = false, isTabProfile = true;
    String message,u, fu, e, ph, d, ava, i, ids, longs, lat, cover,loc, ugid,
            extra_target_id, extra_mode_view,followAccess, modeFollow, srcFrom, contentType = "article",gpid, gptitle, hashtag = "";
    Thread threadPost;
    ArrayList<String> pid, ptitle, pdesc, pdate, ptype, pfiles, pusers,puserid,pdon,prepcap, prof_caption, prof_value,
        assignValues;
    ArrayList<Boolean> prep, pfav, showrep;
    ViewButton button_follow, button_profile, button_posted, button_schedule;
    LayoutInflater inflater;
    RelativeLayout button_edit, relativeMaps, relativeSearch;
    ImageButton imagebutton_back;
    CircularImageView imageview_avatar;
    ImageView imageview_cover;
    ListView listview_post;
    GoogleMap maps;
    FragmentGroupList groupList;
    View viewHeader, viewFooter;
    LinearLayout linearVerified, container;
    int wScreen = 0, hScreen = 0, l = 5, o = 0;
    ViewText pagetitle, header_fullname;
    ArrayList<String> tempid, temptitle, tempdesc, tempdate, temptype, tempfiles, tempusers, tempuserid, tempdon, temprepcap,tempcaption, tempvalue;
    ArrayList<Boolean> temprep, tempfav, tempshowrep;
    PostAdapter adapter;
    ProfileAdapter profileAdapter;
    ViewEditText edittextHashtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        wScreen = HelperGlobal.GetDimension("w", ActivityProfile.this);
        hScreen = HelperGlobal.GetDimension("h",ActivityProfile.this);
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Profile");
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalyticsInit();
        init();
        getProfile();
    }

    private void init()
    {
        followAccess = "0";
        pid = new ArrayList<String>();
        ptitle = new ArrayList<String>();
        pdesc = new ArrayList<String>();
        pdate = new ArrayList<String>();
        ptype = new ArrayList<String>();
        pfiles = new ArrayList<String>();
        pusers = new ArrayList<String>();
        puserid = new ArrayList<String>();
        prep = new ArrayList<Boolean>();
        pfav = new ArrayList<Boolean>();
        prepcap = new ArrayList<String>();
        showrep = new ArrayList<Boolean>();
        prof_caption = new ArrayList<String>();
        prof_value = new ArrayList<String>();
        assignValues = new ArrayList<String>();

        adapter = new PostAdapter();
        profileAdapter = new ProfileAdapter();

        extra_target_id = getIntent().getExtras().getString("target");
        extra_mode_view = getIntent().getExtras().getString("mode");
        srcFrom = getIntent().getExtras().getString("src");
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listview_post = (ListView) findViewById(R.id.profile_listview_posted);
        viewHeader = inflater.inflate(R.layout.listview_header_profile, null, false);
        viewFooter = inflater.inflate(R.layout.listview_footer_profile, null, false);
        listview_post.addHeaderView(viewHeader);
        listview_post.addFooterView(viewFooter);
        container = (LinearLayout) findViewById(R.id.profile_linear_data);
        pagetitle = (ViewText) findViewById(R.id.main_textview_pagetitle);
        header_fullname = (ViewText) findViewById(R.id.profile_textview_fullname);
        imageview_avatar = (CircularImageView) findViewById(R.id.profile_imageview_avatar);
        imageview_cover = (ImageView) findViewById(R.id.profile_imageview_header);
        button_follow = (ViewButton) findViewById(R.id.profile_button_follow);
        imagebutton_back = (ImageButton) findViewById(R.id.main_imagebutton_menu);
        linearVerified = (LinearLayout) findViewById(R.id.timeline_linear_followed);
        relativeSearch = (RelativeLayout) findViewById(R.id.profile_search_article_container);
        button_profile = (ViewButton) viewHeader.findViewById(R.id.profile_button_tab_profile);
        button_posted = (ViewButton) viewHeader.findViewById(R.id.profile_button_tab_post);
        button_schedule = (ViewButton) viewHeader.findViewById(R.id.profile_button_tab_schedule);
        edittextHashtag = (ViewEditText) viewHeader.findViewById(R.id.profile_search_hashtag_edittext);

        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        button_profile.getLayoutParams().width = wScreen / 3;
        button_profile.setText(getResources().getString(R.string.v1_profile_tab_profile));
        button_profile.setVisibility(View.VISIBLE);
        button_profile.setRegular();
        button_posted.getLayoutParams().width = wScreen / 3;
        button_posted.setText(getResources().getString(R.string.v1_profile_tab_post));
        button_posted.setVisibility(View.VISIBLE);
        button_posted.setRegular();
        button_schedule.getLayoutParams().width = wScreen / 3;
        button_schedule.setText(getResources().getString(R.string.v1_profile_tab_schedule));
        button_schedule.setVisibility(View.VISIBLE);
        button_schedule.setRegular();
        pagetitle.setText(getResources().getString(R.string.v1_profile_tab_profile));
        listview_post.setAdapter(profileAdapter);

        button_profile.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_active));
        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_profile.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_active));
                button_posted.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                button_schedule.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                edittextHashtag.setText("");
                isTabPost = false;
                isTabSchedule = false;
                isTabProfile = true;
                profileAdapter.clear();
                profileAdapter.notifyDataSetChanged();
                listview_post.setAdapter(profileAdapter);
                getPost();
            }
        });

        button_posted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_profile.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                button_posted.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_active));
                button_schedule.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                isTabPost = true;
                isTabSchedule = false;
                isTabProfile = false;
                adapter.clear();
                adapter.notifyDataSetChanged();
                listview_post.setAdapter(adapter);
                contentType = "article";
                edittextHashtag.setText("");
                getPost();
            }
        });
        button_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_profile.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                button_posted.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_normal));
                button_schedule.setBackgroundColor(getResources().getColor(R.color.v1_profile_tab_active));
                isTabPost = false;
                isTabProfile = false;
                isTabSchedule = true;
                adapter.clear();
                adapter.notifyDataSetChanged();
                listview_post.setAdapter(adapter);
                contentType = "schedule";
                edittextHashtag.setText("");
                getPost();
            }
        });
        edittextHashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() > 2)
                {
                    hashtag = edittextHashtag.getText().toString();
                }
                else
                {
                    hashtag = "";
                }
                adapter.clear();
                adapter.notifyDataSetChanged();
                getPost();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getProfile()
    {
        new AsyncTask<Void, Integer, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionProfile profile_list = new ActionProfile(ActivityProfile.this);
                profile_list.setParam(getParam(), getToken());
                profile_list.setModeView(extra_mode_view);
                if(extra_mode_view.equals("view"))
                {
                    profile_list.setTarget(extra_target_id);
                }
                profile_list.executeListProfile();
                if(profile_list.getSuccess())
                {
                    assignValues.addAll(profile_list.getValues());
                    success = true;
                }
                else
                {
                    message = profile_list.getMessage();
                    success = false;
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(success)
                {
                    u = assignValues.get(2).toString();
                    fu = assignValues.get(3).toString();
                    e = assignValues.get(4).toString();
                    ph = assignValues.get(5).toString();
                    d = assignValues.get(6).toString();
                    ava = assignValues.get(7).toString();
                    i = assignValues.get(0).toString();
                    ids = assignValues.get(1).toString();
                    longs = assignValues.get(8).toString();
                    lat = assignValues.get(9).toString();
                    cover = assignValues.get(10).toString();
                    followAccess = assignValues.get(11).toString();
                    loc = assignValues.get(12).toString();
                    ugid = assignValues.get(13).toString();
                    if(extra_mode_view.equals("view"))
                    {
                        if(!extra_target_id.equals(getParam()))
                        {
                            button_follow.setVisibility(View.VISIBLE);
                            checkFollow();
                        }
                        if(srcFrom.equals("4"))
                        {
                            gpid = getIntent().getExtras().getString("gpid");
                            gptitle = getIntent().getExtras().getString("gptitle");
                        }
                    }
                    success = false;
                    message = "";

                    header_fullname.setText(fu);
                    if(ava != null && !ava.equals(""))
                    {
                        final boolean checkInternalAvatar = HelperGlobal.CheckInternalImage(ActivityProfile.this, ava);
                        final String pathAvatar;
                        if(checkInternalAvatar)
                        {
                            pathAvatar = HelperGlobal.GetInternalPath(ActivityProfile.this, ava);
                        }
                        else {
                            pathAvatar = ava;
                        }

                        Target targetAvatar = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if(!checkInternalAvatar)
                                {
                                    HelperGlobal.SaveBitmapLocal(pathAvatar, ActivityProfile.this, bitmap);
                                }
                                imageview_avatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                imageview_avatar.setAdjustViewBounds(true);
                                imageview_avatar.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.with(ActivityProfile.this).load(pathAvatar).into(targetAvatar);
                    }
                    if(cover != null && !cover.equals(""))
                    {
                        final boolean checkInternalCover = HelperGlobal.CheckInternalImage(ActivityProfile.this, cover);
                        final String pathCover;
                        if(checkInternalCover)
                        {
                            pathCover = HelperGlobal.GetInternalPath(ActivityProfile.this, cover);
                        }
                        else {
                            pathCover = cover;
                        }

                        Target targetCover = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if(!checkInternalCover)
                                {
                                    HelperGlobal.SaveBitmapLocal(pathCover, ActivityProfile.this, bitmap);
                                }
                                if(bitmap.getHeight() > bitmap.getWidth())
                                {
                                    imageview_cover.getLayoutParams().height = imageview_cover.getLayoutParams().width;
                                    imageview_cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageview_cover.setAdjustViewBounds(true);
                                    imageview_cover.setImageBitmap(bitmap);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.with(ActivityProfile.this).load(pathCover).into(targetCover);
                    }
                    getPost();
                }
            }
        }.execute();
    }

    private void runThread()
    {
        threadPost = null;
        threadPost = new Thread(runnablePost);
        threadPost.start();
    }

    private Runnable runnablePost = new Runnable() {
        @Override
        public void run() {
            if(isThreadRunning)
            {
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempdate = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempfiles = new ArrayList<String>();
                tempusers = new ArrayList<String>();
                tempuserid = new ArrayList<String>();
                tempdon = new ArrayList<String>();
                pdon = new ArrayList<String>();
                temprep = new ArrayList<Boolean>();
                tempfav = new ArrayList<Boolean>();
                temprepcap = new ArrayList<String>();
                tempshowrep = new ArrayList<Boolean>();
                tempcaption = new ArrayList<String>();
                tempvalue = new ArrayList<String>();

                ActionPost post = new ActionPost(ActivityProfile.this);
                post.setParam(getParam(), getToken());
                post.setLimit(l);
                post.setOffset(o);
                post.setMethod(extra_mode_view);
                if(extra_mode_view.equals("view"))
                {
                    post.setTarget(extra_target_id);
                }
                post.setContentType(contentType);
                post.setHashTag(hashtag);
                post.executeList();
                if(post.getSuccess())
                {
                    success = true;
                    if(isTabPost || isTabSchedule)
                    {
                        tempid.addAll(post.getPID());
                        temptitle.addAll(post.getPTitle());
                        tempdesc.addAll(post.getPDesc());
                        tempdate.addAll(post.getPDate());
                        tempusers.addAll(post.getPUsers());
                        temptype.addAll(post.getPType());
                        tempfiles.addAll(post.getPFiles());
                        tempuserid.addAll(post.getPUserid());
                        tempdon.addAll(post.getPDon());
                        tempfav.addAll(post.getPFav());
                        temprep.addAll(post.getPRep());
                        temprepcap.addAll(post.getPRepCaption());
                        tempshowrep.addAll(post.getPShowRep());
                        o = post.getOffset();
                    }
                    else
                    {
                        tempcaption.add(0, getResources().getString(R.string.v1_profile_table_caption_username));
                        tempcaption.add(1, getResources().getString(R.string.v1_profile_table_caption_email));
                        tempcaption.add(2, getResources().getString(R.string.v1_profile_table_caption_phone));
                        tempcaption.add(3, getResources().getString(R.string.v1_profile_table_caption_fullname));
                        tempcaption.add(4, getResources().getString(R.string.v1_profile_table_caption_location));
                        tempcaption.add(5, getResources().getString(R.string.v1_profile_table_caption_desc));
                        tempvalue.add(0, u);
                        tempvalue.add(1, e);
                        tempvalue.add(2, ph);
                        tempvalue.add(3, fu);
                        tempvalue.add(4, loc);
                        tempvalue.add(5, d);
                    }
                }
                else
                {
                    success = false;
                    message = post.getMessage();
                }

                handlerData.sendEmptyMessage(0);
            }
        }
    };

    private Handler handlerData = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(success)
            {
                isThreadRunning = false;
                if(isTabPost || isTabSchedule)
                {
                    if(isTabPost)
                    {
                        relativeSearch.setVisibility(View.VISIBLE);
                    }
                    if(isTabSchedule)
                    {
                        relativeSearch.setVisibility(View.GONE);
                    }
                    viewFooter.setVisibility(View.GONE);
                    if(tempid.size() > 0)
                    {
                        for(int i = 0;i < tempid.size();i++)
                        {
                            pid.add(tempid.get(i));
                            ptitle.add(temptitle.get(i));
                            pdesc.add(tempdesc.get(i));
                            pdate.add(tempdate.get(i));
                            pusers.add(tempusers.get(i));
                            ptype.add(temptype.get(i));
                            pfiles.add(tempfiles.get(i));
                            puserid.add(tempuserid.get(i));
                            pdon.add(tempdon.get(i));
                            prep.add(temprep.get(i));
                            pfav.add(tempfav.get(i));
                            prepcap.add(temprepcap.get(i));
                            showrep.add(tempshowrep.get(i));
                        }
                        adapter.notifyDataSetChanged();
                        listview_post.setOnScrollListener(new EndlessScrollListener() {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount) {
                                loadMore();
                            }
                        });
                    }
                }
                else
                {
                    prof_caption.addAll(tempcaption);
                    prof_value.addAll(tempvalue);
                    viewFooter.setVisibility(View.VISIBLE);
                    relativeMaps = (RelativeLayout) findViewById(R.id.profile_table_relative_maps);
                    button_edit = (RelativeLayout) findViewById(R.id.profile_relative_tabdetail_button_edit);
                    if(ugid.equals("6"))
                    {
                        relativeMaps.setVisibility(View.VISIBLE);
                    }
                    if(!extra_mode_view.equals("view") && extra_target_id.equals(getParam()))
                    {
                        button_edit.setVisibility(View.VISIBLE);
                        button_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ActivityProfile.this, ActivityProfileEdit.class);
                                i.putExtra("src", srcFrom);
                                i.putExtra("target", extra_target_id);
                                i.putExtra("mode", extra_mode_view);
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                    initMap();
                }

            }
            else
            {
                Toast.makeText(ActivityProfile.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getPost()
    {
        o = 0;
        isThreadRunning = true;
        runThread();
    }

    private void loadMore()
    {
        isThreadRunning = true;
        runThread();
    }


    private void doFollow()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                button_follow.setEnabled(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","target","mode-follow"};
                String[] values = new String[]{getToken(), getParam(), extra_target_id,modeFollow};
                ActionProfile profile = new ActionProfile(ActivityProfile.this);
                profile.setArrayPOST(field, values);
                profile.executeFollow();
                if(profile.getSuccess())
                {
                    isSuccess = true;
                }
                else {
                    isSuccess = false;
                    msg = profile.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    if (!followAccess.equals("0")) {
                        followAccess = "0";
                    } else {
                        followAccess = "1";
                    }
                    button_follow.setEnabled(true);
                    checkFollow();
                }
                else
                {
                    Toast.makeText(ActivityProfile.this, msg, Toast.LENGTH_SHORT).show();
                }

            }
        }.execute();
    }

    private void checkFollow()
    {
        if(!followAccess.equals("0"))
        {
            button_follow.setText(getResources().getString(R.string.base_string_follow));
            modeFollow = "f";
            linearVerified.setVisibility(View.GONE);
        }
        else {
            button_follow.setText(getResources().getString(R.string.base_string_unfollow));
            modeFollow = "uf";
            linearVerified.setVisibility(View.VISIBLE);
        }

        button_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doFollow();
            }
        });
    }

    private void initMap() {
        if (maps == null) {
            maps = ((ViewSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if(maps != null)
            {


                maps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                maps.getUiSettings().setZoomControlsEnabled(true);
                maps.setMyLocationEnabled(true);

                if(longs != null && lat != null && !longs.equals("null") && !lat.equals("null"))
                {
                    double val_long = Double.parseDouble(longs);
                    double val_lat = Double.parseDouble(lat);
                    LatLng lng = new LatLng(val_lat, val_long);
                    maps.addMarker(new MarkerOptions()
                            .position(lng)
                            .title(fu)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
                    maps.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, 16.6f));
                }
            }
            if (maps == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
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

    private void back()
    {
        if(groupList != null && groupList.isAdded())
        {
            removeFragmentGroupList();
        }
        else
        {
            if(srcFrom != null)
            {
                Intent i;
                if(srcFrom.equals("2"))
                {
                    i = new Intent(ActivityProfile.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else if(srcFrom.equals("3"))
                {
                    i = new Intent(ActivityProfile.this,ActivitySearch.class);
                    startActivity(i);
                    finish();
                }
                else if(srcFrom.equals("4"))
                {
                    i = new Intent(ActivityProfile.this,ActivityGroupListPost.class);
                    i.putExtra("gpid",gpid);
                    i.putExtra("gptitle",gptitle);
                    startActivity(i);
                    finish();
                }
                else {
                    i = new Intent(ActivityProfile.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            else {
                Intent i = new Intent(ActivityProfile.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }
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
        final ViewDialogList dialog = new ViewDialogList(ActivityProfile.this);
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
                    Toast.makeText(ActivityProfile.this, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                    if(object[0].equals("3"))
                    {
                        Intent i = new Intent(ActivityProfile.this, ActivityReportPost.class);
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
                        addFragmentGroupList(pid.get(pos));
                    }
                    else {
                        ImageButton btnFlag = new ImageButton(ActivityProfile.this);
                        repost(pos, btnFlag,"1");
                    }
                    dialog.dismiss();
                }
            }
        });
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

    private void loadDetail(int position)
    {
        Intent i = new Intent(ActivityProfile.this, ActivityPostDetail.class);
        i.putExtra("pid", pid.get(position));
        i.putExtra("src",srcFrom);
        i.putExtra("subSrc","prof");
        i.putExtra("target",extra_target_id);
        i.putExtra("mode", extra_mode_view);
        startActivity(i);
        finish();
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
                .replace(R.id.profile_relative_fragment, groupList, FragmentGroupList.TAG_GROUP_LIST)
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
            String msg, modeRepost;
            ViewLoadingDialog dialog;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityProfile.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param", "pid", "act"};
                String[] value = new String[]{getToken(),getParam(), pid.get(position), action};
                ActionPost post = new ActionPost(ActivityProfile.this);
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
                    }
                    else {
                        for(int i = 0;i < pid.size();i++)
                        {
                            if(pid.get(i).equals(pid.get(position)))
                            {
                                pid.remove(i);
                                ptitle.remove(i);
                                pdesc.remove(i);
                                pdate.remove(i);
                                ptype.remove(i);
                                pfiles.remove(i);
                                pusers.remove(i);
                                puserid.remove(i);
                                prep.remove(i);
                                pfav.remove(i);
                                prepcap.remove(i);
                                showrep.remove(i);
                                //adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                Toast.makeText(ActivityProfile.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void item_youtube_player(final String yid)
    {
        ViewYoutubeDialog dialog = new ViewYoutubeDialog();
        dialog.setYoutubeID(yid);
        dialog.setTitle("Youtube Player");
        dialog.show(getSupportFragmentManager(), "youtube_player");
    }



    public class PostAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public PostAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText textusers, texttitle, texttime, textdesc, textrepost;
            ImageButton btn_menu, btn_favorit;
            ImageView image_headline;
            ViewButton btn_more;
            LinearLayout containerItem;
            RelativeLayout containerRepost;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.main_listview_item, null);
            }

            String pathImage = "";

            final ViewHolder holder = new ViewHolder();
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
            holder.texttime.setText(pdate.get(position));

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

            holder.btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pid.get(position).equals("0"))
                    {
                        holder.btn_menu.setEnabled(false);
                    }
                    else
                    {
                        showItemOption(position, ptitle.get(position), puserid.get(position), pdon.get(position), pdesc.get(position), prep.get(position));
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
                        Intent intent = new Intent(ActivityProfile.this, ActivityProfile.class);
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
                pathImage = HelperGlobal.BASE_UPLOAD + pfiles.get(position);
                Picasso.with(ActivityProfile.this).load(pathImage).into(holder.image_headline, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.image_headline.setAdjustViewBounds(true);
                        holder.image_headline.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
            else if(ptype.get(position).equals("youtube"))
            {
                pathImage = "https://img.youtube.com/vi/" + pfiles.get(position)+"/0.jpg";
                holder.image_headline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item_youtube_player(pfiles.get(position));
                    }
                });
                Picasso.with(ActivityProfile.this).load(pathImage).into(holder.image_headline, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.image_headline.setAdjustViewBounds(true);
                        holder.image_headline.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
            else {
                holder.image_headline.setImageResource(R.drawable.bg_timeline_transparent);
            }

            convertView.setTag(holder);
            return convertView;
        }

        public void clear()
        {
            pid.clear();
            ptitle.clear();
            pdesc.clear();
            pdate.clear();
            ptype.clear();
            pfiles.clear();
            pusers.clear();
            puserid.clear();
            prep.clear();
            pfav.clear();
            prepcap.clear();
            showrep.clear();
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



    public class ProfileAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ProfileAdapter() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText txt_caption, txt_value;
            RelativeLayout container;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            //ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_item_profile_detail, null);
            }


            final ViewHolder holder = new ViewHolder();
            holder.txt_caption = (ViewText) convertView.findViewById(R.id.listview_item_profile_txt_caption);
            holder.txt_value = (ViewText) convertView.findViewById(R.id.listview_item_profile_txt_value);
            holder.container = (RelativeLayout) convertView.findViewById(R.id.listview_item_profile_container);
            if(extra_mode_view.equals("view") && !extra_target_id.equals(getParam()))
            {
                if(position == 0 || position == 1 || position == 2)
                {
                    holder.container.setVisibility(View.GONE);
                }
                else
                {
                    holder.container.setVisibility(View.VISIBLE);
                }
            }

            holder.txt_caption.setText(prof_caption.get(position));
            holder.txt_value.setText(prof_value.get(position));

            convertView.setTag(holder);


            return convertView;
        }

        public void clear()
        {
            prof_caption.clear();
            prof_value.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return prof_caption.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return prof_caption.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
