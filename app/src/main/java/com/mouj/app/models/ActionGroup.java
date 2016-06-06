package com.mouj.app.models;

import android.content.Context;

import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 2/27/16.
 */
public class ActionGroup {

    Context mContext;
    boolean isSuccess = false;
    int l,o, num_rows;
    String[] post_field, post_value;
    String url, param, token, message, file_uploaded, gpid, keyword, mode_notif;
    ArrayList<String> foll_id, foll_name, foll_desc, foll_thumb, foll_usid;
    ArrayList<String> gr_id, gr_name, gr_thumb, gr_desc, gr_cover, gr_owner, gr_time;
    ArrayList<String> p_id, p_title, p_desc, p_date, p_thumb;
    boolean isEdit = false;
    ArrayList<Boolean> gr_edit, is_follower;

    public ActionGroup(Context context)
    {
        this.mContext = context;
        l = 0;
        o = 0;
        url = "";
        param = "";
        token = "";
        message = "";
        file_uploaded = "";
        gpid = "0";
        keyword = "";
        mode_notif = "all";
        foll_id = new ArrayList<String>();
        foll_name = new ArrayList<String>();
        foll_desc = new ArrayList<String>();
        foll_thumb = new ArrayList<String>();
        gr_id = new ArrayList<String>();
        gr_name = new ArrayList<String>();
        gr_thumb = new ArrayList<String>();
        p_id = new ArrayList<String>();
        p_title = new ArrayList<String>();
        p_desc = new ArrayList<String>();
        p_date = new ArrayList<String>();
        p_thumb = new ArrayList<String>();
        gr_desc = new ArrayList<String>();
        gr_cover = new ArrayList<String>();
        gr_edit = new ArrayList<Boolean>();
        gr_owner = new ArrayList<String>();
        gr_time = new ArrayList<String>();
        is_follower = new ArrayList<Boolean>();
        foll_usid = new ArrayList<String>();
    }

    public void setURL(String uri)
    {
        this.url = uri;
    }

    public void setParam(String param, String token)
    {
        this.param = param;
        this.token = token;
    }

    public void setPostValue(String[] field, String[] value)
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

    public void setGPID(String gpid)
    {
        this.gpid = gpid;
    }

    public void setKeyword(String k)
    {
        this.keyword = k;
    }

    public void setModeNotif(String m)
    {
        this.mode_notif = m;
    }

