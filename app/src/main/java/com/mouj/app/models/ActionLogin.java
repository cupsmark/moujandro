package com.mouj.app.models;

import android.content.Context;

import com.mouj.app.R;
import com.mouj.app.helper.HelperDB;
import com.mouj.app.helper.HelperGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 10/26/15.
 */
public class ActionLogin {

    Context mContext;
    private ArrayList<String> field, value;
    boolean success;
    int row_limit, row_all;
    String uri,msg, paramLogout;
    ArrayList<String> param1;
    String[] post_field, post_value;

    public ActionLogin(Context context)
    {
        this.mContext = context;
        field = new ArrayList<String>();
        value = new ArrayList<String>();
        param1 = new ArrayList<String>();
        success = false;
        row_limit = 0;
        row_all = 0;
        this.uri = HelperGlobal.U_LOGIN;
        msg = "";
    }

    public void setParameter(ArrayList<String> mField, ArrayList<String> mValue)
    {
        this.field = mField;
        this.value = mValue;
    }

    public void setPost(String[] field, String[] value)
    {
        this.post_field = field;
        this.post_value = value;
    }

    public void executePost()
    {
        if(HelperGlobal.checkConnection(mContext))
        {
            String url_builder = this.uri.replace(" ","%20");
            String[] mField = new String[field.size()];
            String[] mValue = new String[value.size()];
            mField = field.toArray(mField);
            mValue = value.toArray(mValue);
            String res = HelperGlobal.PostData(url_builder,mField, mValue);
            if(res != null)
            {
                try{
                    JSONObject obj = new JSONObject(res);
                    if(obj.getBoolean("status") == true)
                    {
                        success = true;
                        msg = obj.getString("msg");
                        JSONObject objDetail = obj.getJSONObject("data");
                        HelperDB db = new HelperDB(mContext);
                        UUs us = new UUs();
                        us.setUUsID(Integer.parseInt(objDetail.getString("i")));
                        us.setUUsName(objDetail.getString("n"));
                        us.setUUsFull(objDetail.getString("fu"));
                        us.setUUsDesc(objDetail.getString("d"));
                        us.setUUsGroup(Integer.parseInt(objDetail.getString("ugid")));
                        us.setUUsToken(objDetail.getString("token"));
                        us.setUUsAva(objDetail.getString("ava"));
                        us.setUUsIsIn("1");
                        us.setUUsGR(objDetail.getString("hasgr"));
                        us.setUUsStep(objDetail.getString("step"));
                        if(!db.checkUUs(Integer.parseInt(objDetail.getString("i")))){
                            db.addUUs(us);
                        }
                        else
                        {
                            db.updateUUs(us);
                        }
                        param1.add(objDetail.getString("i"));
                        param1.add(objDetail.getString("token"));
                        param1.add(objDetail.getString("ava"));
                        param1.add(objDetail.getString("step"));
                        param1.add(objDetail.getString("ugid"));
                        param1.add(objDetail.getString("long"));
                        param1.add(objDetail.getString("lat"));
                        param1.add(objDetail.getString("hasgr"));
                    }
                    else{
                        success = false;
                        msg = obj.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    success = false;
                    msg = ex.getMessage().toString();
                    ex.printStackTrace();
                }
            }
            else
            {
                success = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else{
            success = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void setParamLogout(String param)
    {
        this.paramLogout = param;
    }
    public void logout()
    {
        HelperDB db = new HelperDB(mContext);
        UUs us = new UUs();
        us.setUUsIsIn("0");
        db.updateUUsIsIn(us, Integer.parseInt(paramLogout));
        db.close();
    }


    public void executeReset()
    {
        this.uri = HelperGlobal.U_RESET;
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
                        success = true;
                    }
                    else
                    {
                        success = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    success = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                success = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
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

    public String getMessage()
    {
        return this.msg;
    }

    public ArrayList<String> getParam()
    {
        return this.param1;
    }
}
