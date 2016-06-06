package com.mouj.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.mouj.app.BaseActivity;
import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.google.android.gms.analytics.Tracker;

public class ActivityTestimoni extends BaseActivity {

    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimoni);
        setShowingMenu(false);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Testimoni");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            HelperGlobal.ExitApp(ActivityTestimoni.this, getResources().getString(R.string.base_string_exit_title));
        }
        return super.onKeyDown(keyCode, event);
    }
}
