package com.android.mouj.models;

/**
 * Created by ekobudiarto on 10/21/15.
 */
public class UGroup {

    private int group_id;
    private String group_name;
    private String group_desc;

    public UGroup()
    {
        group_id = 0;
        group_name = "";
        group_desc = "";
    }

    public void setGroupID(int id){
        this.group_id = id;
    }

    public int getGroupID()
    {
        return this.group_id;
    }

    public void setGroupName(String name)
    {
        this.group_name = name;
    }

    public String getGroupName()
    {
        return this.group_name;
    }

    public void setGroupDesc(String desc){
        this.group_desc = desc;
    }

    public String getGroupDesc()
    {
        return this.group_desc;
    }
}
