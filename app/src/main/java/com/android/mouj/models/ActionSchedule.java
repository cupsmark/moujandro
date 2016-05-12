package com.android.mouj.models;

import android.content.Context;

import com.android.mouj.R;
import com.android.mouj.helper.HelperDB;
import com.android.mouj.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ekobudiarto on 11/18/15.
 */
public class ActionSchedule {

    Context mContext;
    String url, msg, token, param, sortType, singleID, filterGroup;
    String[] post_field, post_value;
    boolean isSuccess = false;
    boolean isOnlyMarking = true;
    boolean isEdit = false, isDelete = false, isAddToMe = false, isSingleParameter = false;
    int month, year, l = 0,o = 0;
    Date day;
    ArrayList<String> sch_id, sch_title, sch_desc, sch_date, sch_repeat,
            sch_location, sch_user, sch_userid, sch_repost_id, sch_repost_name, sch_repost_status;
    String notif_type = "", notif_title = "", notif_message = "", notif_ticker = "", target = "";
    ArrayList<String> date_notif;

    public ActionSchedule(Context context)
    {
        this.mContext = context;
        msg = "";
        this.month = 0;
        this.year = 0;
        this.day = new Date();
        this.url = "";
        this.l = 0;
        this.o = 0;
        this.filterGroup = "";
        sch_id = new ArrayList<String>();
        sch_title = new ArrayList<String>();
        sch_desc = new ArrayList<String>();
        sch_date = new ArrayList<String>();
        sch_location = new ArrayList<String>();
        sch_repeat = new ArrayList<String>();
        sch_user = new ArrayList<String>();
        sch_userid = new ArrayList<String>();
        singleID = "";
        sch_repost_id = new ArrayList<String>();
        sch_repost_name = new ArrayList<String>();
        sch_repost_status = new ArrayList<String>();
        date_notif = new ArrayList<String>();
    }

    public void setURL(String uri)
    {
        this.url = uri;
    }


