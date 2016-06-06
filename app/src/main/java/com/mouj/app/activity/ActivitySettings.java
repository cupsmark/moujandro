package com.mouj.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;

import com.mouj.app.MainActivity;
import com.mouj.app.R;

public class ActivitySettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void back()
    {
        Intent i = new Intent(ActivitySettings.this, MainActivity.class);
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
