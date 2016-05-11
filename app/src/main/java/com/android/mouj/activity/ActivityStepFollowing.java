package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionStep;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ActivityStepFollowing extends BaseActivity {

    ViewButton button_finish;
    ImageButton button_back;
    ListView listusers;
    ArrayList<String> data_follow;
    ViewLoadingDialog dialog;
    int l = 10,o = 0;
    DisplayImageOptions opt;
    ImageLoader loader;
    ArrayList<String> follow_id, follow_title, follow_desc,follow_thumb, followers_count,following_count, follow_loc;
    FollowingAdapter adapter;
    ArrayList<String> selected_id;
    String longs = "0", lat = "0", city = "";
    GoogleMap maps;
    Marker marker;
    Tracker tracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_following);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Following Step");
    }

    private void init()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ActivityStepFollowing.this)
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

        longs = getIntent().getExtras().getString("long");
        lat = getIntent().getExtras().getString("lat");
        data_follow = new ArrayList<String>();
        follow_id = new ArrayList<String>();
        follow_title = new ArrayList<String>();
        follow_desc = new ArrayList<String>();
        follow_thumb = new ArrayList<String>();
        followers_count = new ArrayList<String>();
        following_count = new ArrayList<String>();
        follow_loc = new ArrayList<String>();
        selected_id = new ArrayList<String>();

        button_finish = (ViewButton) findViewById(R.id.step_following_finish);
        button_back = (ImageButton) findViewById(R.id.step_following_imagebutton_back);
        listusers = (ListView) findViewById(R.id.step_following_listview);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        button_finish.setSemiBold();
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFollow();
            }
        });
        getListFollow();
        initilizeMap();
    }

    private void getListFollow()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc, tempfollowerscount, tempthumb, tempfollowingcount, temploc;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityStepFollowing.this);
                dialog.setCancelable(false);
                dialog.show();

                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempfollowerscount = new ArrayList<String>();
                tempfollowingcount = new ArrayList<String>();
                temploc = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionStep step = new ActionStep(ActivityStepFollowing.this);
                step.setURL(HelperGlobal.U_STEP_FOLLOWING);
                step.setL(l);
                step.setO(o);
                step.setTokenParam(getToken(), getParam());
                step.executeListFollowing();
                if(step.getSuccess())
                {
                    tempid.addAll(step.getFollowID());
                    temptitle.addAll(step.getFollowTitle());
                    tempdesc.addAll(step.getFollowDesc());
                    tempthumb.addAll(step.getFollowThumb());
                    tempfollowerscount.addAll(step.getFollowersCount());
                    tempfollowingcount.addAll(step.getFollowingCount());
                    temploc.addAll(step.getLocation());
                    isSuccess = true;
                    o = step.getOffset();
                }
                else {
                    isSuccess = false;
                    msg = step.getMessage();
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
                        follow_id.addAll(tempid);
                        follow_title.addAll(temptitle);
                        follow_desc.addAll(tempdesc);
                        follow_thumb.addAll(tempthumb);
                        followers_count.addAll(tempfollowerscount);
                        following_count.addAll(tempfollowingcount);
                        follow_loc.addAll(temploc);
                        adapter = new FollowingAdapter();
                        notifyListFollow();
                    }
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityStepFollowing.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void notifyListFollow()
    {
        listusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDialogProfile(position);
            }
        });
        listusers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMore();
            }
        });
        if(adapter != null)
        {
            if(follow_id.size() > 0)
            {
                listusers.setAdapter(adapter);
            }
        }
    }

    private void loadMore()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ArrayList<String> tempid, temptitle, tempdesc, tempfollowerscount, tempthumb, tempfollowingcount, temploc;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tempid = new ArrayList<String>();
                temptitle = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                tempfollowerscount = new ArrayList<String>();
                tempfollowingcount = new ArrayList<String>();
                temploc = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionStep step = new ActionStep(ActivityStepFollowing.this);
                step.setURL(HelperGlobal.U_STEP_FOLLOWING);
                step.setL(l);
                step.setO(o);
                step.setTokenParam(getToken(), getParam());
                step.executeListFollowing();
                if(step.getSuccess())
                {
                    tempid.addAll(step.getFollowID());
                    temptitle.addAll(step.getFollowTitle());
                    tempdesc.addAll(step.getFollowDesc());
                    tempthumb.addAll(step.getFollowThumb());
                    tempfollowerscount.addAll(step.getFollowersCount());
                    tempfollowingcount.addAll(step.getFollowingCount());
                    temploc.addAll(step.getLocation());
                    isSuccess = true;
                    o = step.getOffset();
                }
                else {
                    isSuccess = false;
                    msg = step.getMessage();
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
                            follow_id.add(tempid.get(i));
                            follow_title.add(temptitle.get(i));
                            follow_desc.add(tempdesc.get(i));
                            follow_thumb.add(tempthumb.get(i));
                            followers_count.add(tempfollowerscount.get(i));
                            following_count.add(tempfollowingcount.get(i));
                            follow_loc.add(temploc.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }



    private void doFollow()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityStepFollowing.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                if(selected_id.size() > 0)
                {
                    String finalizeSelected = "";
                    for(int i = 0;i < selected_id.size();i++)
                    {
                        finalizeSelected += ","+selected_id.get(i);
                    }

                    String[] field = new String[]{"token","param","fid","long","lat", "city"};
                    String[] value = new String[]{getToken(), getParam(), finalizeSelected,longs, lat, city};
                    ActionStep step = new ActionStep(ActivityStepFollowing.this);
                    step.setPostArray(field, value);
                    step.executeStepFollowing();
                    if(step.getSuccess())
                    {
                        isSuccess = true;
                    }
                    else {
                        isSuccess = false;
                        msg = step.getMessage();
                    }
                }
                else
                {
                    isSuccess = false;
                    msg = getResources().getString(R.string.base_string_invalid_follow);
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
                    Intent i = new Intent(ActivityStepFollowing.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityStepFollowing.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void back()
    {
        HelperGlobal.ExitApp(ActivityStepFollowing.this,getResources().getString(R.string.base_string_exit_title));
        /*if(getUgid().equals("6") || getUgid().equals("5"))
        {
            Intent i = new Intent(ActivityStepFollowing.this, ActivityMaps.class);
            i.putExtra("step-done",false);
            startActivity(i);
            finish();
        }
        else {
            HelperGlobal.ExitApp(ActivityStepFollowing.this,getResources().getString(R.string.base_string_exit_title));
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDialogProfile(int position)
    {
        Dialog d = new Dialog(ActivityStepFollowing.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.step_following_dialog_profile);
        d.setCancelable(true);

        d.show();
        ViewText text_title = (ViewText) d.findViewById(R.id.step_following_dialog_title);
        ViewText text_name = (ViewText)d.findViewById(R.id.step_following_dialog_name);
        ViewText text_location = (ViewText) d.findViewById(R.id.step_following_dialog_location);
        ViewText text_value_following = (ViewText) d.findViewById(R.id.step_following_dialog_following_value);
        ViewText text_value_followers = (ViewText) d.findViewById(R.id.step_following_dialog_followers_value);
        ViewButton button_follow = (ViewButton) d.findViewById(R.id.step_following_dialog_button_follow);
        CircularImageView image_avatar = (CircularImageView) d.findViewById(R.id.step_following_dialog_avatar);

        text_title.setRegular();
        text_name.setRegular();
        text_name.setText(follow_title.get(position));
        text_location.setRegular();
        text_location.setText(follow_loc.get(position));
        text_value_followers.setRegular();
        text_value_followers.setText(followers_count.get(position));
        text_value_following.setRegular();
        text_value_following.setText(following_count.get(position));
        checkerFollow(button_follow, position);
        loader.displayImage(HelperGlobal.BASE_UPLOAD + follow_thumb.get(position), image_avatar, opt);
    }

    private void updateItemAtPosition(int position) {
        int visiblePosition = listusers.getFirstVisiblePosition();
        View view = listusers.getChildAt(position - visiblePosition);
        if(view != null)
        {
            ViewButton button_follow = (ViewButton) view.findViewById(R.id.step_following_item_button_follow);
            checkerFollow(button_follow, position);
        }
    }

    public class FollowingAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public FollowingAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            holder.base_button_follow.setVisibility(View.VISIBLE);

            holder.base_title.setRegular();
            holder.base_title.setText(follow_title.get(position));
            holder.base_desc.setText(follow_desc.get(position));
            holder.base_desc.setRegular();

            checkerFollow(holder.base_button_follow, position);

            loader.displayImage(HelperGlobal.BASE_UPLOAD + follow_thumb.get(position), holder.base_thumb, opt);

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            follow_id.clear();
            follow_title.clear();
            follow_desc.clear();
            follow_thumb.clear();
            followers_count.clear();
            following_count.clear();
            follow_loc.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return follow_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[7];
            object[0] = follow_id.get(arg0);
            object[1] = follow_title.get(arg0);
            object[2] = follow_desc.get(arg0);
            object[3] = follow_thumb.get(arg0);
            object[4] = followers_count.get(arg0);
            object[5] = following_count.get(arg0);
            object[6] = follow_loc.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    private void checkerFollow(final ViewButton button, final int id)
    {
        if(selected_id.contains(follow_id.get(id)))
        {
            button.setRegular();
            button.setText(getResources().getString(R.string.base_string_unfollow));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0;i < selected_id.size();i++)
                    {
                        selected_id.remove(id);
                    }
                    checkerFollow(button,id);
                    updateItemAtPosition(id);
                }
            });
        }
        else {
            button.setRegular();
            button.setText(getResources().getString(R.string.base_string_follow));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_id.add(follow_id.get(id));
                    checkerFollow(button, id);
                    updateItemAtPosition(id);
                }
            });
        }
    }


    private void initilizeMap() {
        if (maps == null) {
            maps = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if(maps != null)
            {


                maps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                maps.setMyLocationEnabled(true);
                maps.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
                        longs = Double.toString(loc.longitude);
                        lat = Double.toString(loc.latitude);
                        ArrayList<String> addr = HelperGlobal.getAddressMaps(ActivityStepFollowing.this, loc.latitude, loc.longitude);
                        if(addr.size() > 4)
                        {
                            city = addr.get(5);
                        }

                        maps.addMarker(new MarkerOptions()
                                .position(loc)
                                .title("Lokasi Anda")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));
                        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));

                        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                String tempLong = Double.toString(latLng.latitude);
                                String tempLat = Double.toString(latLng.longitude);
                            }
                        });
                    }
                });
            }


            // check if map is created successfully or not
            if (maps == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