    public void executeListFollower()
    {
        setURL(HelperGlobal.U_GROUP_FOLLOWERS);
        String url_builder = this.url +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&gpid="+gpid+"&k="+keyword;

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
                            foll_id.add(objectChild.getString("i"));
                            foll_name.add(objectChild.getString("n"));
                            foll_desc.add(objectChild.getString("d"));
                            foll_thumb.add(objectChild.getString("f"));
                            num_rows=  Integer.parseInt(objectMain.getString("num_rows"));
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

    public void executeSearchFollower()
    {
        setURL(HelperGlobal.U_GROUP_FOLLOWERS_SEARCH);
        String url_builder = this.url +"?token="+token+"&param="+param+"&k="+keyword+"&gpid="+gpid;

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
                            foll_id.add(objectChild.getString("i"));
                            foll_name.add(objectChild.getString("n"));
                            foll_desc.add(objectChild.getString("d"));
                            foll_thumb.add(objectChild.getString("f"));
                            is_follower.add(objectChild.getBoolean("ism"));
                            num_rows=  Integer.parseInt(objectMain.getString("num_rows"));
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


    public void executeListNotification()
    {
        setURL(HelperGlobal.U_GROUP_NOTIF);
        String url_builder = this.url +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&gpid="+gpid+"&mode="+mode_notif;

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
                            foll_id.add(objectChild.getString("i"));
                            foll_usid.add(objectChild.getString("usid"));
                            foll_name.add(objectChild.getString("us"));
                            foll_desc.add(objectChild.getString("txt_msg") +" " + objectChild.getString("gr"));
                            foll_thumb.add(objectChild.getString("f"));

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

    public void saveGroup()
    {
        setURL(HelperGlobal.U_GROUP_SAVE);
        String url_builder = this.url;
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

    public void addMember()
    {
        setURL(HelperGlobal.U_GROUP_ADD_MEMBER);
        String url_builder = this.url;
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

    public void delMember()
    {
        setURL(HelperGlobal.U_GROUP_DEL_MEMBER);
        String url_builder = this.url;
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

    public void delPost()
    {
        setURL(HelperGlobal.U_GROUP_DEL_POST);
        String url_builder = this.url;
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

    public void uploadGroupAvatar(String uriFile)
    {
        setURL(HelperGlobal.U_GROUP_UPLOAD);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.Upload(url_builder, uriFile, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                        file_uploaded = object.getString("f");
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

    public void executeListGroup()
    {
        setURL(HelperGlobal.U_GROUP_LIST);
        String url_builder = this.url +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o);

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
                            gr_id.add(objectChild.getString("i"));
                            gr_name.add(objectChild.getString("n"));
                            gr_thumb.add(objectChild.getString("f"));
                            gr_edit.add(objectChild.getBoolean("e"));
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

    public void executeListPost()
    {

        setURL(HelperGlobal.U_GROUP_LIST_POST);
        String url_builder = this.url +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&gpid="+gpid;
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
                            p_id.add(objectChild.getString("i"));
                            p_title.add(objectChild.getString("t"));
                            p_desc.add(objectChild.getString("d"));
                            p_date.add(objectChild.getString("da"));
                            p_thumb.add(objectChild.getString("f"));
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

    public void executeDelete()
    {
        setURL(HelperGlobal.U_GROUP_DEL);
        String url_builder = this.url;
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

    public void executeDetailGroup()
    {
        setURL(HelperGlobal.U_GROUP_DETAIL);
        String url_builder = this.url +"?token="+token+"&param="+param+"&gpid="+gpid;

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
                            gr_id.add(objectChild.getString("i"));
                            gr_name.add(objectChild.getString("n"));
                            gr_desc.add(objectChild.getString("d"));
                            gr_thumb.add(objectChild.getString("f"));
                            gr_cover.add(objectChild.getString("c"));
                            gr_owner.add(objectChild.getString("owner"));
                            gr_time.add(objectChild.getString("time"));
                            isEdit = objectChild.getBoolean("e");
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

    public void rejectRequest()
    {
        setURL(HelperGlobal.U_GROUP_REJECT_JOIN);
        String url_builder = this.url;
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

    public void postToGroup()
    {
        setURL(HelperGlobal.U_GROUP_REPOST);
        String url_builder = this.url;
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

    public ArrayList<String> getFollID()
    {
        return this.foll_id;
    }

    public ArrayList<String> getFollName()
    {
        return this.foll_name;
    }

    public ArrayList<String> getFollDesc()
    {
        return this.foll_desc;
    }

    public ArrayList<String> getFollThumb()
    {
        return this.foll_thumb;
    }

    public ArrayList<String> getFollUSID()
    {
        return this.foll_usid;
    }

    public ArrayList<String> getGRID()
    {
        return this.gr_id;
    }

    public ArrayList<String> getGRName()
    {
        return this.gr_name;
    }

    public ArrayList<String> getGRThumb()
    {
        return this.gr_thumb;
    }

    public ArrayList<String> getGRDesc()
    {
        return this.gr_desc;
    }

    public ArrayList<String> getGRCover()
    {
        return this.gr_cover;
    }

    public ArrayList<String> getGROwner()
    {
        return this.gr_owner;
    }

    public ArrayList<String> getGRTime()
    {
        return this.gr_time;
    }

    public ArrayList<Boolean> getGREdit()
    {
        return this.gr_edit;
    }

    public ArrayList<String> getPostID()
    {
        return this.p_id;
    }

    public ArrayList<String> getPostTitle()
    {
        return this.p_title;
    }

    public ArrayList<String> getPostDesc()
    {
        return this.p_desc;
    }

    public ArrayList<String> getPostDate()
    {
        return this.p_date;
    }

    public ArrayList<String> getPostThumb()
    {
        return this.p_thumb;
    }

    public ArrayList<Boolean> getIsFollowers()
    {
        return this.is_follower;
    }

    public String getFileUploaded()
    {
        return this.file_uploaded;
    }

    public int getOffset()
    {
        return this.o;
    }

    public int getNumRows()
    {
        return this.num_rows;
    }
    public boolean getSuccess()
    {
        return this.isSuccess;
    }
    public String getMessage()
    {
        return this.message;
    }
    public boolean getEdited()
    {
        return this.isEdit;
    }

}
