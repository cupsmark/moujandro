package com.mouj.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mouj.app.activity.ActivityGroup;
import com.mouj.app.activity.ActivityLogin;
import com.mouj.app.activity.ActivityMaps;
import com.mouj.app.activity.ActivityProfile;
import com.mouj.app.activity.ActivitySchedule;
import com.mouj.app.activity.ActivitySettings;
import com.mouj.app.external.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mouj.app.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.FailReason;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.mouj.app.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperDB;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionLogin;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mouj.app.models.UUs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseActivity extends FragmentActivity  implements FragmentInterface {


    SlidingMenu menu;
    ImageButton sidemenu_btn_close, main_imagebutton_menu;
    CircularImageView imageview_avatar;
    LinearLayout sidemenu_btn_settings, sidemenu_btn_logout;
    LinearLayout sidemenu_btn_home, sidemenu_btn_schedule, sidemenu_btn_profile, sidemenu_btn_timeline, sidemenu_add_masjid, sidemenu_btn_group, sidemenu_btn_testimoni;
    //RelativeLayout main_container;
    String token, param, ava, step, ugid, longs, lat, tag, hasgr;
    boolean all_data = false;
    DisplayImageOptions opt;
    ImageLoader loader;
    SharedPreferences pref;
    boolean isMenuShown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

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

    @Override
    protected void onStart() {
        super.onStart();
        attach_slidemenu();
    }

    private void attach_slidemenu()
    {

        pref = getSharedPreferences("var", Context.MODE_PRIVATE);
        param = pref.getString("uid", "0");
        token = pref.getString("token", "0");
        ava = pref.getString("ava","0");
        step = pref.getString("step","1");
        ugid = pref.getString("ugid","0");
        longs = pref.getString("long","0");
        lat = pref.getString("lat","0");
        tag = pref.getString("tag","");
        hasgr = pref.getString("hasgr","0");
        all_data = pref.getBoolean("all_data", false);


        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.SLIDING_WINDOW);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.main_sidemenu_shadow_width);
        menu.setShadowDrawable(R.drawable.sidemenu_shadow);
        menu.setBehindOffsetRes(R.dimen.main_sidemenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sidemenu_item);

        //main_container = (RelativeLayout) findViewById(R.id.activity_main_container);
        sidemenu_btn_close = (ImageButton) findViewById(R.id.sidemenu_imagebutton_close);
        sidemenu_btn_home = (LinearLayout) findViewById(R.id.sidemenu_button_home);
        sidemenu_btn_schedule = (LinearLayout) findViewById(R.id.sidemenu_button_schedule);
        sidemenu_btn_profile = (LinearLayout) findViewById(R.id.sidemenu_button_profile);
        sidemenu_btn_timeline = (LinearLayout) findViewById(R.id.sidemenu_button_timeline);
        sidemenu_btn_settings = (LinearLayout) findViewById(R.id.sidemenu_linear_button_settings);
        sidemenu_btn_logout = (LinearLayout) findViewById(R.id.sidemenu_linear_button_logout);
        sidemenu_add_masjid = (LinearLayout) findViewById(R.id.sidemenu_button_add_masjid);
        sidemenu_btn_group = (LinearLayout) findViewById(R.id.sidemenu_button_create_group);
        imageview_avatar = (CircularImageView) findViewById(R.id.sidemenu_avatar);
        sidemenu_btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllData(false);
                setTag("");
                MainActivity mains = new MainActivity();
                setNextActivity(mains);
            }
        });
        sidemenu_btn_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllData(true);
                setTag("");
                MainActivity mains = new MainActivity();
                setNextActivity(mains);
            }
        });
        sidemenu_btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ActivityProfile.class);
                i.putExtra("target",getParam());
                i.putExtra("mode","self");
                i.putExtra("src","1");
                startActivity(i);
                finish();
            }
        });
        sidemenu_btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySchedule schedule = new ActivitySchedule();
                setNextActivity(schedule);
            }
        });
        sidemenu_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle(true);
            }
        });
        sidemenu_btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionLogin login = new ActionLogin(BaseActivity.this);
                login.setParamLogout(getParam());
                login.logout();
                Intent i = new Intent(BaseActivity.this, ActivityLogin.class);
                startActivity(i);
                finish();
            }
        });
        sidemenu_add_masjid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ActivityMaps.class);
                i.putExtra("step-done",true);
                startActivity(i);
                finish();
            }
        });
        sidemenu_btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ActivitySettings.class);
                startActivity(i);
                finish();
            }
        });
        sidemenu_btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ActivityGroup.class);
                startActivity(i);
                finish();
            }
        });
        if(!ava.equals("0"))
        {
            boolean check_image = HelperGlobal.CheckInternalImage(BaseActivity.this, ava);
            if(check_image)
            {
                String path = HelperGlobal.GetInternalPath(BaseActivity.this, ava);
                loader.displayImage(path, imageview_avatar, opt);
            }
            else
            {
                loader.displayImage(HelperGlobal.BASE_UPLOAD + ava, imageview_avatar, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        HelperGlobal.SaveBitmapLocal(ava, BaseActivity.this, loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
        if(!isMenuShown)
        {
            menu.setSlidingEnabled(false);
        }
    }



    public String getToken()
    {
        return this.token;
    }

    public String getParam()
    {
        return this.param;
    }

    public void setStep(String step)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("step",step);
        editor.commit();
    }

    public void setUgid(String ugid)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ugid",ugid);
        editor.commit();
    }

    public void setLongs(String longs)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("long",longs);
        editor.commit();
    }

    public void setLat(String lat)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lat",lat);
        editor.commit();
    }

    public void setTag(String tag)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tag",tag);
        editor.commit();
    }

    public void setAllData(boolean allData)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("all_data", allData);
        editor.commit();
    }

    public void setShowingMenu(boolean isMenuShown)
    {
        this.isMenuShown = isMenuShown;
    }

    public String getStep()
    {
        return this.step;
    }

    public String getUgid()
    {
        return this.ugid;
    }

    public String getLongs()
    {
        return this.longs;
    }

    public String getLat()
    {
        return this.lat;
    }

    public String getTag()
    {
        return this.tag;
    }

    public String getHasGR()
    {
        return this.hasgr;
    }

    public boolean getAllData()
    {
        return this.all_data;
    }

    public ArrayList<String> getUserData()
    {
        ArrayList<String> result = new ArrayList<String>();
        HelperDB db = new HelperDB(BaseActivity.this);
        List<UUs> lists = db.getAllUUs(1);
        int countUsers = lists.size();
        if(countUsers > 0)
        {
            for(UUs us : lists)
            {
                result.add(0,"1");
                result.add(1,Integer.toString(us.getUUsID()));
                result.add(2,us.getUUsToken());
                result.add(3,us.getUUsName());
                result.add(4,us.getUUsAva());
                result.add(5,us.getUUsStep());
                result.add(6,Integer.toString(us.getUUsGroup()));
                result.add(7, us.getUUsGR());
            }
        }
        return result;
    }

    public ImageLoader getLoader()
    {
        return this.loader;
    }

    public DisplayImageOptions getOpt()
    {
        return this.opt;
    }



    public void toggleMenu(boolean animate)
    {
        menu.toggle(animate);
    }

    public String getLang()
    {
        return Locale.getDefault().toString();
    }

    private void setNextActivity(Activity activity)
    {
        Intent i = new Intent(BaseActivity.this, activity.getClass());
        i.putExtra("uid",param);
        i.putExtra("token",token);
        startActivity(i);
        finish();
    }

    @Override
    public void onNavigate(BaseFragment src, String TAG, boolean isBackStack, String BACKSTACK) {

    }

    @Override
    public void sendParameter(BaseFragment fragment) {

    }
}
