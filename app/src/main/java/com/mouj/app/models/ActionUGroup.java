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
 * Created by ekobudiarto on 10/21/15.
 */
public class ActionUGroup {

    Context mContext;
    int l,o, row_all, row_limit;
    boolean success;
    ArrayList<String> group_id,group_name,group_desc;
    String uri;

    public ActionUGroup(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        this.row_all = 0;
        this.row_limit = 0;
        success = false;
        this.uri = HelperGlobal.U_GROUP;
        group_id = new ArrayList<String>();
        group_name = new ArrayList<String>();
        group_desc = new ArrayList<String>();
    }

    public void setL(int l)
    {
        this.l = l;
    }

    public void setO(int o)
    {
        this.o = o;
    }

    public void execute_create()
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
                    row_limit = arr.length();
                    HelperDB db = new HelperDB(mContext);
                    for (int i = 0;i < row_limit;i++)
                    {
                        JSONObject newObj = arr.getJSONObject(i);
                        int id = Integer.parseInt(newObj.getString("i"));
                        String name = newObj.getString("t");
                        String desc = newObj.getString("d");
                        UGroup group = new UGroup();
                        group.setGroupID(id);
                        group.setGroupName(name);
                        group.setGroupDesc(desc);
                        if(!db.checkUGroup(id))
                        {
                            db.addUGroup(group);
                        }
                        else{
                            db.updateUGroup(group);
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
        List<UGroup> listsUGroup = db.getAllUGroup();
        row_limit = listsUGroup.size();
        for(UGroup u : listsUGroup){
            group_id.add(Integer.toString(u.getGroupID()));
            group_name.add(u.getGroupName());
            group_desc.add(u.getGroupDesc());
        }
        success = true;
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

    public ArrayList<String> getGroupID()
    {
        return this.group_id;
    }

    public ArrayList<String> getGroupName()
    {
        return this.group_name;
    }

    public ArrayList<String> getGroupDesc()
    {
        return this.group_desc;
    }
}
