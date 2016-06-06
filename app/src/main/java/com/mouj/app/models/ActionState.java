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
 * Created by ekobudiarto on 12/16/15.
 */
public class ActionState {

    Context mContext;
    int l,o, row_all, row_limit;
    boolean success;
    ArrayList<String> state_id,state_name,prov_id;
    String uri;

    public ActionState(Context context)
    {
        this.mContext = context;
        this.l = 0;
        this.o = 0;
        this.row_all = 0;
        this.row_limit = 0;
        success = false;
        this.uri = HelperGlobal.U_STATE;
        state_id = new ArrayList<String>();
        state_name = new ArrayList<String>();
        prov_id = new ArrayList<String>();
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
                        String prov = newObj.getString("p");
                        UState state = new UState();
                        state.setStateID(id);
                        state.setStateName(name);
                        state.setProvID(prov);
                        if(!db.checkUState(id))
                        {
                            db.addUState(state);
                        }
                        else{
                            db.updateUState(state);
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
        List<UState> listsUState = db.getAllUState();
        row_limit = listsUState.size();
        for(UState u : listsUState){
            state_id.add(Integer.toString(u.getStateID()));
            state_name.add(u.getStateName());
            prov_id.add(u.getProvID());
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

    public ArrayList<String> getStateID()
    {
        return this.state_id;
    }

    public ArrayList<String> getStateName()
    {
        return this.state_name;
    }

    public ArrayList<String> getProvID()
    {
        return this.prov_id;
    }
}
