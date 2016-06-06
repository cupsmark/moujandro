package com.mouj.app.models;

/**
 * Created by ekobudiarto on 11/25/15.
 */
public class USch {

    private int sch_id;
    private String sch_title;
    private String sch_date;
    private String sch_desc;
    private String sch_location;
    private String sch_repeat;
    private String sch_milis;

    public USch()
    {
        sch_id = 0;
        sch_title = "";
        sch_date = "";
        sch_desc = "";
        sch_location = "";
        sch_repeat = "";
        sch_milis = "";
    }

    public void setSchID(int ids)
    {
        this.sch_id = ids;
    }

    public int getSchID()
    {
        return this.sch_id;
    }

    public void setSchTitle(String title)
    {
        this.sch_title = title;
    }

    public String getSchTitle()
    {
        return this.sch_title;
    }

    public void setSchDate(String date)
    {
        this.sch_date = date;
    }

    public String getSchDate()
    {
        return this.sch_date;
    }

    public void setSchDesc(String desc)
    {
        this.sch_desc = desc;
    }

    public String getSchDesc()
    {
        return this.sch_desc;
    }

    public void setSchLocation(String location)
    {
        this.sch_location = location;
    }

    public String getSchLocation()
    {
        return this.sch_location;
    }

    public void setSchRepeat(String repeat)
    {
        this.sch_repeat = repeat;
    }

    public String getSchRepeat()
    {
        return this.sch_repeat;
    }

    public void setSchMilis(String milis)
    {
        this.sch_milis = milis;
    }

    public String getSchMilis()
    {
        return this.sch_milis;
    }



}
