package com.android.mouj.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ekobudiarto on 5/18/16.
 */
public class MarkerInfo {

    String id, title;
    double lats, longs;
    int index;
    public MarkerInfo()
    {
        id = "0";
        title = "";
        lats = 0;
        longs = 0;
        index = 0;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public String getID()
    {
        return this.id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setLatitude(double lats)
    {
        this.lats = lats;
    }

    public double getLatitude()
    {
        return this.lats;
    }

    public void setLongitude(double longitude)
    {
        this.longs = longitude;
    }

    public double getLongitude()
    {
        return this.longs;
    }

    public void setIndex(int i)
    {
        this.index = i;
    }

    public int getIndex()
    {
        return this.index;
    }

    public LatLng getLongLat()
    {
        return new LatLng(lats, longs);
    }


}
