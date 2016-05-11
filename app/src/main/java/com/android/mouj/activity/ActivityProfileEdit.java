package com.android.mouj.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.FailReason;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.android.mouj.external.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.helper.HelperGoogle;
import com.android.mouj.helper.ServiceGlobal;
import com.android.mouj.models.ActionProfile;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewSupportMapFragment;
import com.android.mouj.view.ViewText;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

public class ActivityProfileEdit extends BaseActivity {

    ImageButton button_back, button_save, button_hide_pass;
    LinearLayout linear_header;
    String u, fu, e, ph, d, ava, i, ids, longs, lat,cover, msg = "", temp_ava = "0", temp_cover = "0", extra_src, extra_target, extra_mode, loc, ugid = "0", city = "";
    ViewEditText tbl_row_fullname,
            tbl_row_phone,tbl_row_desc, tbl_row_location, tbl_row_password;
    ViewText tbl_row_username,tbl_row_email;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;
    boolean isAvatar = true;
    CircularImageView image_avatar;
    DisplayImageOptions opt;
    ImageLoader loader;
    ImageView image_cover;
    int hScreen;
    GoogleMap maps;
    Marker marker;
    String val_long="0", val_lat="0";
    boolean isZoom = true;
    RelativeLayout maps_container;
    LatLng latLng;
    ScrollView scrollViewProfileEdit;
    boolean hidePass = true;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        setShowingMenu(false);
        first_load(savedInstanceState);

