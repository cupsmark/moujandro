package com.mouj.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 11/23/15.
 */
public class PreferencesDefault {

    Activity activity;
    final String scheduleCode = "xml_schedule";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public PreferencesDefault(Activity activity)
    {
        this.activity = activity;
    }

    public void SaveSchedule(String[] data)
    {
        pref = activity.getSharedPreferences(scheduleCode,Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("sch_id",data[0]);
        editor.putString("sch_title",data[1]);
        editor.putString("sch_desc",data[2]);
        editor.putString("sch_date",data[3]);
        editor.putString("sch_location",data[4]);
        editor.putString("sch_repeat",data[5]);
        editor.commit();
    }

    public ArrayList<String> GetSchedule()
    {
        ArrayList<String> result = new ArrayList<String>();
        pref = activity.getSharedPreferences(scheduleCode, Context.MODE_PRIVATE);
        result.add(0, pref.getString("sch_id",""));
        result.add(1, pref.getString("sch_title",""));
        result.add(2, pref.getString("sch_desc",""));
        result.add(3, pref.getString("sch_date",""));
        result.add(4, pref.getString("sch_location",""));
        result.add(5, pref.getString("sch_repeat",""));
        return result;
    }
}
