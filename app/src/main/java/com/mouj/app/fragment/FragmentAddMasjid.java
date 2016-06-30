package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionPost;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 6/29/16.
 */
public class FragmentAddMasjid extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_ADD_MASJID = "tag:fragment-add-masjid";

    ImageButton imagebutton_back;
    ViewButton button_save;
    ViewEditText value_name, value_email, value_phone;
    String value_lat, value_long, value_addres = "",youtube_url;
    RelativeLayout layout_location;
    Map<String, String> param;
    ViewText text_address;
    RelativeLayout button_pick;

    ArrayList<String> temp_file_id, temp_file_name;
    boolean allowUploadVideo = true, allowUploadAudio = true, isAudio = false;
    private static final int PICK_FROM_FILE = 3;
    private Uri mFileUri;
    int counterUpload = 0;
    UploadTask taskUploader;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        first_load(savedInstanceState);
        main_view = inflater.inflate(R.layout.fragment_add_masjid, container, false);
        return main_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null)
        {
            this.activity = (BaseActivity) activity;
            iFragment = (FragmentInterface) this.activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(activity != null)
        {
            init();
        }
    }

    private void init()
    {
        value_lat = "";
        value_long = "";
        param = new HashMap<String, String>();
        temp_file_id = new ArrayList<String>();
        temp_file_name = new ArrayList<String>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imagebutton_back = (ImageButton) activity.findViewById(R.id.add_masjid_imagebutton_back);
        button_save = (ViewButton) activity.findViewById(R.id.add_masjid_btn_save);
        value_name = (ViewEditText) activity.findViewById(R.id.add_masjid_edittext_fullname);
        value_email = (ViewEditText) activity.findViewById(R.id.add_masjid_edittext_email);
        value_phone = (ViewEditText) activity.findViewById(R.id.add_masjid_edittext_phone);
        layout_location = (RelativeLayout) activity.findViewById(R.id.add_masjid_relative_location);
        text_address = (ViewText) activity.findViewById(R.id.add_masjid_value_coordinate);
        button_pick = (RelativeLayout) activity.findViewById(R.id.add_masjid_button_photo);

        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        layout_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMaps maps = new FragmentMaps();
                maps.setExtras(false);
                maps.setFragmentSource(FragmentAddMasjid.this);
                iFragment.onNavigate(maps, "", false, "");
            }
        });
        button_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("image/*");
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }


    private void save()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg, finalName, finalEmail, finalPhone, finalLat, finalLong;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();

                finalName = value_name.getText().toString();
                finalEmail = value_email.getText().toString();
                finalPhone = value_phone.getText().toString();
                finalLat = value_lat;
                finalLong = value_long;
            }

            @Override
            protected String doInBackground(Void... params) {
                if(!value_email.equals("") && !HelperGlobal.isValidEmail(finalEmail))
                {
                    isSuccess = false;
                    msg = getResources().getString(R.string.base_string_invalid_email);
                }
                else {
                    String media_builder = "";
                    if(temp_file_id.size() > 0)
                    {
                        for(int i = 0;i < temp_file_id.size();i++)
                        {
                            media_builder += "," + temp_file_id.get(i);
                        }
                        media_builder.substring(1);
                    }

                    String[] field = new String[]{"token","param","mfu","mem","mph","long","lat","group", "media"};
                    String[] value = new String[]{activity.getToken(),activity.getParam(),finalName,finalEmail,finalPhone,finalLong,finalLat,"m", media_builder};
                    ActionPost post = new ActionPost(activity);
                    post.setArrayPOST(field,value);
                    post.executeAddMasjid();
                    if(post.getSuccess())
                    {
                        isSuccess = true;
                        msg = post.getMessage();
                    }
                    else {
                        isSuccess = false;
                        msg = post.getMessage();
                    }
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
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    activity.onBackPressed();
                }
                if(!isSuccess)
                {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onUpdateUI() {
        super.onUpdateUI();
        param = getParameter();
        value_lat = param.get("lat");
        value_long = param.get("long");
        value_addres = param.get("addr");
        text_address.setText(value_lat + "," + value_long);
    }



    private void chooseFile(String mime){
        Intent intent = new Intent();
        intent.setType(mime);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("fileUpload", mFileUri);
    }

    private void first_load(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            mFileUri = savedInstanceState.getParcelable("fileUpload");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == activity.RESULT_OK || resultCode != activity.RESULT_CANCELED)
        {
            switch (requestCode) {

                case PICK_FROM_FILE:
                    mFileUri = null;
                    mFileUri = data.getData();
                    onPickedFiles("gal");
                    break;
            }
        }
    }

    private void onPickedFiles(String type)
    {
        if(type.equals("gal"))
        {
            String fi = getPath(activity, mFileUri);
            Uri r = Uri.parse(fi);
            File f = new File(r.getPath().toString());
            String uri_final = f.getAbsolutePath();
            String ext = uri_final.substring(uri_final.lastIndexOf(".") + 1, uri_final.length());
            if(ext.equals("mp3"))
            {
                allowUploadAudio = false;
            }
            LinearLayout attachLayout = (LinearLayout) activity.findViewById(R.id.add_masjid_linear_attachment);
            temp_file_name.add(uri_final);
            attachInLayout(temp_file_name.size() - 1, attachLayout, uri_final,"gen");
        }
        else
        {
            LinearLayout attachLayout = (LinearLayout) activity.findViewById(R.id.add_masjid_linear_attachment);
            temp_file_name.add(youtube_url);
            attachInLayout(temp_file_name.size() - 1, attachLayout, youtube_url,"youtube");
        }
    }

    private void attachInLayout(final int ids, final LinearLayout container, String filenames, String type)
    {
        final View item = inflater.inflate(R.layout.create_post_attachment_item, null);
        item.setTag(ids);
        container.addView(item);
        String files = filenames.substring(filenames.lastIndexOf("/") + 1);
        ViewText filename = (ViewText) item.findViewById(R.id.create_post_textview_attach_item);
        filename.setText(files);
        final ImageButton button_cancel = (ImageButton) item.findViewById(R.id.create_post_imagebutton_cancel_attach);
        button_cancel.setTag(ids);
        ProgressBar progressBar = (ProgressBar) item.findViewById(R.id.create_post_progressbar_attach);
        progressBar.setTag(ids);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < temp_file_name.size(); i++) {
                    if (button_cancel.getTag() == item.getTag()) {
                        container.removeView(item);
                        taskUploader.cancel(true);
                        temp_file_name.remove(button_cancel.getTag());
                        if (temp_file_id.size() > 0 && temp_file_id.contains(button_cancel.getTag())) {
                            temp_file_id.remove(button_cancel.getTag());
                        }
                    }
                }
            }
        });
        if(type.equals("gen"))
        {
            taskUploader = new UploadTask(progressBar, ids);
            taskUploader.setIsVideo(false);
            taskUploader.execute(filenames);
        }
        else
        {
            taskUploader = new UploadTask(progressBar, ids);
            taskUploader.setIsVideo(true);
            taskUploader.execute(filenames);
        }
    }

    private class UploadTask extends AsyncTask<String, Integer, String> {

        ProgressBar progressBar;
        String filename;
        int tag;
        boolean isSuccess = false;
        String msg;
        boolean isVideo = false;

        public UploadTask(ProgressBar progressBar, int tags)
        {
            filename = "";
            this.progressBar = progressBar;
            this. tag = tags;
        }

        public void setIsVideo(boolean flag)
        {
            this.isVideo = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String res = "0";
            if(!isCancelled())
            {
                if(isVideo)
                {
                    String[] field = new String[]{"token","param","li"};
                    String[] values = new String[]{activity.getToken(),activity.getParam(),HelperGlobal.getYoutubeID(params[0])};
                    String url_builder = HelperGlobal.U_POST_UPLOAD;
                    if(HelperGlobal.checkConnection(activity))
                    {
                        String response = HelperGlobal.PostData(url_builder, field, values);
                        if(response != null)
                        {
                            try {
                                JSONObject object = new JSONObject(response);
                                if(object.getBoolean("status"))
                                {
                                    allowUploadVideo = false;
                                    isSuccess = true;
                                    temp_file_id.add(object.getString("i"));
                                }
                                else
                                {
                                    isSuccess = false;
                                    msg = object.getString("msg");
                                }
                            } catch (JSONException e) {
                                isSuccess = false;
                                msg = e.getMessage().toString();
                            }
                        }
                        else
                        {
                            isSuccess = false;
                            msg = getResources().getString(R.string.main_null_json);
                        }
                    }
                    else
                    {
                        isSuccess = false;
                        msg = getResources().getString(R.string.main_no_internet);
                    }
                }
                else
                {
                    filename = params[0];
                    int serverResponseCode = 0;
                    HttpURLConnection conn = null;
                    DataOutputStream dos = null;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1024;
                    File sourceFile = new File(filename);
                    int sentByte = 0;

                    if (!sourceFile.isFile()) {
                        isSuccess = false;
                        msg = "File invalid";
                    }
                    else
                    {
                        try {
                            // open a URL connection to the Servlet
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);
                            URL url = new URL(HelperGlobal.U_POST_UPLOAD);

                            // Open a HTTP  connection to  the URL
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); // Allow Inputs
                            conn.setDoOutput(true); // Allow Outputs
                            conn.setUseCaches(false); // Don't use a Cached Copy
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                            conn.setRequestProperty("file", filename);


                            dos = new DataOutputStream(conn.getOutputStream());

                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
                            dos.writeBytes(lineEnd);

                            // assign value
                            dos.writeBytes(filename.substring(filename.lastIndexOf(".")+1));
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + lineEnd);

                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=" + filename  + lineEnd);
                            dos.writeBytes(lineEnd);

                            // create a buffer of  maximum size
                            bytesAvailable = fileInputStream.available();

                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            // read file and write it into form...
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                sentByte += bytesRead;
                                publishProgress((int) (sentByte * 100 / bytesAvailable));

                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            // send multipart form data necesssary after file data...
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // Responses from the server (code and message)
                            serverResponseCode = conn.getResponseCode();
                            String serverResponseMessage = conn.getResponseMessage();

                            if(serverResponseCode == 200){

                                res = serverResponseMessage;
                                fileInputStream.close();
                                dos.flush();
                                dos.close();

                                InputStream in = null;
                                try {
                                    in = conn.getInputStream();
                                    byte[] buffers = new byte[1024];
                                    int read;
                                    while ((read = in.read(buffers)) > 0) {
                                        res = new String(buffers, 0, read, "utf-8");
                                    }

                                    JSONObject obj = new JSONObject(res);
                                    if(obj.getBoolean("status"))
                                    {
                                        isSuccess = true;
                                        temp_file_id.add(obj.getString("i"));
                                    }
                                    else
                                    {
                                        isSuccess = true;
                                        msg = obj.getString("msg");
                                    }
                                }
                                catch (IOException es)
                                {
                                    isSuccess = false;
                                    msg = es.getMessage().toString();
                                }
                                finally {
                                    in.close();
                                    isSuccess = true;
                                }
                            }
                            if(serverResponseCode != 200)
                            {
                                isSuccess = false;
                                msg = "File Invalid";
                                fileInputStream.close();
                                dos.flush();
                                dos.close();
                            }

                            //close the streams //


                        } catch (MalformedURLException ex) {
                            isSuccess = false;
                            msg = ex.getMessage().toString();
                        } catch (Exception e) {
                            isSuccess = false;
                            msg = e.getMessage().toString();
                        }

                    }
                }
            }
            else
            {
                isSuccess = false;
                msg = getResources().getString(R.string.create_post_upload_cancel);
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(progressBar.getTag().equals(tag))
            {
                progressBar.setProgress(values[0]);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            String f = filename.substring(filename.lastIndexOf("/") + 1);
            Toast.makeText(activity,f + " " + getResources().getString(R.string.create_post_upload_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isSuccess)
            {
                Toast.makeText(activity,getResources().getString(R.string.create_post_upload_done), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(taskUploader != null)
        {
            taskUploader = null;
        }
    }

    private void dialogVideo()
    {
        final Dialog d = new Dialog(activity);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.base_dialog_edittext);
        d.setCancelable(true);


        ViewButton button_ok = (ViewButton) d.findViewById(R.id.base_dialog_ok);
        ViewButton button_cancel = (ViewButton) d.findViewById(R.id.base_dialog_cancel);
        ViewText textview_title = (ViewText) d.findViewById(R.id.base_dialog_title);
        final ViewEditText editText = (ViewEditText) d.findViewById(R.id.base_dialog_message);

        textview_title.setSemiBold();
        textview_title.setText(getResources().getString(R.string.create_post_dialog_pick_url));
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkerUrl = HelperGlobal.isValidYoutubeUrl(editText.getText().toString());
                if(checkerUrl)
                {
                    youtube_url = editText.getText().toString();
                    onPickedFiles("youtube");
                    d.dismiss();
                }
                else
                {
                    Toast.makeText(activity, getResources().getString(R.string.create_post_youtube_invalid_url), Toast.LENGTH_SHORT).show();
                }

            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    @SuppressWarnings("deprecation")
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
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
