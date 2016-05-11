package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.models.ActionGroup;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewLoadingDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

public class ActivityGroupForm extends BaseActivity {

    ImageButton imagebutton_back;
    ViewButton button_next;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;
    ImageButton imagebutton_choose;
    CircularImageView image_avatar;
    ImageView image_cover;
    ViewEditText edittext_name, edittext_desc;
    String temp_ava = "0", ava, mode, gpid, gptitle, gpdesc, gpthumb, gpcover, tempmode = "0", temp_cover = "0", cover;
    DisplayImageOptions opt;
    ImageLoader loader;
    boolean isAvatar = true;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_form);
        setShowingMenu(false);
        first_load(savedInstanceState);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .threadPoolSize(5)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);


        opt = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loader = ImageLoader.getInstance();
        mode = getIntent().getExtras().getString("mode");
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Create Group");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {

        imagebutton_back = (ImageButton) findViewById(R.id.group_imagebutton_back);
        button_next = (ViewButton) findViewById(R.id.group_btn_next);
        imagebutton_choose = (ImageButton) findViewById(R.id.group_form_imagebutton_choose);
        image_avatar = (CircularImageView) findViewById(R.id.group_form_image_ava);
        edittext_name = (ViewEditText) findViewById(R.id.group_form_edit_name);
        edittext_desc = (ViewEditText) findViewById(R.id.group_form_edit_desc);
        image_cover = (ImageView) findViewById(R.id.group_form_imageview_cover);

        imagebutton_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
        image_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_picture();
            }
        });
        image_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_picture();
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationNext();
            }
        });
        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        fillAvatar();
        if(mode.equals("edit"))
        {
            gpid = getIntent().getExtras().getString("gpid");
            fetch();
        }

    }

    private void fetch()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            ViewLoadingDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityGroupForm.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(ActivityGroupForm.this);
                group.setGPID(gpid);
                group.setParam(getParam(), getToken());
                group.executeDetailGroup();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    gptitle = group.getGRName().get(0);
                    gpdesc = group.getGRDesc().get(0);
                    gpthumb = group.getGRThumb().get(0);
                    gpcover = group.getGRCover().get(0);
                }
                else
                {
                    msg = group.getMessage();
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
                    edittext_name.setText(gptitle);
                    edittext_desc.setText(gpdesc);
                    if(!temp_ava.equals("0"))
                    {
                        Bitmap bmp = HelperGlobal.ResizeBitmap(temp_ava, 500, 500, false);
                        image_avatar.setImageBitmap(bmp);
                    }
                    if(!temp_cover.equals("0"))
                    {
                        Bitmap bmp_cover = HelperGlobal.ResizeBitmap(temp_cover, 1024, 1024, false);
                        image_cover.setImageBitmap(bmp_cover);
                    }
                    if(temp_ava.equals("0"))
                    {
                        loader.displayImage(HelperGlobal.BASE_UPLOAD + gpthumb, image_avatar, opt);
                    }
                    if(temp_cover.equals("0"))
                    {
                        loader.displayImage(HelperGlobal.BASE_UPLOAD + gpcover, image_cover, opt);
                    }

                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityGroupForm.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void validationNext()
    {
        String name;
        name = edittext_name.getText().toString();
        if(!HelperGlobal.isValidEditText(name))
        {
            Toast.makeText(ActivityGroupForm.this, getResources().getString(R.string.group_form_error_name), Toast.LENGTH_SHORT).show();
        }
        else {
            save();
        }
    }

    private void fillAvatar()
    {
        if(!temp_ava.equals("0"))
        {
            Bitmap bmp = HelperGlobal.ResizeBitmap(temp_ava, 500,500, false);
            image_avatar.setImageBitmap(bmp);
        }
        if(!temp_cover.equals("0"))
        {
            Bitmap bmp_cover = HelperGlobal.ResizeBitmap(temp_cover, 1024, 1024, false);
            image_cover.setImageBitmap(bmp_cover);
        }
    }

    private void save()
    {
        new AsyncTask<Void,Integer, String>(){
            boolean isSuccess = false;
            String msg, name, desc, ava, cover;
            ViewLoadingDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ViewLoadingDialog(ActivityGroupForm.this);
                loading.setCancelable(false);
                loading.show();
                msg = "";
                name = edittext_name.getText().toString();
                desc = edittext_desc.getText().toString();
                ava = temp_ava;
                cover = temp_cover;
            }

            @Override
            protected String doInBackground(Void... params) {

                String[] field = new String[]{"token","param","name","desc","ava","mode","gpid","cover"};
                String[] value = new String[]{getToken(), getParam(), name, desc, ava, mode, "0", cover};
                if(!temp_ava.equals("0"))
                {
                    String[] field_upload = new String[]{"token","param_picture"};
                    String[] value_upload = new String[]{getToken(),"ava"};
                    ActionGroup groupUpload = new ActionGroup(ActivityGroupForm.this);
                    groupUpload.setPostValue(field_upload, value_upload);
                    groupUpload.uploadGroupAvatar(temp_ava);
                    if(groupUpload.getSuccess())
                    {
                        value[4] = groupUpload.getFileUploaded();
                    }
                }

                if(!temp_cover.equals("0"))
                {
                    String[] field_upload = new String[]{"token","param_picture"};
                    String[] value_upload = new String[]{getToken(),"cover"};
                    ActionGroup groupUpload = new ActionGroup(ActivityGroupForm.this);
                    groupUpload.setPostValue(field_upload, value_upload);
                    groupUpload.uploadGroupAvatar(temp_cover);
                    if(groupUpload.getSuccess())
                    {
                        value[7] = groupUpload.getFileUploaded();
                    }
                }
                if(mode.equals("edit"))
                {
                    value[6] = gpid;
                }

                ActionGroup group = new ActionGroup(ActivityGroupForm.this);
                group.setPostValue(field, value);
                group.saveGroup();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    msg = group.getMessage();
                }
                if(!group.getSuccess())
                {
                    msg = group.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(loading != null && loading.isShowing())
                {
                    loading.dismiss();
                }
                if(isSuccess)
                {
                    Intent i = new Intent(ActivityGroupForm.this, ActivityGroup.class);
                    startActivity(i);
                    finish();
                }
                Toast.makeText(ActivityGroupForm.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void show_change_picture()
    {
        final Dialog d = new Dialog(ActivityGroupForm.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_profile_edit_picture);
        d.setCancelable(true);

        ViewButton btn_avatar = (ViewButton) d.findViewById(R.id.profile_edit_dialog_avatar);
        btn_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                d.dismiss();
                isAvatar = true;
                choosePicture();
            }
        });

        ViewButton btn_cover = (ViewButton) d.findViewById(R.id.profile_edit_dialog_cover);
        btn_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
                isAvatar = false;
                choosePicture();
            }
        });
        d.show();
    }

    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("imageCaptureUri", mImageCaptureUri);
    }

    private void first_load(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            mImageCaptureUri = savedInstanceState.getParcelable("imageCaptureUri");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode != RESULT_CANCELED)
        {
            switch (requestCode) {

                case PICK_FROM_FILE:
                    mImageCaptureUri = null;
                    mImageCaptureUri = data.getData();
                    onChoosePicture();
                    break;
            }
        }
    }

    private void onChoosePicture()
    {
        String fi = getPath(ActivityGroupForm.this, mImageCaptureUri);
        Uri r = Uri.parse(fi);
        File f = new File(r.getPath().toString());
        String urls = f.getAbsolutePath();
        if(isAvatar)
        {

            temp_ava = urls;
        }
        else {
            temp_cover = urls;
        }
        fillAvatar();
    }

    private void back()
    {
        Intent i = new Intent(ActivityGroupForm.this, ActivityGroup.class);
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

    @SuppressWarnings("deprecation")
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;

        // DocumentProvider
        if (isKitKat && android.provider.DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = android.provider.DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
