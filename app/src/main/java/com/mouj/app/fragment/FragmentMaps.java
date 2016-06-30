package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.activity.ActivityMasjidForm;
import com.mouj.app.activity.ActivityStepFollowing;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.helper.EndlessScrollListener;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSearch;
import com.mouj.app.models.MarkerInfo;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewSupportMapFragment;
import com.mouj.app.view.ViewText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 6/29/16.
 */
public class FragmentMaps extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_MAPS = "tag:fragment-maps";


    ImageButton button_back;
    ViewText text_title;
    Marker marker;
    boolean stepDone = false;
    ViewButton btn_next;
    String selected_long = "0",selected_lat = "0";


    String selected_keyword = "", param, token, city = "", msg;
    ListView main_list;
    ArrayList<String> masjid_id, masjid_name, masjid_thumb, masjid_desc, masjid_long, masjid_lat;
    ArrayList<String> tempid, tempname, tempthumb, tempdesc, templong, templat;
    ArrayList<String> mname, mlong, mlat, mid;
    MasjidAdapter adapter;
    int l = 10, o = 0;
    Thread searchThread;
    ViewEditText edittext_keyword;
    RelativeLayout layout_searchbox;
    boolean firstZoom = true, loadAll = true,searchRunning = false,isSuccess = false;
    Tracker tracker;
    FragmentScheduleList scheduleList;
    Map<Marker, MarkerInfo> listMarker;
    ViewSupportMapFragment mapFragment;
    BaseFragment fragmentSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_maps, container, false);
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
            GoogleAnalyticsInit();
            listMarker = new HashMap<Marker, MarkerInfo>();
            mapFragment = (ViewSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            initilizeMap();
            init();
        }
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) activity.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Cari Masjid");
    }

    public void setExtras(boolean isStepDone)
    {
        this.stepDone = isStepDone;
    }

    public void setFragmentSource(BaseFragment src)
    {
        this.fragmentSource = src;
    }

    private void init()
    {
        scheduleList = new FragmentScheduleList();
        button_back = (ImageButton) activity.findViewById(R.id.maps_imagebutton_back);
        text_title = (ViewText) activity.findViewById(R.id.maps_pagetitle);
        btn_next = (ViewButton) activity.findViewById(R.id.maps_btn_next);
        layout_searchbox = (RelativeLayout) activity.findViewById(R.id.maps_relative_search);
        main_list = (ListView) activity.findViewById(R.id.maps_listview);

        if(stepDone)
        {

            button_back.setVisibility(View.VISIBLE);
            button_back.setImageResource(R.drawable.v1_icon_main_back);
            button_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });

            layout_searchbox.setVisibility(View.VISIBLE);
            edittext_keyword = (ViewEditText) activity.findViewById(R.id.maps_search);
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
            /*btn_next.setVisibility(View.VISIBLE);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.setLongs(selected_long);
                    activity.setLat(selected_lat);
                    Intent i  = new Intent(activity, ActivityStepFollowing.class);
                    i.putExtra("long",selected_long);
                    i.putExtra("lat",selected_lat);
                    startActivity(i);
                    activity.finish();
                }
            });*/
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
        marker = mapFragment.getMap().addMarker(new MarkerOptions()
                .position(loc)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mosque_maps)));
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
        mapFragment.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerInfo info = listMarker.get(marker);
                if (info != null) {
                    removeScheduleList();
                    initScheduleList(info.getID(), info.getTitle());
                }
                return true;
            }
        });
    }

    private void initilizeMap() {
        mapFragment.getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapFragment.getMap().setMyLocationEnabled(true);
        mapFragment.getMap().setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                selected_lat = Double.toString(location.getLatitude());
                selected_long = Double.toString(location.getLongitude());
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

                if (stepDone) {
                    ArrayList<String> addr = HelperGlobal.getAddressMaps(activity, loc.latitude, loc.longitude);
                    if (addr.size() > 4) {
                        city = addr.get(5);
                    }
                    if (firstZoom) {
                        if (marker != null) {
                            marker.remove();
                        }
                        marker = mapFragment.getMap().addMarker(new MarkerOptions()
                                .position(loc)
                                .title("Lokasi Anda"));
                        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
                        firstZoom = false;
                    }
                } else {
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mapFragment.getMap().addMarker(new MarkerOptions()
                            .position(loc)
                            .title("Lokasi Anda"));
                    mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.6f));
                }

                mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        String tempLong = Double.toString(latLng.longitude);
                        String tempLat = Double.toString(latLng.latitude);

                        if (stepDone) {
                            Intent i = new Intent(activity, ActivityMasjidForm.class);
                            i.putExtra("long", tempLong);
                            i.putExtra("lat", tempLat);
                            startActivity(i);
                            activity.finish();
                        }
                        else{
                            Map<String, String> param = new HashMap<String, String>();
                            param.put("lat", tempLat);
                            param.put("long", tempLong);
                            param.put("addr", HelperGlobal.getCompleteAddressString(activity, latLng.latitude,latLng.longitude));
                            fragmentSource.setParameter(param);
                            fragmentSource.onUpdateUI();
                            activity.onBackPressed();
                        }


                    }
                });
            }
        });
    }

    private void initScheduleList(String targetID, String targetName)
    {
        scheduleList.setTargetID(targetID);
        scheduleList.setTargetName(targetName);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        if(!scheduleList.isAdded())
        {
            ft.add(R.id.map_fragment_schedule, scheduleList);
        }
        else
        {
            ft.show(scheduleList);
            scheduleList.notifyFragment();
        }
        ft.commit();
    }

    private void removeScheduleList()
    {
        if(scheduleList != null && scheduleList.isAdded()){
            getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_bottom, R.anim.slide_in_bottom).hide(scheduleList).commit();
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
                ActionSearch search = new ActionSearch(activity);
                search.setLimit(l);
                search.setOffset(o);
                search.setParameter(activity.getToken(), activity.getParam());
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
                                    MarkerInfo markerInfo = new MarkerInfo();
                                    markerInfo.setID(mid.get(i));
                                    markerInfo.setIndex(i);
                                    markerInfo.setLatitude(Double.parseDouble(mlat.get(i)));
                                    markerInfo.setLongitude(Double.parseDouble(mlong.get(i)));
                                    markerInfo.setTitle(mname.get(i));
                                    Marker m = mapFragment.setMarker(markerInfo);
                                    listMarker.put(m, markerInfo);
                                }
                            }
                            mapFragment.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    MarkerInfo info = listMarker.get(marker);
                                    if(info != null)
                                    {
                                        removeScheduleList();
                                        initScheduleList(info.getID(), info.getTitle());
                                        //Toast.makeText(ActivityMaps.this, "Title : " + info.getTitle() +" - ID : " + info.getID(), Toast.LENGTH_SHORT).show();
                                    }
                                    return true;
                                }
                            });
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
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            final ViewHolder holder;
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


            final ViewHolder finalHolder = holder;
            Picasso.with(activity).load(HelperGlobal.BASE_UPLOAD + masjid_thumb.get(position)).into(holder.base_thumb, new Callback() {
                @Override
                public void onSuccess() {
                    holder.base_thumb.setAdjustViewBounds(true);
                    holder.base_thumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                public void onError() {

                }
            });

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
