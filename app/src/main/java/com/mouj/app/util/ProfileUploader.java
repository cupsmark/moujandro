package com.mouj.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mouj.app.R;
import com.mouj.app.helper.ServiceGlobal;
import com.mouj.app.models.ActionProfile;

/**
 * Created by ekobudiarto on 11/26/15.
 */
public class ProfileUploader {

    Context mContext;
    String mParam, mToken, mFilename, mMessage;
    boolean mIsAvatar = false;
    boolean mIsSuccess = false;

    public ProfileUploader(Context context,String param, String token, boolean isAvatar, String filename)
    {
        this.mContext = context;
        this.mParam = param;
        this.mToken = token;
        this.mIsAvatar = isAvatar;
        this.mFilename = filename;
    }

    public void Start()
    {
        String message = "";
        if(mIsAvatar) {
            message = mContext.getResources().getString(R.string.start_upload_avatar);
        }
        else {
            message = mContext.getResources().getString(R.string.start_upload_cover);
        }
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                if(mIsAvatar){
                    i = 1;
                }
                if(!mIsAvatar){
                    i = 0;
                }
                ActionProfile prof_upload = new ActionProfile(mContext);
                prof_upload.setParam(mParam, mToken);
                prof_upload.setIsAvatar(mIsAvatar);
                prof_upload.setFileDevice(mFilename);
                prof_upload.executeUpload();
                if(prof_upload.getSuccess())
                {
                    mIsSuccess = true;
                }
                else
                {
                    mIsSuccess = false;
                    mMessage = prof_upload.getMessage();
                }
                handlerProfileUpload.sendEmptyMessage(i);
            }
        }).start();
    }

    private Handler handlerProfileUpload = new Handler()
    {
        @Override
        public void handleMessage(Message msgs) {
            super.handleMessage(msgs);
            Intent i = new Intent(mContext, ServiceGlobal.class);
            i.addCategory(ServiceGlobal.SERVICE_TAG);
            mContext.stopService(i);
            if(mIsSuccess)
            {
                String message = "";
                if(msgs.what == 1) {
                    message = mContext.getResources().getString(R.string.done_upload_avatar);
                }
                else {
                    message = mContext.getResources().getString(R.string.done_upload_cover);
                }
                Toast.makeText(mContext, message,Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(mContext, mMessage,Toast.LENGTH_LONG).show();
            }
        }
    };
}
