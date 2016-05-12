package com.android.mouj.models;

import android.content.Context;

import com.android.mouj.R;
import com.android.mouj.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 12/24/15.
 */
public class ActionSearch {

    Context mContext;
    int l,o;
    String message, token, param, uri, keyword, post_method, city = "";
    boolean isSuccess = false;
    ArrayList<String> ust_id, ust_name, ust_desc, ust_thumb;
    ArrayList<String> msjd_id, msjd_name, msjd_desc, msjd_thumb, msjd_long, msjd_lat;
    ArrayList<String> postid,posttitle,postdate,postusers;
    ArrayList<String> tag_id, tag_name, tag_count;
    ArrayList<String> notif_id, notif_users, notif_title,notif_thumb, notif_date, notif_type, notif_primary;
    ArrayList<String> group_id, group_name, group_desc, group_thumb, group_long, group_lat;
    ArrayList<String> group_join,group_join_title;
    String[] post_field, post_value;

    public ActionSearch(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        this.message = "";
        this.token = "";
        this.param = "";
        this.uri = "";
        this.keyword = "";

        ust_id = new ArrayList<String>();
        ust_name = new ArrayList<String>();
        ust_desc = new ArrayList<String>();
        ust_thumb = new ArrayList<String>();
        msjd_id = new ArrayList<String>();
        msjd_name = new ArrayList<String>();
        msjd_desc = new ArrayList<String>();
        msjd_thumb = new ArrayList<String>();
        msjd_long = new ArrayList<String>();
        msjd_lat = new ArrayList<String>();
        postid = new ArrayList<String>();
        posttitle = new ArrayList<String>();
        postdate = new ArrayList<String>();
        postusers = new ArrayList<String>();
        tag_id = new ArrayList<String>();
        tag_name = new ArrayList<String>();
        tag_count = new ArrayList<String>();

        notif_id = new ArrayList<String>();
        notif_users = new ArrayList<String>();
        notif_title = new ArrayList<String>();
        notif_thumb = new ArrayList<String>();
        notif_date = new ArrayList<String>();
        notif_type = new ArrayList<String>();
        notif_primary = new ArrayList<String>();

        group_id = new ArrayList<String>();
        group_name = new ArrayList<String>();
        group_desc = new ArrayList<String>();
        group_thumb = new ArrayList<String>();
        group_long = new ArrayList<String>();
        group_lat = new ArrayList<String>();
        group_join = new ArrayList<String>();
        group_join_title = new ArrayList<String>();
    }

    public void setParameter(String token, String param)
    {
        this.token = token;
        this.param = param;
    }

