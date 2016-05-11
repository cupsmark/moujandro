package com.android.mouj.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.android.mouj.R;
import com.android.mouj.activity.ActivityNotificationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ekobudiarto on 3/17/16.
 */
public class ServiceNotification extends Service {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            startNotif(intent, intent.getExtras().getString("type"));
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startNotif(Intent intent, String type)
    {

        String title = intent.getExtras().getString("title");
        String msg = intent.getExtras().getString("message");
        String ticker = intent.getExtras().getString("ticker");
        String token = intent.getExtras().getString("token");
        String param = intent.getExtras().getString("param");
        if(!token.equals("") && !param.equals(""))
        {
            Intent mIntent = new Intent(this, ActivityNotificationService.class);
            mIntent.putExtra("token",token);
            mIntent.putExtra("param", param);
            mIntent.putExtra("type", type);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builderNotif = new NotificationCompat.Builder(this);
            builderNotif.setAutoCancel(true);

            builderNotif.setContentTitle(title);
            builderNotif.setContentText(msg);
            builderNotif.setTicker(ticker);
            builderNotif.setSmallIcon(R.mipmap.ic_launcher);
            builderNotif.setContentIntent(pendingIntent);
            notificationManager.notify(0, builderNotif.build());
        }
    }
}
