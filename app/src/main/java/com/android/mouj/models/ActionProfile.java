package com.android.mouj.models;

import android.content.Context;

import com.android.mouj.R;
import com.android.mouj.helper.HelperGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 11/5/15.
 */
public class ActionProfile {

    Context mContext;
    int l,o, row_all, row_limit;
    boolean success;
    String uri, token,param, msg, mode_view, target;
    String file_avatar_server, file_avatar_local;
    ArrayList<String> values;
    boolean isAva = true;
    String[] post_field, post_values;

    public ActionProfile(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        this.row_all = 0;
        this.row_limit = 0;
        this.success = false;
        this.uri = HelperGlobal.U_PROFILE;
        this.msg = "";
        this.mode_view = "";
        this.target = "";
        values = new ArrayList<String>();
    }

    public void setParam(String param, String token)
    {
        this.param = param;
        this.token = token;
    }

    public void setIsAvatar(boolean isAva)
    {
        this.isAva = isAva;
    }

    public void setFileDevice(String filename)
    {
        this.file_avatar_local = filename;
    }

    public void setURI(String uri)
    {
        this.uri = uri;
    }

    public void setArrayPOST(String[] field, String[] values)
    {
        this.post_field = field;
        this.post_values = values;
    }

    public void setModeView(String mode)
    {
        this.mode_view = mode;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }


    public void executeListProfile()
    {
        String url_builder = this.uri + "?token="+token+"&uid="+param;
        if(mode_view.equals("view"))
        {
            url_builder += "&target="+target+"&mode=view";
        }
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.GetJSON(url_builder.replace(" ","%20"));
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        JSONObject newObj = obj.getJSONObject("data");
                        values.add(0, newObj.getString("i"));
                        values.add(1, newObj.getString("ids"));
                        values.add(2, newObj.getString("u"));
                        values.add(3, newObj.getString("fu"));
                        values.add(4, newObj.getString("e"));
                        values.add(5, newObj.getString("ph"));
                        values.add(6, newObj.getString("d"));
                        values.add(7, newObj.getString("ava"));
                        values.add(8, newObj.getString("long"));
                        values.add(9, newObj.getString("lat"));
                        values.add(10, newObj.getString("cover"));
                        values.add(11, newObj.getString("flwaccess"));
                        values.add(12, newObj.getString("loc"));
                        values.add(13, newObj.getString("ugid"));
                        success = true;
                    }
                    else
                    {
                        success = false;
                    }
                    msg = obj.getString("msg");
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

    public void executeUpload()
    {
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String[] field = new String[]{"param_picture","param","token"};
                String[] value;
                if(isAva)
                {
                    value = new String[]{"ava",param, token};
                }
                else
                {
                    value = new String[]{"cover",param, token};
                }
                String response = HelperGlobal.Upload(HelperGlobal.U_UPLOAD, file_avatar_local, field, value);
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        file_avatar_server = obj.getString("f");
                        success = true;
                    }
                    else
                    {
                        success = false;
                    }
                }
                else
                {
                    success = false;
                }
            }catch (JSONException e){
                success = false;
            }
        }
        else
        {
            success = false;
        }
    }

    public void executeSave()
    {
        String url_builder = this.uri + "?token="+token+"&uid="+param;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ", "%20"), post_field, post_values);
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        success = true;
                        msg = obj.getString("msg");
                    }
                    else
                    {
                        success = false;
                    }
                    msg = obj.getString("msg");
                }
                else
                {
                    success = false;
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

    public void executeFollow()
    {
        this.uri = HelperGlobal.U_PROFILE_FOLLOW;
        String url_builder = this.uri;
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.PostData(url_builder.replace(" ","%20"),post_field, post_values);
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("status"))
                    {
                        success = true;
                        msg = obj.getString("msg");
                    }
                    else
                    {
                        success = false;
                    }
                    msg = obj.getString("msg");
                }
                else
                {
                    success = false;
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

    public String getFileImage()
    {
        return this.file_avatar_server;
    }

    public String getMessage()
    {
        return this.msg;
    }
    public ArrayList<String> getValues()
    {
        return this.values;
    }
}
