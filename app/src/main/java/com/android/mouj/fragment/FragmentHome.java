package com.android.mouj.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.activity.ActivityCreatePost;
import com.android.mouj.activity.ActivityNotification;
import com.android.mouj.activity.ActivityPostDetail;
import com.android.mouj.activity.ActivityProfile;
import com.android.mouj.activity.ActivityReportPost;
import com.android.mouj.activity.ActivityScheduleTimeline;
import com.android.mouj.activity.ActivitySearch;
import com.android.mouj.external.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.FailReason;
import com.android.mouj.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.FragmentInterface;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.models.ActionPost;
import com.android.mouj.util.TimeUtils;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewDialogList;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ekobudiarto on 10/13/15.
 */
public class FragmentHome extends BaseFragment{

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_HOME = "tag:fragment-home";

    ImageButton button_menu, button_search, button_schedule, button_create, button_notification;
    ListView main_listview;
    ArrayList<String> pid,ptitle,pusers,ptype,pdesc,pfile,ptime,puserid,pdonlot;
    int l,o;
    ViewLoadingDialog dialog;
    MainAdapter adapter;
    MediaPlayer media;
    SeekBar seekbar_mp3;
    Handler handler_mp3;
    int stat_play = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_home, container, false);
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
        }
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
        handler_mp3 = new Handler();
        media = new MediaPlayer();
        main_listview = (ListView) activity.findViewById(R.id.main_listview_post);
        button_menu = (ImageButton) activity.findViewById(R.id.main_imagebutton_menu);
        button_search = (ImageButton) activity.findViewById(R.id.main_imagebutton_search);
        button_schedule = (ImageButton) activity.findViewById(R.id.main_imagebutton_schedule);
        button_create = (ImageButton) activity.findViewById(R.id.main_imagebutton_create);
        button_notification = (ImageButton) activity.findViewById(R.id.main_imagebutton_notification);

        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.toggleMenu(true);
            }
        });
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ActivitySearch.class);
                startActivity(i);
                activity.finish();
            }
        });
        button_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ActivityScheduleTimeline.class);
                startActivity(intent);
                activity.finish();
            }
        });
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ActivityCreatePost.class);
                i.putExtra("mode-action","add");
                i.putExtra("src","4");
                startActivity(i);
                activity.finish();
            }
        });
        button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(activity, ActivityNotification.class);
                startActivity(i);
                activity.finish();*/
                FragmentNotification notif = new FragmentNotification();
                iFragment.onNavigate(notif, TAG_HOME,true, FragmentNotification.TAG_NOTIF);

            }
        });
        fetch();
    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc,temptime,tempusers,temptype,tempfile,tempusersid, tempdonlot;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                temptime = new ArrayList<String>();
                tempusers = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempfile = new ArrayList<String>();
                tempusersid = new ArrayList<String>();
                tempdonlot = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionPost post = new ActionPost(activity);
                post.setParam(activity.getParam(), activity.getToken());
                post.setLimit(l);
                post.setOffset(o);
                post.setMethod("home");
                post.setTag(getTag());
                post.executeList();
                if(post.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(post.getPID());
                    temptitle.addAll(post.getPTitle());
                    tempdesc.addAll(post.getPDesc());
                    temptime.addAll(post.getPDate());
                    tempusers.addAll(post.getPUsers());
                    temptype.addAll(post.getPType());
                    tempfile.addAll(post.getPFiles());
                    tempusersid.addAll(post.getPUserid());
                    tempdonlot.addAll(post.getPDon());
                    o = post.getOffset();
                }
                else
                {
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
                    if(tempid.size() > 0)
                    {

                        pid.addAll(tempid);
                        ptitle.addAll(temptitle);
                        pdesc.addAll(tempdesc);
                        ptime.addAll(temptime);
                        pusers.addAll(tempusers);
                        ptype.addAll(temptype);
                        pfile.addAll(tempfile);
                        puserid.addAll(tempusersid);
                        pdonlot.addAll(tempdonlot);
                        adapter = new MainAdapter(true);
                        notifyListView();
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
        main_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        main_listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        if(adapter != null)
        {
            if(pid.size() > 0)
            {
                main_listview.setAdapter(adapter);
            }
        }
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc,temptime,tempusers,temptype,tempfile,tempusersid,tempdonlot;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                temptime = new ArrayList<String>();
                tempusers = new ArrayList<String>();
                temptype = new ArrayList<String>();
                tempfile = new ArrayList<String>();
                tempusersid = new ArrayList<String>();
                tempdonlot = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionPost post = new ActionPost(activity);
                post.setParam(activity.getParam(), activity.getToken());
                post.setLimit(l);
                post.setOffset(o);
                post.setMethod("home");
                post.setTag(getTag());
                post.executeList();
                if(post.getSuccess())
                {
                    isSuccess = true;
                    tempid.addAll(post.getPID());
                    temptitle.addAll(post.getPTitle());
                    tempdesc.addAll(post.getPDesc());
                    temptime.addAll(post.getPDate());
                    tempusers.addAll(post.getPUsers());
                    temptype.addAll(post.getPType());
                    tempfile.addAll(post.getPFiles());
                    tempusersid.addAll(post.getPUserid());
                    tempdonlot.addAll(post.getPDon());
                    o = post.getOffset();
                }
                else
                {
                    msg = post.getMessage();
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
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }

    private void loadDetail(int position)
    {
        Intent i = new Intent(activity, ActivityPostDetail.class);
        i.putExtra("pid",pid.get(position));
        i.putExtra("src","2");
        i.putExtra("subSrc","home");
        startActivity(i);
        activity.finish();
    }

    private void showItemOption(final String ids, final String title, final String u, final String don, final String desc)
    {
        ArrayList<String> data_id = new ArrayList<String>();
        data_id.add("1");
        data_id.add("2");
        data_id.add("3");
        ArrayList<String> data_title = new ArrayList<String>();
        data_title.add(getResources().getString(R.string.home_dialog_option_share_timeline));
        data_title.add(getResources().getString(R.string.home_dialog_option_share_socmed));
        data_title.add(getResources().getString(R.string.home_dialog_option_share_report));
        final ViewDialogList dialog = new ViewDialogList(activity);
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
                if (dialog.getListViewData().getAdapter().getItem(position) == null) {
                    Toast.makeText(activity, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                } else {
                    String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                    if (object[0].equals("3")) {
                        Intent i = new Intent(activity, ActivityReportPost.class);
                        i.putExtra("pi", ids);
                        i.putExtra("pt", title);
                        i.putExtra("u", u);
                        startActivity(i);
                        activity.finish();
                    } else if (object[0].equals("2")) {
                        share(desc, don);
                    } else {
                        Intent i = new Intent(activity, ActivityCreatePost.class);
                        i.putExtra("mode-action", "repost");
                        i.putExtra("target", ids);
                        i.putExtra("src", "1");
                        startActivity(i);
                        activity.finish();
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    public class MainAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private int mLastPosition;
        private boolean isScroll;

        public MainAdapter(boolean isScroll) {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.isScroll = isScroll;
        }

        class ViewHolder{
            ViewText textusers;
            ViewText texttitle;
            ViewText texttime;
            ViewText textdesc;
            ImageButton btn_menu;
            ImageView image_headline;
            ViewButton btn_more;
            LinearLayout mp3Player, containerItem;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            //ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.main_listview_item, null);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long milis = 0;
            try {
                Date d = sdf.parse(ptime.get(position));
                milis = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
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

            holder.containerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDetail(position);
                }
            });

            if(ptype.get(position).equals("img"))
            {
                pathImage = HelperGlobal.BASE_UPLOAD + pfile.get(position);
            }
            else {
                pathImage = "drawable://" + R.drawable.bg_timeline_transparent;
            }

            String desc = pdesc.get(position);
            if(desc.length() > 105)
            {
                desc = pdesc.get(position).substring(0,100);
            }

            holder.textusers.setRegular();
            holder.textusers.setText(pusers.get(position));
            holder.texttitle.setText(ptitle.get(position));
            holder.texttitle.setRegular();
            holder.textdesc.setText(desc);
            if(milis != 0)
            {
                holder.texttime.setText(TimeUtils.getTimeAgo(milis));
            }
            else{
                holder.texttime.setVisibility(View.GONE);
            }
            holder.btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showItemOption(pid.get(position), ptitle.get(position), puserid.get(position), pdonlot.get(position), pdesc.get(position));
                }
            });
            holder.btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDetail(position);
                }
            });
            holder.textusers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ActivityProfile.class);
                    intent.putExtra("target", puserid.get(position));
                    intent.putExtra("mode", "view");
                    intent.putExtra("src", "2");
                    startActivity(intent);
                    activity.finish();
                }
            });


            getLoader().displayImage(pathImage, holder.image_headline, getOpt(), new ImageLoadingListener() {
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


            convertView.setTag(holder);
            if(isScroll) {
                float initialTranslation = (mLastPosition <= position ? 500f : -500f);

                convertView.setTranslationY(initialTranslation);
                convertView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);

                // Keep track of the last position we loaded
                mLastPosition = position;
            }


            return convertView;
        }

        public void clear()
        {
            pid.clear();
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


    private void initMp3Player(final String source, View parent)
    {
        final ImageButton button_play = (ImageButton) parent.findViewById(R.id.view_music_player_play);
        seekbar_mp3 = (SeekBar) parent.findViewById(R.id.view_music_player_seekbar);
        ViewText text_time = (ViewText) parent.findViewById(R.id.view_music_player_time);

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

        button_play.setOnClickListener(new View.OnClickListener() {
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
                        button_play.setImageResource(R.drawable.v1_icon_home_play);
                    } else {
                        media.start();
                        button_play.setImageResource(R.drawable.v1_icon_pause);
                    }
                    stat_play = 1;

                    // set Progress bar values
                    seekbar_mp3.setProgress(0);
                    seekbar_mp3.setMax(100);

                    // Updating progress bar
                    updateProgressBar();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(activity, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(activity, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(activity, getResources().getString(R.string.base_string_error) + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
            //songTotalDurationLabel.setText(""+HelperGlobal.MP3MilliToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(HelperGlobal.MP3GetProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekbar_mp3.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler_mp3.postDelayed(this, 100);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(media != null && media.isPlaying())
        {
            media.stop();
        }
    }
}
