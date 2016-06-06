package com.mouj.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionLogin;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivityLogin extends Activity {

    ViewEditText u, p;
    Tracker tracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        setupComponent();
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Login");
    }

    private void setupComponent()
    {
        u = (ViewEditText) findViewById(R.id.login_editfield_username);
        u.setRegular();
        p = (ViewEditText) findViewById(R.id.login_editfield_password);
        p.setRegular();
        ImageButton button_forgot = (ImageButton) findViewById(R.id.login_imagebutton_forgot);
        ViewButton button_sign = (ViewButton) findViewById(R.id.login_button_sign);
        button_sign.setRegular();
        ViewText text_no_account = (ViewText) findViewById(R.id.login_caption_no_account);
        text_no_account.setRegular();
        ViewText button_signup = (ViewText) findViewById(R.id.login_button_signup);
        button_signup.setSemiBold();

        LinearLayout btn_gplus = (LinearLayout) findViewById(R.id.login_linear_button_gplus);
        btn_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLoginGplus();
            }
        });

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref_to_signup();
            }
        });

        button_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action_login();
            }
        });

        button_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResetPassword();
            }
        });

        p.setImeActionLabel("LOGIN", KeyEvent.KEYCODE_ENTER);
        p.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER))
                    {
                        action_login();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void ref_to_signup()
    {
        Intent i = new Intent(ActivityLogin.this, ActivitySignup.class);
        startActivity(i);
        finish();
    }
    private void action_login()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            String us, pwd, message;
            String param,token, ava, step, ugid, longs, lat, hasgr;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityLogin.this);
                dialog.setCancelable(false);
                dialog.show();

                us = u.getText().toString();
                pwd = p.getText().toString();
                message = "";
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                if(HelperGlobal.checkConnection(ActivityLogin.this))
                {
                    if(!HelperGlobal.isValidEditText(us) && !HelperGlobal.isValidEditText(pwd))
                    {
                        message = "Username and Password " + getResources().getString(R.string.v1_signup_validation_empty);
                        result = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(us) && HelperGlobal.isValidEditText(pwd))
                    {
                        message = "Username " + getResources().getString(R.string.v1_signup_validation_empty);
                        result = "0";
                    }
                    else if(HelperGlobal.isValidEditText(us) && !HelperGlobal.isValidEditText(pwd))
                    {
                        message = "Password " + getResources().getString(R.string.v1_signup_validation_empty);
                        result = "0";
                    }
                    else
                    {
                        ArrayList<String> field = new ArrayList<String>();
                        field.add("token");
                        field.add("u");
                        field.add("pwd");
                        ArrayList<String> value = new ArrayList<String>();
                        value.add(HelperGlobal.GetDeviceID(ActivityLogin.this));
                        value.add(us);
                        value.add(pwd);
                        ActionLogin login = new ActionLogin(ActivityLogin.this);
                        login.setParameter(field,value);
                        login.executePost();
                        if(login.getSuccess() == true)
                        {
                            result = "1";
                            param = login.getParam().get(0);
                            token = login.getParam().get(1);
                            ava = login.getParam().get(2);
                            step = login.getParam().get(3);
                            ugid = login.getParam().get(4);
                            longs = login.getParam().get(5);
                            lat = login.getParam().get(6);
                            hasgr = login.getParam().get(7);
                        }
                        else {
                            result = "0";
                        }
                        message = login.getMessage();
                    }

                }
                else
                {
                    result = "0";
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(s.equals("1"))
                {
                    ref_to_main(param, token, ava, step, ugid, longs, lat, hasgr);
                }
                Toast.makeText(ActivityLogin.this, message, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void ref_to_main(String param, String token, String ava, String step, String ugid, String longs, String lat, String hasgr)
    {
        SharedPreferences pref = getSharedPreferences("var", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uid",param);
        editor.putString("token",token);
        editor.putString("ava",ava);
        editor.putString("step",step);
        editor.putString("ugid",ugid);
        editor.putString("long",longs);
        editor.putString("lat",lat);
        editor.putString("hasgr", hasgr);
        editor.commit();

        Intent i = null;
        if(ugid.equals("6") || ugid.equals("5") || ugid.equals("4"))
        {
            if(step.equals("1"))
            {
                i = new Intent(ActivityLogin.this, ActivityMaps.class);
                i.putExtra("step-done",false);
            }
            else if(step.equals("2"))
            {
                i = new Intent(ActivityLogin.this, ActivityStepFollowing.class);
                i.putExtra("long",longs);
                i.putExtra("lat",lat);
            }
            else
            {
                i = new Intent(ActivityLogin.this, MainActivity.class);
            }
        }
        else{
            if(step.equals("2"))
            {
                i = new Intent(ActivityLogin.this, ActivityStepFollowing.class);
                i.putExtra("long",longs);
                i.putExtra("lat",lat);
            }
            else{
                i = new Intent(ActivityLogin.this, MainActivity.class);
            }
        }
        startActivity(i);
        finish();
    }

    private void dialogResetPassword()
    {
        final Dialog d = new Dialog(ActivityLogin.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.base_dialog_edittext);
        d.setCancelable(true);


        ViewButton button_ok = (ViewButton) d.findViewById(R.id.base_dialog_ok);
        ViewButton button_cancel = (ViewButton) d.findViewById(R.id.base_dialog_cancel);
        ViewText textview_title = (ViewText) d.findViewById(R.id.base_dialog_title);
        textview_title.setText("Reset Password");
        final ViewEditText editText = (ViewEditText) d.findViewById(R.id.base_dialog_message);
        editText.setHint("Masukkan Email");

        textview_title.setSemiBold();
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = editText.getText().toString();
                if(!HelperGlobal.isValidEmail(value))
                {
                    Toast.makeText(ActivityLogin.this, getResources().getString(R.string.base_string_invalid_email), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    d.dismiss();
                    doResetPassword(value);
                }

            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void doResetPassword(String em)
    {
        new AsyncTask<String, Integer,String>(){

            boolean isSuccess = false;
            String msg;

            @Override
            protected String doInBackground(String... params) {
                String[] field = new String[]{"em"};
                String[] value = new String[]{params[0]};
                ActionLogin login = new ActionLogin(ActivityLogin.this);
                login.setPost(field, value);
                login.executeReset();
                if(login.getSuccess())
                {
                    isSuccess = true;
                    msg = login.getMessage();
                }
                else
                {
                    isSuccess = false;
                    msg = login.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isSuccess)
                {

                }
                Toast.makeText(ActivityLogin.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(em);
    }

    private void doLoginGplus()
    {
        Intent i = new Intent(ActivityLogin.this, Activity_GPlus.class);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            HelperGlobal.ExitApp(ActivityLogin.this,getResources().getString(R.string.base_string_exit_title));
        }
        return super.onKeyDown(keyCode, event);
    }
}
