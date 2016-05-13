package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
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
import android.widget.RelativeLayout;
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
import com.android.mouj.fragment.FragmentScheduleList;
import com.android.mouj.helper.EndlessScrollListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionSearch;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ActivityMaps extends BaseActivity{

    GoogleMap maps;
    ImageButton button_back;
    ViewText text_title;
    Marker marker;
    boolean stepDone = false;
    ViewButton btn_next;
    String selected_long = "0",selected_lat = "0";


    String selected_keyword = "", param, token, city = "";
    ListView main_list;
    ArrayList<String> masjid_id, masjid_name, masjid_thumb, masjid_desc, masjid_long, masjid_lat;
    ArrayList<String> tempid, tempname, tempthumb, tempdesc, templong, templat;
    ArrayList<String> mname, mlong, mlat, mid;
    DisplayImageOptions opt;
    ImageLoader loader;
    MasjidAdapter adapter;
    int l = 10, o = 0;
    boolean isSuccess = false;
    String msg;
    boolean searchRunning = false;
    Thread searchThread;
    ViewEditText edittext_keyword;
    RelativeLayout layout_searchbox;
    boolean firstZoom = true;
    boolean loadAll = true;
    Tracker tracker;
    FragmentScheduleList scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_maps);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ActivityMaps.this)
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
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Cari Masjid");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        initilizeMap();
    }

    private void init()
    {
        stepDone = getIntent().getExtras().getBoolean("step-done");
        button_back = (ImageButton) findViewById(R.id.maps_imagebutton_back);
        text_title = (ViewText) findViewById(R.id.maps_pagetitle);
        btn_next = (ViewButton) findViewById(R.id.maps_btn_next);
        layout_searchbox = (RelativeLayout) findViewById(R.id.maps_relative_search);
        main_list = (ListView) findViewById(R.id.maps_listview);

        if(stepDone)
        {

            button_back.setVisibility(View.VISIBLE);
            button_back.setImageResource(R.drawable.v1_icon_main_back);
            button_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    back();
                    //HelperGlobal.ExitApp(ActivityMaps.this,getResources().getString(R.string.base_string_exit_title));
                }
            });

            layout_searchbox.setVisibility(View.VISIBLE);
            edittext_keyword = (ViewEditText) findViewById(R.id.maps_search);
            searchRunning = true;
            o = 0;

            masjid_id = new ArrayList<String>();
            masjid_name = new ArrayList<String>();
            masjid_thumb = new ArrayList<String>();
            masjid_desc = new ArrayList<String>();
            masjid_long = new ArrayList<String>();
            masjid_lat = new ArrayList<String>();
            adapter = new MasjidAdapter();
            main_list.setVisibility(View.VISIBLE);
            main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!masjid_lat.get(position).equals("null") && !masjid_lat.get(position).equals("0") && !masjid_long.get(position).equals("null") && !masjid_long.get(position).equals("null")) {
                        double lo = Double.parseDouble(masjid_long.get(position));
                        double la = Double.parseDouble(masjid_lat.get(position));
                        LatLng latLng = new LatLng(la, lo);
                        locatePosition(latLng, masjid_name.get(position), masjid_id.get(position));
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }

                }
            });
            main_list.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    searchRunning = true;
                    runThread();
                }
            });
            if(adapter != null)
            {
                main_list.setAdapter(adapter);
            }
            edittext_keyword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() > 2)
                    {
                        loadAll = false;
                        selected_keyword = edittext_keyword.getText().toString();
                        o = 0;
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        masjid_id = new ArrayList<String>();
                        masjid_name = new ArrayList<String>();
                        masjid_thumb = new ArrayList<String>();
                        masjid_desc = new ArrayList<String>();
                        masjid_long = new ArrayList<String>();
                        masjid_lat = new ArrayList<String>();
                        searchRunning = true;
                        runThread();
                    }
                    else
                    {
                        setEmptySelected();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            setEmptySelected();
        }
        else {
            btn_next.setVisibility(View.VISIBLE);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLongs(selected_long);
                    setLat(selected_lat);
                    Intent i  = new Intent(ActivityMaps.this, ActivityStepFollowing.class);
                    i.putExtra("long",selected_long);
                    i.putExtra("lat",selected_lat);
                    startActivity(i);
                    finish();
                }
            });
        }


    }

    private void setEmptySelected()
    {

        adapter.clear();
        adapter.notifyDataSetChanged();
        selected_keyword = "";
        loadAll = true;
        o = 0;
        l = 100;
        mname = new ArrayList<String>();
        mlong = new ArrayList<String>();
        mlat = new ArrayList<String>();
        mid = new ArrayList<String>();
        searchRunning = true;
        runThread();
    }

    private void locatePosition(LatLng loc, final String title, final String id)
    {
        if(marker != null)
        {
            marker.remove();
        }
        marker = maps.addMarker(new MarkerOptions()
                .position(loc)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                removeScheduleList();
                initScheduleList(id, title);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                        selected_lat = Double.toString(location.getLatitude());
                        selected_long = Double.toString(location.getLongitude());
                        LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());

                        if(stepDone)
                        {
                            ArrayList<String> addr = HelperGlobal.getAddressMaps(ActivityMaps.this, loc.latitude, loc.longitude);
                            if(addr.size() > 4)
                            {
                                city = addr.get(5);
                            }
                            if(firstZoom)
                            {
                                if(marker != null)
                                {
                                    marker.remove();
                                }
                                marker = maps.addMarker(new MarkerOptions()
                                        .position(loc)
                                        .title("Lokasi Anda")

                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
                                maps.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
                                firstZoom = false;
                            }
                        }
                        else {
                            if(marker != null)
                            {
                                marker.remove();
                            }
                            marker = maps.addMarker(new MarkerOptions()
                                    .position(loc)
                                    .title("Lokasi Anda")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
                            maps.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
                        }

                        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                String tempLong = Double.toString(latLng.longitude);
                                String tempLat = Double.toString(latLng.latitude);

                                if(stepDone)
                                {
                                    Intent i = new Intent(ActivityMaps.this, ActivityMasjidForm.class);
                                    i.putExtra("long",tempLong);
                                    i.putExtra("lat",tempLat);
                                    startActivity(i);
                                    finish();
                                }



                            }
                        });
                        maps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                return false;
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


    private void back()
    {
        if(stepDone)
        {
            Intent i = new Intent(ActivityMaps.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            //HelperGlobal.ExitApp(ActivityMaps.this,getResources().getString(R.string.base_string_exit_title));
            Intent i = new Intent(ActivityMaps.this,MainActivity.class);
            startActivity(i);
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

    private void initScheduleList(String targetID, String targetName)
    {
        scheduleList = new FragmentScheduleList();
        scheduleList.setTargetID(targetID);
        scheduleList.setTargetName(targetName);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        ft.replace(R.id.map_fragment_schedule, scheduleList);
        ft.commit();
    }

    private void removeScheduleList()
    {
        if(scheduleList != null && scheduleList.isAdded()){
            getSupportFragmentManager().beginTransaction().remove(scheduleList).commit();
        }
    }

    private void runThread()
    {
        searchThread = null;
        searchThread = new Thread(runnableSearch);
        searchThread.start();
    }



    private Runnable runnableSearch = new Runnable() {
        @Override
        public void run() {

            if(searchRunning)
            {
                searchRunning = false;
                tempid = new ArrayList<String>();
                tempname = new ArrayList<String>();
                tempthumb = new ArrayList<String>();
                tempdesc = new ArrayList<String>();
                templong = new ArrayList<String>();
                templat = new ArrayList<String>();
                ActionSearch search = new ActionSearch(ActivityMaps.this);
                search.setLimit(l);
                search.setOffset(o);
                search.setParameter(getToken(), getParam());
                search.setKeyword(selected_keyword);
                if(loadAll)
                {
                    search.setCity(city);
                }
                search.executeSearchMsjd();
                if(search.getSuccess())
                {
                    isSuccess = true;
                    if(search.getMsjdID().size() > 0)
                    {
                        tempid.addAll(search.getMsjdID());
                        tempname.addAll(search.getMsjdName());
                        tempdesc.addAll(search.getMsjdDesc());
                        tempthumb.addAll(search.getMsjdThumb());
                        templong.addAll(search.getMsjdLong());
                        templat.addAll(search.getMsjdLat());
                        o = search.getOffset();
                    }
                }
                searchHandler.sendEmptyMessage(0);
            }

        }
    };

    private Handler searchHandler = new Handler(){

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if(isSuccess)
            {
                if(tempid.size() > 0)
                {
                    if(!loadAll)
                    {
                        for(int i = 0;i < tempid.size();i++)
                        {
                            masjid_id.add(tempid.get(i));
                            masjid_name.add(tempname.get(i));
                            masjid_desc.add(tempdesc.get(i));
                            masjid_thumb.add(tempthumb.get(i));
                            masjid_long.add(templong.get(i));
                            masjid_lat.add(templat.get(i));
                        }

                        adapter.notifyDataSetChanged();
                    }
                    else {
                        mid.addAll(tempid);
                        mname.addAll(tempname);
                        mlong.addAll(templong);
                        mlat.addAll(templat);
                        if(mlong.size() > 0 && mlat.size() >0)
                        {
                            for(int i = 0;i < mlat.size();i++)
                            {
                                if(!mlat.get(i).equals("null") && !mlat.get(i).equals("null"))
                                {
                                    maps.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(mlat.get(i)), Double.parseDouble(mlong.get(i))))
                                            .title(mname.get(i))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
                                    final int finalI = i;
                                    maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {
                                            removeScheduleList();
                                            initScheduleList(mid.get(finalI), mname.get(finalI));
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
                isSuccess = false;
                searchRunning = true;
            }
        }
    };

    public class MasjidAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public MasjidAdapter() {
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

            holder.base_title.setRegular();
            holder.base_title.setText(masjid_name.get(position));
            holder.base_desc.setText(masjid_desc.get(position));
            holder.base_desc.setRegular();


            loader.displayImage(HelperGlobal.BASE_UPLOAD + masjid_thumb.get(position), holder.base_thumb, opt);

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            masjid_id.clear();
            masjid_name.clear();
            masjid_desc.clear();
            masjid_thumb.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return masjid_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[4];
            object[0] = masjid_id.get(arg0);
            object[1] = masjid_name.get(arg0);
            object[2] = masjid_desc.get(arg0);
            object[3] = masjid_thumb.get(arg0);
            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
