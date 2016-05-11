package com.android.mouj.models;

import android.content.Context;

import com.android.mouj.R;
import com.android.mouj.helper.HelperGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 10/23/15.
 */
public class ActionSignup {

    Context mContext;
    ArrayList<String> field, value;
    boolean success;
    String uri, msg;

    public ActionSignup(Context context){
        mContext = context;
        success = false;
        this.uri = HelperGlobal.U_SIGNUP;
        field = new ArrayList<String>();
        value = new ArrayList<String>();
        msg = "";
    }

    public void setParam(ArrayList<String> field, ArrayList<String> value)
    {
        this.field = field;
        this.value = value;
    }

    public void executePost()
    {
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String url_builder = this.uri.replace(" ","%20");
                String[] val = new String[value.size()];
                String[] fie = new String[field.size()];
                val = value.toArray(val);
                fie = field.toArray(fie);
                String res = HelperGlobal.PostData(url_builder,fie, val);
                JSONObject obj = new JSONObject(res);
                if(obj.getBoolean("status") == true)
                {
                    success = true;
                    msg = obj.getString("msg");
                }
                else
                {
                    success = false;
                    msg = obj.getString("msg");
                }

            }catch (JSONException e)
            {
                success = false;
                msg = e.getMessage().toString();
            }
        }
        else{
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

}