    public void setMessage(String message)
    {
        this.msg = message;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void setDay(Date day)
    {
        this.day = day;
    }

    public void setIsDay(boolean isDay)
    {
        isOnlyMarking = isDay;
    }

    public void setSingleParameter(boolean isSingle)
    {
        this.isSingleParameter = isSingle;
    }

    public void setParameterTarget(String target)
    {
        this.target = target;
    }

    public void getSchedule()
    {

        setURL(HelperGlobal.U_LIST_SCH);
        String url_builder = this.url+"?token="+token+"&param="+param;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d = "";
        if(isOnlyMarking)
        {
            d = sdf.format(day);
            url_builder += "&d="+d+"";
        }
        if(!isOnlyMarking)
        {
            d = "";
            url_builder += "&m="+Integer.toString(month)+"&y="+Integer.toString(year);
        }


        HelperDB db = new HelperDB(mContext);
        List<USch> listSchedule = db.getAllUSch(month,year,d);
        if(listSchedule.size() > 0)
        {
            for(USch sch : listSchedule)
            {
                sch_id.add(Integer.toString(sch.getSchID()));
                sch_title.add(sch.getSchTitle());
                sch_desc.add(sch.getSchDesc());
                sch_date.add(sch.getSchDate());
                sch_repeat.add(sch.getSchRepeat());
                sch_location.add(sch.getSchLocation());
            }
            isSuccess = true;
        }
        else
        {
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
                            for(int i = 0;i < arr.length();i++)
                            {
                                JSONObject objectChild = arr.getJSONObject(i);
                                USch sch = new USch();
                                sch.setSchID(Integer.parseInt(objectChild.getString("id")));
                                sch.setSchTitle(objectChild.getString("ttl"));
                                sch.setSchDesc(objectChild.getString("des"));
                                sch.setSchDate(objectChild.getString("dst"));
                                sch.setSchRepeat(objectChild.getString("rpt"));
                                sch.setSchLocation(objectChild.getString("loc"));
                                if(db.checkUSch(Integer.parseInt(objectChild.getString("id"))))
                                {
                                    db.updateUSch(sch);
                                }
                                else
                                {
                                    db.addUSch(sch);
                                }
                                sch_id.add(objectChild.getString("id"));
                                sch_title.add(objectChild.getString("ttl"));
                                sch_desc.add(objectChild.getString("des"));
                                sch_date.add(objectChild.getString("dst"));
                                sch_repeat.add(objectChild.getString("rpt"));
                                sch_location.add(objectChild.getString("loc"));
                            }
                            isSuccess = true;
                            setMessage(object.getString("msg"));
                        }
                        else
                        {
                            isSuccess = false;
                            setMessage(object.getString("msg"));
                        }
                    }catch (JSONException ex)
                    {
                        isSuccess = false;
                        setMessage(ex.getMessage().toString());
                    }
                }
                else
                {
                    isSuccess = false;
                    setMessage(mContext.getResources().getString(R.string.main_null_json));
                }
            }
            else
            {
                isSuccess = false;
                setMessage(mContext.getResources().getString(R.string.main_no_internet));
            }
        }
    }

    public void executeSave()
    {
        setURL(HelperGlobal.U_SAVE_SCH);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"),post_field, post_value);
                if(response != null)
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                        JSONArray arr = object.getJSONArray("date_list");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            date_notif.add(objectChild.getString("date"));
                        }
                        notif_type = object.getString("n_type");
                        notif_title = object.getString("n_title");
                        notif_message = object.getString("n_message");
                        notif_ticker = object.getString("n_ticker");
                        setMessage(object.getString("msg"));
                    }
                    else
                    {
                        isSuccess = false;
                        setMessage(object.getString("msg"));
                    }
                }
                else {
                    isSuccess = false;
                    setMessage(mContext.getResources().getString(R.string.main_null_json));
                }
            }catch (JSONException ex)
            {
                isSuccess = false;
                setMessage(ex.getMessage().toString());
            }
        }
        else
        {
            isSuccess = false;
            setMessage(mContext.getResources().getString(R.string.main_no_internet));
        }
    }

    public void setSingleID(String ids)
    {
        this.singleID = ids;
    }
    public void setParam(String param, String token)
    {
        this.param = param;
        this.token = token;
    }

    public void setArrayPOST(String[] field, String[] value)
    {
        this.post_field = field;
        this.post_value = value;
    }

    public void setL(int l)
    {
        this.l = l;
    }

    public void setO(int o)
    {
        this.o = o;
    }

    public void setSortType(String sortType)
    {
        this.sortType = sortType;
    }

    public void setFilterGroup(String filterGroup)
    {
        this.filterGroup =  filterGroup;
    }

    public void executeScheduleTimeline()
    {
        setURL(HelperGlobal.U_SCH_TIMELINE);
        String url_builder = this.url + "?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&sorttype="+sortType+"&filter="+filterGroup;
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
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            sch_id.add(objectChild.getString("id"));
                            sch_title.add(objectChild.getString("ttl"));
                            sch_desc.add(objectChild.getString("des"));
                            sch_date.add(objectChild.getString("dst"));
                            sch_repeat.add(objectChild.getString("rpt"));
                            sch_location.add(objectChild.getString("loc"));
                            sch_user.add(objectChild.getString("u"));
                            sch_userid.add(objectChild.getString("ut"));
                            sch_repost_status.add(objectChild.getString("repost"));
                            sch_repost_id.add(objectChild.getString("repost_from_id"));
                            sch_repost_name.add(objectChild.getString("repost_from_name"));
                        }
                        isSuccess = true;
                        o = l + o;
                    }
                    else
                    {
                        isSuccess = false;
                        msg = object.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    msg = ex.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeScheduleSingleUsers()
    {
        setURL(HelperGlobal.U_LIST_SCH);
        String url_builder = this.url + "?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&users="+target;
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
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            sch_id.add(objectChild.getString("id"));
                            sch_title.add(objectChild.getString("ttl"));
                            sch_desc.add(objectChild.getString("des"));
                            sch_date.add(objectChild.getString("dst"));
                            sch_repeat.add(objectChild.getString("rpt"));
                            sch_location.add(objectChild.getString("loc"));
                            sch_user.add(objectChild.getString("u"));
                            sch_userid.add(objectChild.getString("ut"));
                            sch_repost_status.add(objectChild.getString("repost"));
                            sch_repost_id.add(objectChild.getString("repost_from_id"));
                            sch_repost_name.add(objectChild.getString("repost_from_name"));
                        }
                        isSuccess = true;
                        o = l + o;
                    }
                    else
                    {
                        isSuccess = false;
                        msg = object.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    msg = ex.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeScheduleDetail()
    {
        setURL(HelperGlobal.U_SCH_DET);
        String url_builder = this.url + "?token="+token+"&param="+param+"&target="+singleID;
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
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            sch_id.add(objectChild.getString("id"));
                            sch_title.add(objectChild.getString("ttl"));
                            sch_desc.add(objectChild.getString("des"));
                            sch_date.add(objectChild.getString("dst"));
                            sch_repeat.add(objectChild.getString("rpt"));
                            sch_location.add(objectChild.getString("loc"));
                            sch_user.add(objectChild.getString("u"));
                            sch_userid.add(objectChild.getString("ut"));
                            sch_repost_status.add(objectChild.getString("repost"));
                            sch_repost_id.add(objectChild.getString("repost_from_id"));
                            sch_repost_name.add(objectChild.getString("repost_from_name"));
                        }
                        isEdit = object.getBoolean("ed");
                        isDelete = object.getBoolean("del");
                        isAddToMe = object.getBoolean("addtome");
                        isSuccess = true;
                        o = l + o;
                    }
                    else
                    {
                        isSuccess = false;
                        msg = object.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    msg = ex.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeDelete()
    {
        setURL(HelperGlobal.U_SCH_DEL);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"),post_field, post_value);
                if(response != null)
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                        setMessage(object.getString("msg"));
                    }
                    else
                    {
                        isSuccess = false;
                        setMessage(object.getString("msg"));
                    }
                }
                else {
                    isSuccess = false;
                    setMessage(mContext.getResources().getString(R.string.main_null_json));
                }
            }catch (JSONException ex)
            {
                isSuccess = false;
                setMessage(ex.getMessage().toString());
            }
        }
        else
        {
            isSuccess = false;
            setMessage(mContext.getResources().getString(R.string.main_no_internet));
        }
    }


    public void executeRepost()
    {
        setURL(HelperGlobal.U_SCH_REPOST);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"),post_field, post_value);
                if(response != null)
                {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                        JSONArray arr = object.getJSONArray("date_list");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            date_notif.add(objectChild.getString("date"));
                        }
                        notif_type = object.getString("n_type");
                        notif_title = object.getString("n_title");
                        notif_message = object.getString("n_message");
                        notif_ticker = object.getString("n_ticker");
                        setMessage(object.getString("msg"));
                    }
                    else
                    {
                        isSuccess = false;
                        setMessage(object.getString("msg"));
                    }
                }
                else {
                    isSuccess = false;
                    setMessage(mContext.getResources().getString(R.string.main_null_json));
                }
            }catch (JSONException ex)
            {
                isSuccess = false;
                setMessage(ex.getMessage().toString());
            }
        }
        else
        {
            isSuccess = false;
            setMessage(mContext.getResources().getString(R.string.main_no_internet));
        }
    }


    public String getMessage()
    {
        return this.msg;
    }

    public boolean getSuccess()
    {
        return this.isSuccess;
    }

    public boolean getEdit()
    {
        return this.isEdit;
    }

    public boolean getDelete()
    {
        return this.isDelete;
    }

    public boolean getAddToMe()
    {
        return this.isAddToMe;
    }

    public int getOffset()
    {
        return this.o;
    }

    public ArrayList<String> getSchID()
    {
        return this.sch_id;
    }

    public ArrayList<String> getSchTitle()
    {
        return this.sch_title;
    }

    public ArrayList<String> getSchDesc()
    {
        return this.sch_desc;
    }

    public ArrayList<String> getSchDate()
    {
        return this.sch_date;
    }

    public ArrayList<String> getSchRepeat()
    {
        return this.sch_repeat;
    }

    public ArrayList<String> getSchLocation()
    {
        return this.sch_location;
    }

    public ArrayList<String> getSchUser()
    {
        return this.sch_user;
    }

    public ArrayList<String> getSchUserID()
    {
        return this.sch_userid;
    }

    public ArrayList<String> getRepostStatus()
    {
        return this.sch_repost_status;
    }

    public ArrayList<String> getRepostFromID()
    {
        return this.sch_repost_id;
    }

    public ArrayList<String> getRepostFromName()
    {
        return this.sch_repost_name;
    }

    public String getNType()
    {
        return this.notif_type;
    }

    public String getNTitle()
    {
        return this.notif_title;
    }

    public String getNMessage()
    {
        return this.notif_message;
    }
    public String getNTicker()
    {
        return this.notif_ticker;
    }

    public ArrayList<String> getNDateList()
    {
        return this.date_notif;
    }
}
