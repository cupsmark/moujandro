package com.android.mouj.models;

import android.content.Context;

import com.android.mouj.R;
import com.android.mouj.helper.HelperDB;
import com.android.mouj.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 12/17/15.
 */
public class ActionStep {

    Context mContext;
    int l,o, row_all, row_limit;
    boolean success;
    ArrayList<String> follow_id,follow_name, follow_thumb, follow_desc, follow_sum, following_sum, follow_loc;
    String uri, token, param, msg;
    String[] post_field, post_value;

    public ActionStep(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        this.row_all = 0;
        this.row_limit = 0;
        success = false;
        this.uri = HelperGlobal.U_STEP_ONE_MASJID;
        follow_id = new ArrayList<String>();
        follow_name = new ArrayList<String>();
        follow_thumb = new ArrayList<String>();
        follow_desc = new ArrayList<String>();
        follow_sum = new ArrayList<String>();
        following_sum = new ArrayList<String>();
        follow_loc = new ArrayList<String>();
    }

    public void setL(int l)
    {
        this.l = l;
    }

    public void setO(int o)
    {
        this.o = o;
    }

    public void setURL(String url)
    {
        this.uri = url;
    }

    public void setPostArray(String[] f, String[] val)
    {
        this.post_field = f;
        this.post_value = val;
    }

    public void setTokenParam(String token, String param)
    {
        this.token = token;
        this.param = param;
    }

    public void executeSendStepOne()
    {
        String url_builder = this.uri;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"), post_field, post_value);
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        success = true;
                    }
                    else {
                        success = false;
                        msg = obj.getString("msg");
                    }
                }
                else
                {
                    success = false;
                    msg = mContext.getResources().getString(R.string.main_null_json);
                }
            }catch (JSONException e){
                success = false;
                msg = e.getMessage().toString();
            }
        }
        else
        {
            success = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeListFollowing()
    {
        String url_builder = this.uri+"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o);
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ","%20"));
            if(response != null)
            {
                try{
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        JSONArray arr = object.getJSONArray("data");
                        row_limit = arr.length();
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objchild = arr.getJSONObject(i);
                            String fid = objchild.getString("i");
                            String ft = objchild.getString("t");
                            String fd = objchild.getString("d");
                            String fth = objchild.getString("f");
                            String floc = objchild.getString("loc");
                            String fcfwer = objchild.getString("cfwer");
                            String fcfwing = objchild.getString("cfwing");
                            follow_id.add(fid);
                            follow_name.add(ft);
                            follow_desc.add(fd);
                            follow_thumb.add(fth);
                            follow_sum.add(fcfwer);
                            following_sum.add(fcfwing);
                            follow_loc.add(floc);
                        }
                        success = true;
                        o = o + l;
                    }
                    else
                    {
                        success = false;
                        msg = object.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    success = false;
                    msg = ex.getMessage().toString();
                }
            }
            else
            {
                success = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else {
            success = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public void executeStepFollowing()
    {
        setURL(HelperGlobal.U_STEP_FOLLOWING_SAVE);
        String url_builder = this.uri;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"), post_field, post_value);
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        success = true;
                        HelperDB db = new HelperDB(mContext);
                        UUs us = new UUs();
                        us.setUUsStep("3");
                        db.updateUUsStep(us, Integer.parseInt(post_value[1]));
                    }
                    else {
                        success = false;
                        msg = obj.getString("msg");
                    }
                }
                else
                {
                    success = false;
                    msg = mContext.getResources().getString(R.string.main_null_json);
                }
            }catch (JSONException e){
                success = false;
                msg = e.getMessage().toString();
            }
        }
        else
        {
            success = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public boolean getSuccess()
    {
        return this.success;
    }

    public int getRowLimit()
    {
        return this.row_limit;
    }

    public int getRowAll()
    {
        return this.row_all;
    }

    public String getMessage()
    {
        return this.msg;
    }

    public int getOffset()
    {
        return this.o;
    }

    public ArrayList<String> getFollowID()
    {
        return this.follow_id;
    }

    public ArrayList<String> getFollowTitle()
    {
        return this.follow_name;
    }

    public ArrayList<String> getFollowDesc()
    {
        return this.follow_desc;
    }

    public ArrayList<String> getFollowThumb()
    {
        return this.follow_thumb;
    }

    public ArrayList<String> getFollowersCount()
    {
        return this.follow_sum;
    }

    public ArrayList<String> getFollowingCount()
    {
        return this.following_sum;
    }

    public ArrayList<String> getLocation()
    {
        return this.follow_loc;
    }
}
