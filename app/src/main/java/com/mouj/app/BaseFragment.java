package com.mouj.app;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.mouj.app.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.mouj.app.external.nostra13.universalimageloader.core.ImageLoader;
import com.mouj.app.external.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ekobudiarto on 2/26/16.
 */
public class BaseFragment extends Fragment {

    String TAG;
    Map<String, String> parameter;
    DisplayImageOptions opt;
    ImageLoader loader;
    public BaseFragment()
    {
        opt = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loader = ImageLoader.getInstance();
    }

    public ImageLoader getLoader()
    {
        return loader;
    }

    public DisplayImageOptions getOpt()
    {
        return opt;
    }

    public void setTAGFragment(String tag)
    {
        this.TAG = tag;
    }

    public void onUpdateUI(){}

    public String getTAGFragment()
    {
        return this.TAG;
    }

    public void setParameter(Map<String, String> param)
    {
        this.parameter = param;
    }

    public Map<String, String> getParameter()
    {
        return this.parameter;
    }
}
