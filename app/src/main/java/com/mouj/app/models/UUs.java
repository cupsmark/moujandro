package com.mouj.app.models;

/**
 * Created by ekobudiarto on 10/26/15.
 */
public class UUs {

    private int uus_id;
    private String uus_name;
    private String uus_full;
    private int uus_group;
    private String uus_desc;
    private String uus_tkn;
    private String uus_ava;
    private String uus_is_in;
    private String uus_step;
    private String uus_hasgr;

    public UUs()
    {
        uus_id = 0;
        uus_name = "";
        uus_full = "";
        uus_group = 0;
        uus_desc = "";
        uus_tkn= "";
        uus_ava = "";
        uus_is_in = "0";
        uus_step = "1";
        uus_hasgr = "0";
    }

    public void setUUsID(int ids)
    {
        this.uus_id = ids;
    }

    public int getUUsID(){
        return this.uus_id;
    }

    public void setUUsName(String name)
    {
        this.uus_name = name;
    }

    public String getUUsName()
    {
        return this.uus_name;
    }

    public void setUUsFull(String full)
    {
        this.uus_full = full;
    }

    public String getUUsFull(){
        return this.uus_full;
    }

    public void setUUsGroup(int group)
    {
        this.uus_group = group;
    }

    public int getUUsGroup()
    {
        return this.uus_group;
    }

    public void setUUsDesc(String desc)
    {
        this.uus_desc = desc;
    }

    public String getUUsDesc()
    {
        return this.uus_desc;
    }

    public void setUUsToken(String token)
    {
        this.uus_tkn = token;
    }

    public String getUUsToken()
    {
        return this.uus_tkn;
    }

    public void setUUsAva(String ava)
    {
        this.uus_ava = ava;
    }

    public String getUUsAva()
    {
        return this.uus_ava;
    }

    public void setUUsIsIn(String flag)
    {
        this.uus_is_in = flag;
    }

    public String getUUsIsIn()
    {
        return this.uus_is_in;
    }

    public void setUUsStep(String step)
    {
        this.uus_step = step;
    }

    public String getUUsStep()
    {
        return this.uus_step;
    }

    public void setUUsGR(String gr)
    {
        this.uus_hasgr = gr;
    }

    public String getUUsGR()
    {
        return this.uus_hasgr;
    }

}
