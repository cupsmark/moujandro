package com.mouj.app.models;

import android.content.Context;

import com.mouj.app.helper.HelperDB;
import com.mouj.app.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class ActionStatus {

    Context mContext;
    int l,o;
    boolean success;
    ArrayList<String> status_id,status_title,status_modular;
    String uri, modular;

    public ActionStatus(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        modular = "0";
        success = false;
        this.uri = HelperGlobal.U_STATUS;
        status_id = new ArrayList<String>();
        status_title = new ArrayList<String>();
        status_modular = new ArrayList<String>();
    }

    public void setL(int l)
    {
        this.l = l;
    }

    public void setO(int o)
    {
        this.o = o;
    }

    public void setParameterModular(String mod)
    {
        this.modular = mod;
    }

    public void executeBind()
    {
        String url_builder = this.uri + "?token="+HelperGlobal.GetDeviceID(mContext)+"&l="+Integer.toString(l)+"&o="+Integer.toString(o);
        if(HelperGlobal.checkConnection(mContext))
        {
            try{
                String response = HelperGlobal.GetJSON(url_builder.replace(" ","%20"));
                if(response != null)
                {
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("data");
                    HelperDB db = new HelperDB(mContext);
                    for (int i = 0;i < arr.length();i++)
                    {
                        JSONObject newObj = arr.getJSONObject(i);
                        String status_id = newObj.getString("i");
                        String status_title = newObj.getString("t");
                        String status_modular = newObj.getString("m");
                        UStatus status = new UStatus();
                        status.setStatusID(status_id);
                        status.setStatusTitle(status_title);
                        status.setStatusModular(status_modular);
                        if(!db.checkStatus(status_id))
                        {
                            db.addStatus(status);
                        }
                        else{
                            db.updateStatus(status);
                        }
                    }
                    success = true;
                    this.o = this.l + this.o;
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

    public void execute_index()
    {
        HelperDB db = new HelperDB(mContext);
        List<UStatus> listsUState = db.getAllStatus();
        if(!modular.equals("0"))
        {
            listsUState = db.getStatusByModular(modular);
        }
        for(UStatus u : listsUState){
            status_id.add(u.getStatusID());
            status_title.add(u.getStatusTitle());
            status_modular.add(u.getStatusModular());
        }
        success = true;
    }

    public boolean getSuccess()
    {
        return this.success;
    }


    public ArrayList<String> getStatusID()
    {
        return this.status_id;
    }

    public ArrayList<String> getStatusTitle()
    {
        return this.status_title;
    }

    public ArrayList<String> getStatusModular()
    {
        return this.status_modular;
    }
}