    public void setURL(String uri)
    {
        this.uri = uri;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public void setLimit(int l)
    {
        this.l = l;
    }

    public void setOffset(int o)
    {
        this.o = o;
    }

    public void setMethod(String m)
    {
        this.post_method = m;
    }

    public void setPostParameter(String[] field, String[] value)
    {
        this.post_field = field;
        this.post_value = value;
    }

    public void executeSearchPost()
    {
        setURL(HelperGlobal.U_SRCH_POST);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&keyword="+keyword;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            postid.add(objectChild.getString("i"));
                            posttitle.add(objectChild.getString("t"));
                            postdate.add(objectChild.getString("d"));
                            postusers.add(objectChild.getString("u"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeSearchUst()
    {
        setURL(HelperGlobal.U_SRCH_USER);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&keyword="+keyword+"&mode=ust";
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            ust_id.add(objectChild.getString("ids"));
                            ust_name.add(objectChild.getString("fu"));
                            ust_desc.add(objectChild.getString("d"));
                            ust_thumb.add(objectChild.getString("ava"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeSearchMsjd()
    {
        setURL(HelperGlobal.U_SRCH_USER);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&keyword="+keyword+"&mode=msjd&city="+city;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            msjd_id.add(objectChild.getString("ids"));
                            msjd_name.add(objectChild.getString("fu"));
                            msjd_desc.add(objectChild.getString("d"));
                            msjd_thumb.add(objectChild.getString("ava"));
                            msjd_long.add(objectChild.getString("long"));
                            msjd_lat.add(objectChild.getString("lat"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeSearchGroup()
    {
        setURL(HelperGlobal.U_SRCH_USER);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&keyword="+keyword+"&mode=group&city="+city;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            group_id.add(objectChild.getString("ids"));
                            group_name.add(objectChild.getString("fu"));
                            group_desc.add(objectChild.getString("d"));
                            group_thumb.add(objectChild.getString("ava"));
                            group_long.add(objectChild.getString("long"));
                            group_lat.add(objectChild.getString("lat"));
                            group_join.add(objectChild.getString("join"));
                            group_join_title.add(objectChild.getString("join_title"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeSearchTag()
    {
        setURL(HelperGlobal.U_SRCH_TAG);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&keyword="+keyword;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            tag_id.add(objectChild.getString("i"));
                            tag_name.add(objectChild.getString("t"));
                            tag_count.add(objectChild.getString("c"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public void executeNotification()
    {
        setURL(HelperGlobal.U_SRCH_NOTIF);
        String url_builder = this.uri +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o);
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            notif_id.add(objectChild.getString("i"));
                            notif_title.add(objectChild.getString("t"));
                            notif_thumb.add(objectChild.getString("f"));
                            notif_users.add(objectChild.getString("us"));
                            notif_date.add(objectChild.getString("da"));
                            notif_type.add(objectChild.getString("type"));
                            notif_primary.add(objectChild.getString("primary"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                    }
                    else {
                        isSuccess = false;
                        message = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    message = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public void executeRequestJoin()
    {
        setURL(HelperGlobal.U_GROUP_REQUEST_JOIN);
        String url_builder = this.uri;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    message = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    message = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                message = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            message = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public boolean getSuccess()
    {
        return this.isSuccess;
    }

    public String getMessage()
    {
        return this.message;
    }

    public int getOffset()
    {
        return this.o;
    }

    public ArrayList<String> getPID()
    {
        return this.postid;
    }
    public ArrayList<String> getPTitle()
    {
        return this.posttitle;
    }
    public ArrayList<String> getPDate()
    {
        return this.postdate;
    }
    public ArrayList<String> getPUsers()
    {
        return this.postusers;
    }


    public ArrayList<String> getUstID()
    {
        return this.ust_id;
    }

    public ArrayList<String> getUstName()
    {
        return this.ust_name;
    }

    public ArrayList<String> getUstDesc()
    {
        return this.ust_desc;
    }

    public ArrayList<String> getUstThumb()
    {
        return this.ust_thumb;
    }

    public ArrayList<String> getMsjdID()
    {
        return this.msjd_id;
    }

    public ArrayList<String> getMsjdName()
    {
        return this.msjd_name;
    }

    public ArrayList<String> getMsjdDesc()
    {
        return this.msjd_desc;
    }

    public ArrayList<String> getMsjdThumb()
    {
        return this.msjd_thumb;
    }

    public ArrayList<String> getMsjdLong()
    {
        return this.msjd_long;
    }

    public ArrayList<String> getMsjdLat()
    {
        return this.msjd_lat;
    }

    public ArrayList<String> getTagID()
    {
        return this.tag_id;
    }

    public ArrayList<String> getTagName()
    {
        return this.tag_name;
    }

    public ArrayList<String> getTagCount()
    {
        return this.tag_count;
    }

    public ArrayList<String> getNotifID()
    {
        return this.notif_id;
    }

    public ArrayList<String> getNotifTitle()
    {
        return this.notif_title;
    }

    public ArrayList<String> getNotifUsers()
    {
        return this.notif_users;
    }
    public ArrayList<String> getNotifThumb()
    {
        return this.notif_thumb;
    }
    public ArrayList<String> getNotifDate()
    {
        return this.notif_date;
    }
    public ArrayList<String> getNotifType()
    {
        return this.notif_type;
    }

    public ArrayList<String> getNotifPrimary()
    {
        return this.notif_primary;
    }

    public ArrayList<String> getGRID()
    {
        return this.group_id;
    }

    public ArrayList<String> getGRName()
    {
        return this.group_name;
    }

    public ArrayList<String> getGRDesc()
    {
        return this.group_desc;
    }

    public ArrayList<String> getGRThumb()
    {
        return this.group_thumb;
    }

    public ArrayList<String> getGRLong()
    {
        return this.group_long;
    }

    public ArrayList<String> getGRLat()
    {
        return this.group_lat;
    }

    public ArrayList<String> getGRJoin()
    {
        return this.group_join;
    }

    public ArrayList<String> getGRJoinTitle()
    {
        return this.group_join_title;
    }

}
