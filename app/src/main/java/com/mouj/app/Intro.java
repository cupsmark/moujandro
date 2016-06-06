package com.mouj.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.mouj.app.R;
import com.mouj.app.activity.ActivityLogin;
import com.mouj.app.activity.ActivityMaps;
import com.mouj.app.activity.ActivityStepFollowing;
import com.mouj.app.helper.HelperDB;
import com.mouj.app.models.ActionState;
import com.mouj.app.models.ActionUGroup;
import com.mouj.app.models.UUs;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class Intro extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.v2_activity_intro);
        adjust_global_setting();
    }

    private void adjust_global_setting()
    {
        new AsyncTask<Void, Integer, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                ActionUGroup group = new ActionUGroup(Intro.this);
                group.setL(100);
                group.execute_create();
                ActionState state = new ActionState(Intro.this);
                state.setL(200);
                state.execute_create();
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Intent i;
                if(isLoggedIn().get(0).equals("1"))
                {
                    if(isLoggedIn().get(5).equals("6") || isLoggedIn().get(5).equals("5") || isLoggedIn().get(5).equals("4"))
                    {
                        if(isLoggedIn().get(4).equals("1"))
                        {
                            i = new Intent(Intro.this, ActivityMaps.class);
                            i.putExtra("step-done",false);
                        }
                        else if(isLoggedIn().get(4).equals("2"))
                        {
                            i = new Intent(Intro.this, ActivityStepFollowing.class);
                            i.putExtra("long",getLongs());
                            i.putExtra("lat",getLat());
                        }
                        else {
                            i = new Intent(Intro.this, MainActivity.class);
                        }
                    }
                    else {
                        if(isLoggedIn().get(4).equals("2"))
                        {
                            i = new Intent(Intro.this, ActivityStepFollowing.class);
                            i.putExtra("long",getLongs());
                            i.putExtra("lat",getLat());
                        }
                        else
                        {
                            i = new Intent(Intro.this, MainActivity.class);
                        }

                    }
                }
                else
                {
                    i = new Intent(Intro.this, ActivityLogin.class);
                }

                startActivity(i);
                finish();
            }
        }.execute();
    }

    private ArrayList<String> isLoggedIn()
    {
        ArrayList<String> result = new ArrayList<String>();
        HelperDB db = new HelperDB(Intro.this);
        List<UUs> lists = db.getAllUUs(1);
        int countUsers = lists.size();
        if(countUsers > 0)
        {
            for(UUs us : lists)
            {
                if(us.getUUsIsIn().equals("1"))
                {
                    result.add(0,"1");
                    result.add(1,Integer.toString(us.getUUsID()));
                    result.add(2,us.getUUsToken());
                    result.add(3,us.getUUsAva());
                    result.add(4,us.getUUsStep());
                    result.add(5,Integer.toString(us.getUUsGroup()));
                    result.add(6, us.getUUsGR());
                }
                else {
                    result.add(0,"0");
                }
            }
        }
        else {
            result.add(0,"0");
        }
        return result;
    }


}
