package com.mouj.app.models;

/**
 * Created by ekobudiarto on 12/16/15.
 */
public class UState {

    int state_id;
    String state_name;
    String prov_id;

    public UState()
    {
        state_id = 0;
        state_name = "";
        prov_id = "";
    }

    public void setStateID(int id)
    {
        this.state_id = id;
    }

    public int getStateID()
    {
        return this.state_id;
    }

    public void setStateName(String name)
    {
        this.state_name = name;
    }

    public String getStateName()
    {
        return this.state_name;
    }

    public void setProvID(String prov)
    {
        this.prov_id = prov;
    }

    public String getProvID()
    {
        return this.prov_id;
    }

}
