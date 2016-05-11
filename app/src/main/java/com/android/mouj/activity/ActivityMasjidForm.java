package com.android.mouj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.MainActivity;
import com.android.mouj.R;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionPost;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;
import com.google.android.gms.analytics.Tracker;

public class ActivityMasjidForm extends BaseActivity {

    ImageButton btn_back;
    ViewButton btn_post;
    ViewEditText editText_name,editText_email,editText_phone;
    ViewText text_name,text_email,text_phone;
    String longs, lats;
    ViewLoadingDialog dialog;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid_form);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Form Tambah Masjid");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        longs = getIntent().getExtras().getString("long");
        lats = getIntent().getExtras().getString("lat");
        btn_back = (ImageButton) findViewById(R.id.masjid_form_imagebutton_back);
        btn_post = (ViewButton) findViewById(R.id.masjid_form_btn_post);
        editText_name = (ViewEditText) findViewById(R.id.masjid_form_edittext_fullname);
        editText_email = (ViewEditText) findViewById(R.id.masjid_form_edittext_email);
        editText_phone = (ViewEditText) findViewById(R.id.masjid_form_edittext_phone);
        text_name = (ViewText) findViewById(R.id.masjid_form_text_fullname);
        text_email = (ViewText) findViewById(R.id.masjid_form_text_email);
        text_phone = (ViewText) findViewById(R.id.masjid_form_text_phone);

        btn_post.setBold();
        text_name.setRegular();
        text_email.setRegular();
        text_phone.setRegular();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    private void send()
    {
        new AsyncTask<Void, Integer, String>(){
            boolean isSuccess = false;
            String msg, value_name, value_email, value_phone;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityMasjidForm.this);
                dialog.setCancelable(false);
                dialog.show();

                value_name = editText_name.getText().toString();
                value_email = editText_email.getText().toString();
                value_phone = editText_phone.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {
                if(!value_email.equals("") && !HelperGlobal.isValidEmail(value_email))
                {
                    isSuccess = false;
                    msg = getResources().getString(R.string.base_string_invalid_email);
                }
                else {
                    String[] field = new String[]{"token","param","mfu","mem","mph","long","lat"};
                    String[] value = new String[]{getToken(),getParam(),value_name,value_email,value_phone,longs,lats};
                    ActionPost post = new ActionPost(ActivityMasjidForm.this);
                    post.setArrayPOST(field,value);
                    post.executeAddMasjid();
                    if(post.getSuccess())
                    {
                        isSuccess = true;
                        msg = post.getMessage();
                    }
                    else {
                        isSuccess = false;
                        msg = post.getMessage();
                    }
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                if(isSuccess)
                {
                    Toast.makeText(ActivityMasjidForm.this, msg, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ActivityMasjidForm.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityMasjidForm.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void back()
    {
        Intent i = new Intent(ActivityMasjidForm.this, ActivityMaps.class);
        i.putExtra("step-done", true);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }
}
