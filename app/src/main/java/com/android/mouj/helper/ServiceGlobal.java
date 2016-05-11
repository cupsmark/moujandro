package com.android.mouj.helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.mouj.util.ProfileUploader;

/**
 * Created by ekobudiarto on 11/16/15.
 */
public class ServiceGlobal extends Service {

    boolean isSuccess = false;
    String msg = "";
    public static String SERVICE_TAG = "services";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras().getBoolean("is_upload"))
        {
            if(intent.getExtras().getString("state").equals("profile"))
            {
                String param = intent.getExtras().getString("param");
                String token = intent.getExtras().getString("token");
                boolean is_avatar = intent.getExtras().getBoolean("is_avatar");
                String files = intent.getExtras().getString("filename");
                ProfileUploader profile = new ProfileUploader(getApplicationContext(),param, token, is_avatar, files);
                profile.Start();
            }
            else{

            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
