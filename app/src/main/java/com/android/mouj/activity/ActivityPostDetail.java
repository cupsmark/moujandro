package com.android.mouj.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.FailReason;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.android.mouj.fragment.FragmentGroupList;
import com.android.mouj.fragment.FragmentYoutube;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionGroup;
import com.android.mouj.models.ActionPost;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewDialogList;
import com.android.mouj.view.ViewDialogMessage;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ActivityPostDetail extends BaseActivity{

    String pid,ptitle,pdesc,pusers,pdate,ptype,pfile, extra_src, extra_sub_src, extra_target, extra_mode, downloadLink, gpid, gptitle, con_type, con_repeat, con_d_start, con_location;
    ArrayList<String> mediatype,mediafile;
    ImageButton button_back,button_edit, button_delete, button_share, btn_option, btn_favorit, btn_repost_schedule;
    ViewLoadingDialog dialog;
    ViewText text_users, text_desc, text_minute, text_title, text_schedule_time_caption, text_schedule_time_value, text_schedule_location_caption, text_schedule_location_value;
    LayoutInflater inflater;
    ArrayList<String> typeVideo = new ArrayList<String>();
    ArrayList<String> typeAudio = new ArrayList<String>();
    ArrayList<String> typeImage = new ArrayList<String>();
    ArrayList<String> typeLink = new ArrayList<String>();
    ProgressDialog mProgressDialog;
    boolean isEdit = false,isDelete = false, fav, rep, showrep;
    DisplayImageOptions opt;
    ImageLoader loader;
    ArrayList<Uri> uriList;
    MediaPlayer media;
    SeekBar seekbar_mp3;
    Handler handler_mp3;
    ViewText mp3Timer;
    int stat_play = 0;
    Tracker tracker;
    FragmentGroupList groupList;
    ImageButton button_playMp3;
    WebView youtubePlayer;
    RelativeLayout relativeScheduleTime,relativeScheduleLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_post_detail);

        setShowingMenu(false);

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
    }

    private void init()
    {
        extra_src = getIntent().getExtras().getString("src");
        extra_sub_src = getIntent().getExtras().getString("subSrc");
        if(extra_sub_src.equals("prof"))
        {
            extra_target = getIntent().getExtras().getString("target");
            extra_mode = getIntent().getExtras().getString("mode");
        }
        if(extra_sub_src.equals("post-group"))
        {
            gpid = getIntent().getExtras().getString("gpid");
            gptitle = getIntent().getExtras().getString("gptitle");
        }
        pid = "";
        ptitle = "";
        pdesc = "";
        pdate = "";
        pusers = "";
        ptype = "";
        pfile = "";
        downloadLink = "";
        con_type = "article";
        con_d_start = "";
        con_repeat = "";
        con_location = "";
        handler_mp3 = new Handler();
        Collections.addAll(typeVideo,"youtube");
        Collections.addAll(typeAudio,"mp3","wav");
        Collections.addAll(typeImage,"jpg","jpeg","png","bmp","gif");
        Collections.addAll(typeLink,"link");
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pid = getIntent().getExtras().getString("pid");
        mediafile = new ArrayList<String>();
        mediatype = new ArrayList<String>();
        text_users = (ViewText) findViewById(R.id.detail_post_textview_users_fullname);
        text_users.setRegular();
        text_title = (ViewText) findViewById(R.id.detail_post_textview_title);
        text_title.setRegular();
        text_desc = (ViewText) findViewById(R.id.detail_post_textview_desc);
        text_minute = (ViewText) findViewById(R.id.detail_post_textview_time_uploaded);
        button_back = (ImageButton) findViewById(R.id.detail_post_imagebutton_back);
        button_delete = (ImageButton) findViewById(R.id.detail_post_imagebutton_delete);
        button_edit = (ImageButton) findViewById(R.id.detail_post_imagebutton_edit);
        button_share  = (ImageButton) findViewById(R.id.detail_post_imagebutton_share);
        btn_option = (ImageButton) findViewById(R.id.detail_post_imagebutton_option);
        btn_favorit = (ImageButton) findViewById(R.id.detail_post_imagebutton_favorit);
        btn_repost_schedule = (ImageButton) findViewById(R.id.detail_post_imagebutton_repost_schedule);
        relativeScheduleTime = (RelativeLayout) findViewById(R.id.detail_post_relative_schedule_time);
        relativeScheduleLocation = (RelativeLayout) findViewById(R.id.detail_post_relative_schedule_location);
        text_schedule_time_caption = (ViewText) findViewById(R.id.detail_post_textview_schedule_time_caption);
        text_schedule_time_value = (ViewText) findViewById(R.id.detail_post_textview_schedule_time_value);
        text_schedule_location_caption = (ViewText) findViewById(R.id.detail_post_textview_schedule_location_caption);
        text_schedule_location_value = (ViewText) findViewById(R.id.detail_post_textview_schedule_location_value);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        fetch();
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityPostDetail.this);
                dialog.setCancelable(false);
                dialog.show();
                uriList = new ArrayList<Uri>();
            }

            @Override
            protected String doInBackground(Void... params) {

                ActionPost post = new ActionPost(ActivityPostDetail.this);
                post.setParam(getParam(), getToken());
                post.setPID(pid);
                post.executeDetail();
                if(post.getSuccess())
                {
                    ptitle = post.getPTitle().get(0);
                    pdesc = post.getPDesc().get(0);
                    pdate = post.getPDate().get(0);
                    pusers = post.getPUsers().get(0);
                    mediatype.addAll(post.getMediaType());
                    mediafile.addAll(post.getMediaFile());
                    isEdit = post.getEdit();
                    isDelete = post.getDelete();
                    downloadLink = post.getDownloadLink();
                    fav = post.getPFav().get(0);
                    rep = post.getPRep().get(0);
                    showrep = post.getPShowRep().get(0);
                    con_type = post.getPContentType().get(0);
                    con_d_start = post.getPContentDateStart().get(0);
                    con_repeat = post.getPContentRepeat().get(0);
                    con_location = post.getPContentLocation().get(0);
                    isSuccess = true;
                }
                else
                {
                    isSuccess = false;
                    msg = post.getMessage();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(isSuccess)
                {
                    GoogleAnalyticsInit("Page - " + ptitle);
                    if(isEdit)
                    {
                        button_edit.setVisibility(View.VISIBLE);
                        button_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ActivityPostDetail.this, ActivityCreatePost.class);
                                i.putExtra("mode-action","edit");
                                i.putExtra("target",pid);
                                i.putExtra("src","3");
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                    if(isDelete)
                    {
                        button_delete.setVisibility(View.VISIBLE);
                        button_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDelete();
                            }
                        });
                    }
                    text_users.setText(pusers);
                    text_desc.setText(pdesc);
                    text_title.setText(ptitle);
                    text_minute.setText(pdate);


                    if(mediatype.size() > 0)
                    {
                        LinearLayout layout_media_bottom = (LinearLayout) findViewById(R.id.detail_post_linear_attachment_container);
                        LinearLayout layout_media_top = (LinearLayout) findViewById(R.id.detail_post_component_header);
                        if(mediatype.size() <= 1)
                        {

                            inflate_attachment(layout_media_top, mediatype.get(0), mediafile.get(0));
                            layout_media_top.setVisibility(View.VISIBLE);
                        }
                        else {
                            inflate_header_media(layout_media_top, mediatype.get(0), mediafile.get(0));
                            //inflate_attachment(layout_media_top, mediatype.get(0), mediafile.get(0));
                            layout_media_top.setVisibility(View.VISIBLE);
                            for(int i = 1;i < mediatype.size();i++)
                            {
                                inflate_attachment(layout_media_bottom, mediatype.get(i), mediafile.get(i));
                            }
                            layout_media_bottom.setVisibility(View.VISIBLE);
                        }
                    }
                    button_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            share();
                        }
                    });
                    btn_option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showItemOption(pid, ptitle, pusers, downloadLink, pdesc);
                        }
                    });
                    if(!fav)
                    {
                        btn_favorit.setImageResource(R.drawable.icon_favorite_normal);
                    }
                    if(fav)
                    {
                        btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
                    }
                    btn_favorit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            repost(pid, btn_favorit, "1");
                        }
                    });
                    if(con_type.equals("schedule"))
                    {
                        if(!fav)
                        {
                            btn_repost_schedule.setVisibility(View.VISIBLE);
                        }
                        String dateParser = TimeUtils.convertFormatDate(con_d_start, "dt") + ", "+
                                TimeUtils.convertFormatDate(con_d_start, "d") + " " +
                                TimeUtils.convertFormatDate(con_d_start, "M") + " " +
                                TimeUtils.convertFormatDate(con_d_start, "Y") + " Pukul " +
                                TimeUtils.convertFormatDate(con_d_start, "H");
                        relativeScheduleTime.setVisibility(View.VISIBLE);
                        relativeScheduleLocation.setVisibility(View.VISIBLE);
                        text_schedule_time_caption.setSemiBold();
                        text_schedule_time_value.setText(dateParser);
                        text_schedule_location_caption.setSemiBold();
                        text_schedule_location_value.setText((con_location.equals("") ? "-" : con_location));
                        btn_repost_schedule.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                repost(pid, btn_favorit, "1");
                            }
                        });
                    }

                }
                else {
                    Toast.makeText(ActivityPostDetail.this, msg, Toast.LENGTH_SHORT).show();
                }



            }
        }.execute();
    }

    private void repost(final String pid,final ImageButton btn_favorit, final String action)
    {
        new AsyncTask<Void, Integer, String>()
        {
            boolean success = false;
            String msg;
            ViewLoadingDialog dialog;
            String modeRepost, ntype, ntitle, nticker, nmessage;
            ArrayList<String> dateList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityPostDetail.this);
                dialog.setCancelable(false);
                dialog.show();
                dateList = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param", "pid", "act", "content_type"};
                String[] value = new String[]{getToken(),getParam(), pid, action, con_type};
                ActionPost post = new ActionPost(ActivityPostDetail.this);
                post.setArrayPOST(field, value);
                post.executeRepost();
                if(post.getSuccess())
                {
                    success = true;
                    modeRepost = post.getModeRepost();
                    if(modeRepost.equals("add-schedule"))
                    {
                        ntype = post.getNotifType();
                        ntitle = post.getNotifTitle();
                        nmessage = post.getNotifMessage();
                        nticker = post.getNotifTicker();
                        dateList.addAll(post.getDateList());
                    }
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
                        rep = true;
                        btn_favorit.setImageResource(R.drawable.icon_favorite_hover);
                        btn_repost_schedule.setVisibility(View.GONE);
                    }
                    else if(modeRepost.equals("del")){
                        rep = false;
                        btn_favorit.setImageResource(R.drawable.icon_favorite_normal);
                        btn_repost_schedule.setVisibility(View.VISIBLE);
                    }
                    else {
                        btn_repost_schedule.setVisibility(View.GONE);
                        if(dateList.size() > 0)
                        {
                            String token = getToken();
                            String param = getParam();
                            for(int i = 0;i < dateList.size();i++)
                            {
                                HelperGlobal.setAlarmManager(ActivityPostDetail.this,5000+i, ntype, ntitle, nmessage, nticker, token, param, dateList.get(i), "run.schedule");
                            }
                        }
                    }
                }
                Toast.makeText(ActivityPostDetail.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void inflate_header_media(LinearLayout layout, String type, final String filename)
    {
        if(typeVideo.contains(type))
        {
            FragmentYoutube youtube = FragmentYoutube.newInstance(filename, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_post_component_header, youtube).commit();
        }
        if(typeAudio.contains(type))
        {
            View mp3player = inflater.inflate(R.layout.view_music_player, null);
            layout.addView(mp3player);
            LinearLayout mp3_container = (LinearLayout) mp3player.findViewById(R.id.view_music_player_base);
            mp3_container.setVisibility(View.VISIBLE);
            initMp3Player(HelperGlobal.BASE_UPLOAD + filename);
        }
        if(typeImage.contains(type))
        {
            final ImageButton button_image = new ImageButton(ActivityPostDetail.this);
            final LinearLayout.LayoutParams paramButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button_image.setLayoutParams(paramButton);
            button_image.setAdjustViewBounds(true);
            button_image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            button_image.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            button_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_dialog(HelperGlobal.BASE_UPLOAD + filename);
                }
            });
            layout.addView(button_image);
            loader.displayImage(HelperGlobal.BASE_UPLOAD + filename, button_image, opt, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage.getWidth() < loadedImage.getHeight()) {
                        button_image.getLayoutParams().height = loadedImage.getHeight() / 2;
                        button_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    private void inflate_attachment(LinearLayout layout, String type, final String filename)
    {
        View item = inflater.inflate(R.layout.detail_post_attachment_item, null);
        layout.addView(item);
        LinearLayout componentContainer = (LinearLayout) item.findViewById(R.id.detail_post_item_container_component);
        ImageView icon_type = (ImageView) item.findViewById(R.id.detail_post_item_attachment_icontype);
        final ImageButton button_image = (ImageButton) item.findViewById(R.id.detail_post_item_attachment_imagebutton);
        youtubePlayer = new WebView(ActivityPostDetail.this);
        //final WebView youtubePlayer = (WebView) item.findViewById(R.id.webViewYoutube);
        if(typeVideo.contains(type))
        {
            FragmentYoutube youtube = FragmentYoutube.newInstance(filename, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_post_item_container_component, youtube).commit();
            /*//String yID = HelperGlobal.getYoutubeID(filename);
            String html = "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"http://www.youtube.com/embed/"
                    + ""+filename
                    + "?fs=0\" frameborder=\"0\">\n"
                    + "</iframe>\n";
            icon_type.setImageResource(R.drawable.v1_icon_home_video);
            LinearLayout.LayoutParams paramplayer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramplayer.height = (int) getResources().getDimension(R.dimen.youtube_player_height);
            youtubePlayer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            youtubePlayer.setVisibility(View.VISIBLE);
            youtubePlayer.getSettings().setJavaScriptEnabled(true);
            youtubePlayer.getSettings().setPluginState(WebSettings.PluginState.ON);
            youtubePlayer.getSettings().setAllowFileAccess(true);
            youtubePlayer.setWebViewClient(new WebViewClient() {


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    // TODO Auto-generated method stub


                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // TODO Auto-generated method stub
                    //cookieManager.setAcceptCookie(true);

                    super.onPageFinished(view, url);
                }


                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_FILE_NOT_FOUND || errorCode == ERROR_CONNECT ||
                            errorCode == ERROR_UNKNOWN || errorCode == ERROR_BAD_URL) {
                        youtubePlayer.loadUrl("file:///android_asset/not_found.html");
                    }
                }
            });
            youtubePlayer.setWebChromeClient(new WebChromeClient());
            youtubePlayer.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");*/
            //componentContainer.addView(youtubePlayer);
            //componentContainer.addView(youtubePlayerView);
        }
        if(typeAudio.contains(type))
        {
            View mp3view = inflater.inflate(R.layout.view_music_player, null);
            componentContainer.addView(mp3view);
            LinearLayout mp3_container = (LinearLayout) item.findViewById(R.id.view_music_player_base);
            icon_type.setImageResource(R.drawable.v1_icon_home_audio);
            mp3_container.setVisibility(View.VISIBLE);
            initMp3Player(HelperGlobal.BASE_UPLOAD + filename);
        }
        if(typeImage.contains(type))
        {
            icon_type.setImageResource(R.drawable.v1_icon_home_quote);
            button_image.setVisibility(View.VISIBLE);
            button_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_dialog(HelperGlobal.BASE_UPLOAD + filename);
                }
            });
            loader.displayImage(HelperGlobal.BASE_UPLOAD + filename, button_image, opt, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage.getWidth() < loadedImage.getHeight()) {
                        button_image.getLayoutParams().height = loadedImage.getHeight() / 2;
                        button_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
        /*ViewButton button_file = (ViewButton) item.findViewById(R.id.detail_post_item_attachment_filename);
        button_file.setText("Download File");
        button_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(HelperGlobal.BASE_UPLOAD + filename);
            }
        });*/
        Uri u = Uri.parse(HelperGlobal.BASE_UPLOAD + filename);
        uriList.add(u);
    }

    private void item_dialog(String files)
    {
        final Dialog d = new Dialog(ActivityPostDetail.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.post_detail_imageviewer);
        d.setCancelable(true);


        ImageView img = (ImageView) d.findViewById(R.id.post_detail_imageviewer);
        loader.displayImage(files, img, opt);
        d.show();
    }

    private void initMp3Player(final String source)
    {
        button_playMp3 = (ImageButton) findViewById(R.id.view_music_player_play);
        seekbar_mp3 = (SeekBar) findViewById(R.id.view_music_player_seekbar);
        mp3Timer = (ViewText) findViewById(R.id.view_music_player_time);

        media = new MediaPlayer();
        seekbar_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (handler_mp3 != null) {
                    if (mUpdateTimeMP3 != null) {
                        handler_mp3.removeCallbacks(mUpdateTimeMP3);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler_mp3.removeCallbacks(mUpdateTimeMP3);
                int totalDuration = media.getDuration();
                int currentPosition = HelperGlobal.MP3ProgressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                media.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });

        button_playMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (stat_play == 0) {
                        media.reset();
                        media.setDataSource(source);
                        media.prepare();
                    }
                    if (media.isPlaying()) {
                        media.pause();
                        button_playMp3.setImageResource(R.drawable.icon_play);
                    } else {
                        media.start();
                        button_playMp3.setImageResource(R.drawable.icon_pause);
                    }
                    stat_play = 1;
                    // Displaying Song title


                    // Changing Button Image to pause image


                    // set Progress bar values
                    seekbar_mp3.setProgress(0);
                    seekbar_mp3.setMax(100);

                    // Updating progress bar
                    updateProgressBar();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(ActivityPostDetail.this, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(ActivityPostDetail.this, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(ActivityPostDetail.this, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateProgressBar() {
        handler_mp3.postDelayed(mUpdateTimeMP3, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeMP3 = new Runnable() {
        public void run() {
            long totalDuration = media.getDuration();
            long currentDuration = media.getCurrentPosition();

            // Displaying Total Duration time
            //mp3Timer.setText(""+HelperGlobal.MP3MillisToTimer(totalDuration));
            // Displaying time completed playing
            mp3Timer.setText("" + HelperGlobal.MP3MillisToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(HelperGlobal.MP3GetProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            if(progress >= 100)
            {
                button_playMp3.setImageResource(R.drawable.icon_play);
            }
            seekbar_mp3.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler_mp3.postDelayed(this, 100);
        }
    };

    private void download(String uri)
    {
        mProgressDialog = new ProgressDialog(ActivityPostDetail.this);
        mProgressDialog.setMessage("Download on progress");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        final DownloadTask downloadTask = new DownloadTask(ActivityPostDetail.this);
        downloadTask.execute(uri);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String dirs;

        public DownloadTask(Context context) {
            this.context = context;
            this.dirs = getResources().getString(R.string.app_name);
        }

        @Override
        protected String doInBackground(String... sUrl) {

            File baseDir = new File("/sdcard/"+ dirs);
            if(!baseDir.exists())
            {
                baseDir.mkdirs();
            }
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                File outputFile = new File(baseDir, sUrl[0].substring(sUrl[0].lastIndexOf("/") + 1));
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete()
    {
        final ViewDialogMessage dialogs = new ViewDialogMessage(ActivityPostDetail.this);
        dialogs.setTitle(getResources().getString(R.string.base_string_delete_title));
        dialogs.setMessage(getResources().getString(R.string.base_string_delete_message));
        dialogs.setCancelable(true);
        dialogs.show();

        dialogs.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.dismiss();
                doDelete();
            }
        });
        dialogs.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.dismiss();
            }
        });
    }

    private void doDelete()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] field = new String[]{"token","param","target"};
                String[] value = new String[]{getToken(),getParam(),pid};
                ActionPost post = new ActionPost(ActivityPostDetail.this);
                post.setArrayPOST(field,value);
                post.executeDelete();
                if(post.getSuccess())
                {
                    isSuccess = true;
                    msg = post.getMessage();
                }
                else
                {
                    isSuccess = false;
                    msg = post.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {
                    Toast.makeText(ActivityPostDetail.this, msg, Toast.LENGTH_SHORT).show();
                    back();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityPostDetail.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void share()
    {
        String desc = pdesc + "<br/><br/>Attachment : " + downloadLink;
        Intent specific_intent = new Intent();
        specific_intent.setAction(Intent.ACTION_SEND);
        specific_intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(desc).toString());
        specific_intent.putExtra(Intent.EXTRA_TITLE, ptitle);
        specific_intent.putExtra(Intent.EXTRA_SUBJECT, ptitle);
        specific_intent.setType("text/plain");
        specific_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(specific_intent);
    }

    private void back()
    {
        Intent i = null;
        if(extra_src != null)
        {
            if(extra_sub_src.equals("prof"))
            {
                i = new Intent(ActivityPostDetail.this, ActivityProfile.class);
                i.putExtra("target",extra_target);
                i.putExtra("mode",extra_mode);
                i.putExtra("src",extra_src);
            }
            else if(extra_sub_src.equals("search"))
            {
                i = new Intent(ActivityPostDetail.this, ActivitySearch.class);
            }
            else if(extra_sub_src.equals("notification"))
            {
                i = new Intent(ActivityPostDetail.this, ActivityNotification.class);
            }
            else if(extra_sub_src.equals("post-group"))
            {
                i = new Intent(ActivityPostDetail.this, ActivityGroupListPost.class);
                i.putExtra("gpid",gpid);
                i.putExtra("gptitle", gptitle);
            }
            else
            {
                i = new Intent(ActivityPostDetail.this, MainActivity.class);
            }
        }
        else {
            i = new Intent(ActivityPostDetail.this, MainActivity.class);
        }
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinearLayout layout_attachment = (LinearLayout) findViewById(R.id.detail_post_linear_attachment_container);
        layout_attachment.removeAllViewsInLayout();
        if(handler_mp3 != null)
        {
            if(media != null && media.isPlaying())
            {
                media.stop();
            }
            if(mUpdateTimeMP3 != null)
            {
                handler_mp3.removeCallbacks(mUpdateTimeMP3);
            }
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(groupList != null && groupList.isAdded())
            {
                removeFragmentGroupList();
            }
            else
            {
                back();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*LinearLayout layout_attachment = (LinearLayout) findViewById(R.id.detail_post_linear_attachment_container);
        layout_attachment.removeAllViewsInLayout();*/

    }


    private void showItemOption(final String ids, final String title, final String u, final String don, final String desc)
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
        final ViewDialogList dialog = new ViewDialogList(ActivityPostDetail.this);
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
                    Toast.makeText(ActivityPostDetail.this, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                    if(object[0].equals("3"))
                    {
                        Intent i = new Intent(ActivityPostDetail.this, ActivityReportPost.class);
                        i.putExtra("pi",ids);
                        i.putExtra("pt",title);
                        i.putExtra("u",u);
                        startActivity(i);
                        finish();
                    }
                    else if(object[0].equals("2"))
                    {
                        share();
                    }
                    else if(object[0].equals("4"))
                    {

                        addFragmentGroupList(ids);
                    }
                    else {
                        ImageButton btnFlag = new ImageButton(ActivityPostDetail.this);
                        repost(ids, btnFlag, "1");
                    }
                    dialog.dismiss();
                }
            }
        });
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
                .replace(R.id.post_detail_relative_fragment, groupList, FragmentGroupList.TAG_GROUP_LIST)
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
}