        hScreen = HelperGlobal.GetDimension("h",ActivityProfileEdit.this);

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
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Edit Profil");
    }

    private void initilizeMap() {
        if (maps == null) {
            maps = ((ViewSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            ((ViewSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new ViewSupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    if(scrollViewProfileEdit != null)
                    {
                        scrollViewProfileEdit.requestDisallowInterceptTouchEvent(true);
                    }
                }
            });
            if(maps != null)
            {

                maps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                maps.setMyLocationEnabled(true);
                maps.getUiSettings().setZoomControlsEnabled(true);
                maps.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        if(isZoom)
                        {
                            maps.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Lokasi Anda")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));
                            maps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.6f));
                            isZoom = false;
                        }

                        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                String tempLong = Double.toString(latLng.longitude);
                                String tempLat = Double.toString(latLng.latitude);
                                longs = tempLong;
                                lat = tempLat;

                                if(marker != null)
                                {
                                    marker.remove();
                                }
                                marker = maps.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(fu)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));
                            }
                        });
                        maps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                return false;
                            }
                        });
                    }
                });
                if(longs != null && lat != null && !longs.equals("null") && !lat.equals("null"))
                {
                    double val_long = Double.parseDouble(longs);
                    double val_lat = Double.parseDouble(lat);
                    maps.addMarker(new MarkerOptions()
                            .position(new LatLng(val_lat, val_long))
                            .title(fu)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));
                }
            }


            // check if map is created successfully or not
            if (maps == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        load_profile_data();
    }

    private void init()
    {
        image_avatar = (CircularImageView) findViewById(R.id.profile_edit_imageview_avatar);
        image_cover = (ImageView) findViewById(R.id.profile_edit_imageview_header);
        button_back = (ImageButton) findViewById(R.id.profile_edit_imagebutton_back);
        button_save = (ImageButton) findViewById(R.id.profile_edit_imagebutton_save);
        linear_header = (LinearLayout) findViewById(R.id.profile_edit_linear_header);
        tbl_row_username = (ViewText) findViewById(R.id.profile_textview_tabdetail_value_username);
        tbl_row_email = (ViewText) findViewById(R.id.profile_textview_tabdetail_value_email);
        tbl_row_fullname = (ViewEditText) findViewById(R.id.profile_textview_tabdetail_value_fullname);
        tbl_row_phone = (ViewEditText) findViewById(R.id.profile_textview_tabdetail_value_phone);
        tbl_row_desc = (ViewEditText) findViewById(R.id.profile_textview_tabdetail_value_desc);
        tbl_row_location = (ViewEditText) findViewById(R.id.profile_textview_tabdetail_value_location);
        tbl_row_password = (ViewEditText) findViewById(R.id.profile_textview_tabdetail_value_password);
        maps_container = (RelativeLayout) findViewById(R.id.profile_edit_relative_maps_container);
        scrollViewProfileEdit = (ScrollView) findViewById(R.id.profile_scrollview_container);
        button_hide_pass = (ImageButton) findViewById(R.id.profile_edit_imagebutton_password_viewer);
        if(ugid.equals("6"))
        {
            maps_container.setVisibility(View.VISIBLE);
        }
        linear_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_picture();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_profile(ava);
            }
        });
        fill_component();
    }

    private void load_profile_data()
    {
        new AsyncTask<Void,Integer, String>(){

            ViewLoadingDialog dialog;
            ArrayList<String> values;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityProfileEdit.this);
                dialog.setCancelable(false);
                dialog.show();

                values = new ArrayList<String>();
                extra_src = getIntent().getExtras().getString("src");
                extra_target = getIntent().getExtras().getString("target");
                extra_mode = getIntent().getExtras().getString("mode");
            }

            @Override
            protected String doInBackground(Void... params) {

                String result = "";
                ActionProfile profile_list = new ActionProfile(ActivityProfileEdit.this);
                profile_list.setParam(getParam(), getToken());
                profile_list.executeListProfile();
                if(profile_list.getSuccess())
                {
                    values.addAll(profile_list.getValues());
                    result = "1";
                }
                else
                {
                    msg = profile_list.getMessage();
                    result = "0";
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                if(s.equals("1"))
                {
                    u = values.get(2).toString();
                    fu = values.get(3).toString();
                    e = values.get(4).toString();
                    ph = values.get(5).toString();
                    d = values.get(6).toString();
                    ava = values.get(7).toString();
                    i = values.get(0).toString();
                    ids = values.get(1).toString();
                    longs = values.get(8).toString();
                    lat = values.get(9).toString();
                    cover = values.get(10).toString();
                    loc = values.get(12).toString();
                    ugid = values.get(13).toString();
                    if(!temp_ava.equals("0"))
                    {
                        ava = temp_ava;
                    }
                    if(!temp_cover.equals("0"))
                    {
                        cover = temp_cover;
                    }
                }
                else
                {
                    Toast.makeText(ActivityProfileEdit.this, msg, Toast.LENGTH_LONG).show();
                }

                init();

            }
        }.execute();
    }

    private void fill_component()
    {
        tbl_row_username.setText(u);
        tbl_row_fullname.setText(fu);
        tbl_row_email.setText(e);
        tbl_row_phone.setText(ph);
        tbl_row_desc.setText(d);
        tbl_row_location.setText(loc);

        if( ava != null && !ava.equals(""))
        {
            boolean check_image = HelperGlobal.CheckInternalImage(ActivityProfileEdit.this, ava);
            if(check_image)
            {
                String path = HelperGlobal.GetInternalPath(ActivityProfileEdit.this, ava);
                loader.displayImage(path, image_avatar, opt);
            }
            else
            {
                loader.displayImage(HelperGlobal.BASE_UPLOAD + ava, image_avatar, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        HelperGlobal.SaveBitmapLocal(ava, ActivityProfileEdit.this, loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }

        if(cover != null && !cover.equals(""))
        {
            boolean check_image = HelperGlobal.CheckInternalImage(ActivityProfileEdit.this, cover);
            if(check_image)
            {
                String path = HelperGlobal.GetInternalPath(ActivityProfileEdit.this, cover);
                loader.displayImage(path, image_cover, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(loadedImage.getHeight() > loadedImage.getWidth())
                        {
                            image_cover.getLayoutParams().height = hScreen / 3;
                            image_cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
            else
            {
                loader.displayImage(HelperGlobal.BASE_UPLOAD + cover, image_cover, opt, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        HelperGlobal.SaveBitmapLocal(cover, ActivityProfileEdit.this, loadedImage);
                        if(loadedImage.getHeight() > loadedImage.getWidth())
                        {
                            image_cover.getLayoutParams().height = hScreen / 3;
                            image_cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }

        initilizeMap();
        switchHidePassword();
    }

    private void switchHidePassword()
    {
        if(hidePass)
        {
            int start, end;
            hidePass = false;
            start = tbl_row_password.getSelectionStart();
            end = tbl_row_password.getSelectionEnd();
            tbl_row_password.setTransformationMethod(new PasswordTransformationMethod());
            tbl_row_password.setSelection(start, end);

            button_hide_pass.setImageResource(R.drawable.icon_eye_inactive);
            button_hide_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchHidePassword();
                }
            });
        }
        else
        {
            int start, end;
            hidePass = true;
            start = tbl_row_password.getSelectionStart();
            end = tbl_row_password.getSelectionEnd();
            tbl_row_password.setTransformationMethod(null);
            tbl_row_password.setSelection(start, end);
            button_hide_pass.setImageResource(R.drawable.icon_eye_active);
            button_hide_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchHidePassword();
                }
            });
        }
    }

    private void show_change_picture()
    {
        final Dialog d = new Dialog(ActivityProfileEdit.this);
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

    private void save_profile(final String filename)
    {
        new AsyncTask<String, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String fu, ph, d, avatar, cov, longs, lat, val_password;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityProfileEdit.this);
                dialog.setCancelable(false);
                dialog.show();

                fu = tbl_row_fullname.getText().toString();
                ph = tbl_row_phone.getText().toString();
                d = tbl_row_desc.getText().toString();
                val_password = "";
                val_password = tbl_row_password.getText().toString();
                longs = ActivityProfileEdit.this.longs;
                lat = ActivityProfileEdit.this.lat;

                if(longs != null && !longs.equals("null") && lat != null && !lat.equals("null"))
                {
                    ArrayList<String> addr = HelperGlobal.getAddressMaps(ActivityProfileEdit.this, Double.parseDouble(lat), Double.parseDouble(longs));
                    if(addr.size() > 4)
                    {
                        city = addr.get(5);
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                String[] field = new String[]{"fu","ph","d","long","lat","provider","devVersion","devBrand","devID","pwd","city"};
                String[] values = new String[]{fu, ph, d, longs, lat,HelperGlobal.GetProvider(ActivityProfileEdit.this),
                        HelperGlobal.GetDeviceVersion(), HelperGlobal.GetDeviceBrand(), HelperGlobal.GetDeviceID(ActivityProfileEdit.this),val_password, city};
                ActionProfile prof_save = new ActionProfile(ActivityProfileEdit.this);
                prof_save.setParam(getParam(), getToken());
                prof_save.setURI(HelperGlobal.U_SAVE_PROFILE);
                prof_save.setArrayPOST(field, values);
                prof_save.executeSave();
                if(prof_save.getSuccess())
                {
                    isSuccess = true;
                    result = prof_save.getMessage();
                }
                else {
                    isSuccess = false;
                    result = prof_save.getMessage();
                }
                return result;
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
                    back();
                }
                Toast.makeText(ActivityProfileEdit.this, s, Toast.LENGTH_LONG).show();

            }
        }.execute(filename);
    }

    private void back()
    {
        Intent i = new Intent(ActivityProfileEdit.this, ActivityProfile.class);
        i.putExtra("src",extra_src);
        i.putExtra("target",extra_target);
        i.putExtra("mode",extra_mode);
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
        new AsyncTask<Void,Integer, String>(){

            Bitmap bm;

            @Override
            protected String doInBackground(Void... voids) {
                String urls = "";
                if(mImageCaptureUri != null)
                {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    String fi = getPath(ActivityProfileEdit.this, mImageCaptureUri);
                    Uri r = Uri.parse(fi);
                    File f = new File(r.getPath().toString());
                    urls = f.getAbsolutePath();
                    //bm = BitmapFactory.decodeFile(urls,options);
                    bm = HelperGlobal.ResizeBitmap(urls, 1024, 1024, false);
                }
                else
                {
                    urls = "";

                }

                return urls;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s != null && !s.equals(""))
                {
                    if(isAvatar)
                    {
                        temp_ava = s;
                        image_avatar.setImageBitmap(bm);
                    }
                    else {
                        temp_cover = s;
                        if(bm.getHeight() > bm.getWidth())
                        {
                            image_cover.getLayoutParams().height = hScreen / 3;
                            image_cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                        image_cover.setImageBitmap(bm);
                    }
                    startUploadService();
                }

                super.onPostExecute(s);
            }
        }.execute();
    }

    private void startUploadService(){
        Intent i = new Intent(ActivityProfileEdit.this, ServiceGlobal.class);
        i.putExtra("is_upload",true);
        i.putExtra("state","profile");
        i.putExtra("is_avatar",isAvatar);
        i.putExtra("param", getParam());
        i.putExtra("token",getToken());
        if(isAvatar){
            i.putExtra("filename",temp_ava);
        }
        else
        {
            i.putExtra("filename",temp_cover);
        }
        startService(i);
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
