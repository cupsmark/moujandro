package com.mouj.app.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mouj.app.R;

/**
 * Created by ekobudiarto on 11/26/15.
 */
public class PostUploader {
    Context mContext;
    String[] mFilename;

    public PostUploader(Context context, String[] filename)
    {
        this.mContext = context;
        this.mFilename = filename;
    }

    public void Start()
    {
        String message = mContext.getResources().getString(R.string.create_post_start_upload);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
