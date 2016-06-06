package com.mouj.app.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by ekobudiarto on 3/17/16.
 */
public class ReceiverNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String type = "", title = "", message = "", ticker = "", token = "", param = "";
        SharedPreferences pref;
        if(intent != null)
        {
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            {
                pref = context.getSharedPreferences("notif_var", Context.MODE_PRIVATE);
                type = pref.getString("type", "");
                title = pref.getString("title", "");
                message = pref.getString("message", "");
                ticker = pref.getString("ticker", "");
                token = pref.getString("token", "");
                param = pref.getString("param", "");
            }
            else
            {
                type = intent.getExtras().getString("type");
                title = intent.getExtras().getString("title");
                message = intent.getExtras().getString("message");
                ticker = intent.getExtras().getString("ticker");
                token = intent.getExtras().getString("token");
                param = intent.getExtras().getString("param");
            }

            Intent service = new Intent(context, ServiceNotification.class);
            service.putExtra("type", type);
            service.putExtra("title", title);
            service.putExtra("message", message);
            service.putExtra("ticker", ticker);
            service.putExtra("token", token);
            service.putExtra("param", param);
            context.startService(service);

        }
    }
}
