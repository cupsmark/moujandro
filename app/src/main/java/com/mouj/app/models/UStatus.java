package com.mouj.app.models;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class UStatus {

    String status_id, status_title, status_modular;

    public UStatus()
    {
        status_id = "";
        status_title = "";
        status_modular = "";
    }

    public void setStatusID(String status_id)
    {
        this.status_id = status_id;
    }

    public String getStatusID()
    {
        return this.status_id;
    }

    public void setStatusTitle(String status_title)
    {
        this.status_title = status_title;
    }

    public String getStatusTitle()
    {
        return this.status_title;
    }

    public void setStatusModular(String status_modular)
    {
        this.status_modular = status_modular;
    }

    public String getStatusModular()
    {
        return this.status_modular;
    }
}
