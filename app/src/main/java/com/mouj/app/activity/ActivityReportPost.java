package com.mouj.app.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionPost;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

public class ActivityReportPost extends BaseActivity {

    ImageButton button_back;
    ViewText textview_post_title_cap, textview_post_title_val;
    ViewEditText edittext_message;
    ViewButton button_submit;
    String pi,pt,u;
    ViewLoadingDialog dialog;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Report Post");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        pi = getIntent().getExtras().getString("pi");
        pt = getIntent().getExtras().getString("pt");
        u = getIntent().getExtras().getString("u");
        button_back = (ImageButton) findViewById(R.id.report_post_imagebutton_back);
        textview_post_title_cap = (ViewText) findViewById(R.id.report_post_textview_cap_title);
        textview_post_title_val = (ViewText) findViewById(R.id.report_post_textview_val_title);
        edittext_message = (ViewEditText) findViewById(R.id.report_post_edittext_message);
        button_submit = (ViewButton) findViewById(R.id.report_post_submit);

        textview_post_title_cap.setRegular();
        textview_post_title_val.setText(pt);
        edittext_message.setRegular();
        button_submit.setRegular();
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReport();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void doReport()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg, m;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityReportPost.this);
                dialog.setCancelable(false);
                dialog.show();

                m = edittext_message.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {

                String[] field = new String[]{"token","param","m","pi","pt"};
                String[] values = new String[]{getToken(), getParam(),m,pi,u};
                ActionPost post = new ActionPost(ActivityReportPost.this);
                post.setURL(HelperGlobal.U_POST_REPORT);
                post.setArrayPOST(field,values);
                post.executeReportPost();
                if(post.getSuccess())
                {
                    isSuccess = true;
                }
                else {
                    isSuccess = false;
                }
                msg = post.getMessage();
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
                    Toast.makeText(ActivityReportPost.this, msg, Toast.LENGTH_SHORT).show();
                    back();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityReportPost.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    private void back()
    {
        Intent i = new Intent(ActivityReportPost.this, MainActivity.class);
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
