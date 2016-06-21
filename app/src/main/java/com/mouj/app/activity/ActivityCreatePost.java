package com.mouj.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionPost;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityCreatePost extends BaseActivity {

    ImageButton button_back;
    ViewEditText text_title, text_content;
    private static final int PICK_FROM_FILE = 3;
    private Uri mFileUri;
    RelativeLayout btn_pick_foto,btn_pick_video, btn_pick_sounds, btn_pick_doc;
    ViewButton btn_post;

    ArrayList<String> temp_file_id, temp_file_name;
    int counterUpload = 0;
    UploadTask taskUploader;
    LayoutInflater inflater;
    int wScreen = 0;
    String youtube_url;
    String extra_mode,extra_target, extra_src, gpid, gptitle;
    Tracker tracker;
    boolean allowUploadVideo = true, allowUploadAudio = true, isAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_create_post);
        setShowingMenu(false);
        first_load(savedInstanceState);
        wScreen = HelperGlobal.GetDimension("w", ActivityCreatePost.this);
        temp_file_id = new ArrayList<String>();
        temp_file_name = new ArrayList<String>();
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Create Post");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }


    private void init()
    {
        youtube_url = "";
        extra_mode = "add";
        extra_target = "";
        btn_pick_foto = (RelativeLayout) findViewById(R.id.create_post_pick_photo);
        btn_pick_video = (RelativeLayout) findViewById(R.id.create_post_pick_video);
        btn_pick_sounds = (RelativeLayout) findViewById(R.id.create_post_pick_sounds);
        btn_pick_doc = (RelativeLayout) findViewById(R.id.create_post_pick_doc);
        text_title = (ViewEditText) findViewById(R.id.create_post_editfield_title);
        text_content = (ViewEditText) findViewById(R.id.create_post_editfield_content);
        btn_post = (ViewButton) findViewById(R.id.create_post_save);
        button_back = (ImageButton) findViewById(R.id.create_post_imagebutton_back);

        btn_post.setBold();
        text_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 2)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_pick_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("image/*");
            }
        });
        btn_pick_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowUploadVideo)
                {
                    dialogVideo();    
                }
                else
                {
                    Toast.makeText(ActivityCreatePost.this, getResources().getString(R.string.create_post_maximum_upload_video), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_pick_sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowUploadAudio)
                {
                    isAudio = true;
                    chooseFile("audio/*");
                }
                else
                {
                    Toast.makeText(ActivityCreatePost.this, getResources().getString(R.string.create_post_maximum_upload_audio), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_pick_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("text/plain");
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_action();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        extra_mode = getIntent().getExtras().getString("mode-action");
        if(extra_mode.equals("repost") || extra_mode.equals("edit"))
        {
            extra_target = getIntent().getExtras().getString("target");
            extra_src = getIntent().getExtras().getString("src");
            modeChecker();
        }
        else if(extra_mode.equals("post-group"))
        {
            gpid = getIntent().getExtras().getString("gpid");
            gptitle = getIntent().getExtras().getString("gptitle");
            btn_pick_foto.getLayoutParams().width = wScreen / 3;
            btn_pick_foto.setVisibility(View.VISIBLE);
            btn_pick_video.getLayoutParams().width = wScreen / 3;
            btn_pick_video.setVisibility(View.VISIBLE);
            btn_pick_sounds.getLayoutParams().width = wScreen / 3;
            btn_pick_sounds.setVisibility(View.VISIBLE);
            //btn_pick_doc.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_pick_foto.getLayoutParams().width = wScreen / 3;
            btn_pick_foto.setVisibility(View.VISIBLE);
            btn_pick_video.getLayoutParams().width = wScreen / 3;
            btn_pick_video.setVisibility(View.VISIBLE);
            btn_pick_sounds.getLayoutParams().width = wScreen / 3;
            btn_pick_sounds.setVisibility(View.VISIBLE);
            //btn_pick_doc.setVisibility(View.VISIBLE);
        }

    }

    private void modeChecker()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg;
            String title,desc;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityCreatePost.this);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionPost post = new ActionPost(ActivityCreatePost.this);
                post.setParam(getParam(), getToken());
                post.setPID(extra_target);
                post.executeDetail();
                if(post.getSuccess())
                {
                    title = post.getPTitle().get(0);
                    desc = post.getPDesc().get(0);
                    isSuccess = true;
                }
                else
                {
                    isSuccess = false;
                    msg = post.getMessage();
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
                    text_title.setText(title);
                    text_content.setText(desc);
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityCreatePost.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }



    private void save_action()
    {
        if(taskUploader != null)
        {
            if(taskUploader.getStatus() == AsyncTask.Status.FINISHED)
            {
                save_task();
            }
            if(taskUploader.getStatus() == AsyncTask.Status.RUNNING)
            {
                Toast.makeText(ActivityCreatePost.this, getResources().getString(R.string.create_post_upload_waiting), Toast.LENGTH_SHORT).show();
            }
            if(taskUploader.getStatus() == AsyncTask.Status.PENDING)
            {
                Toast.makeText(ActivityCreatePost.this, getResources().getString(R.string.create_post_upload_waiting), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            save_task();
        }
    }

    private void save_task()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg = "";
            String title,content,hashtag;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityCreatePost.this);
                dialog.setCancelable(false);
                dialog.show();
                btn_post.setEnabled(false);
                title = text_title.getText().toString();
                content = text_content.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {
                String media_builder = "";
                String hashtag_title = "";
                if(temp_file_id.size() > 0)
                {
                    for(int i = 0;i < temp_file_id.size();i++)
                    {
                        media_builder += "," + temp_file_id.get(i);
                    }
                }
                Pattern hashtagPattern = Pattern.compile("#(\\w+)");
                Matcher matcher = hashtagPattern.matcher(content);
                while(matcher.find())
                {
                    hashtag_title += "," + matcher.group(1);
                }

                String[] field = new String[]{"param","token","ttl","med","det","htg","mod","target","gpid"};
                String[] values = new String[]{getParam(),getToken(),title, media_builder, content,hashtag_title,extra_mode,extra_target,"0"};

                if(gpid != null && !gpid.equals("0"))
                {
                    values[8] = gpid;
                }

                ActionPost post = new ActionPost(ActivityCreatePost.this);
                post.setParam(getParam(), getToken());
                post.setArrayPOST(field, values);
                post.executeSave();
                if(post.getSuccess())
                {
                    isSuccess = true;
                }
                msg = post.getMessage();
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
                    btn_post.setEnabled(true);
                    back();
                }
                Toast.makeText(ActivityCreatePost.this, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void pick_file()
    {
        final Dialog d = new Dialog(ActivityCreatePost.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_create_post_pick);
        d.setCancelable(true);

        ViewButton btn_browse = (ViewButton) d.findViewById(R.id.create_post_dialog_file);
        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                d.dismiss();
                //chooseFile();
            }
        });

        ViewButton btn_url = (ViewButton) d.findViewById(R.id.create_post_dialog_url);
        btn_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.dismiss();
            }
        });
        d.show();
    }

    private void back()
    {
        Intent i = null;
        if(extra_mode.equals("post-group"))
        {
            i = new Intent(ActivityCreatePost.this, ActivityGroupListPost.class);
            i.putExtra("gpid",gpid);
            i.putExtra("gptitle", gptitle);
        }
        else
        {
            i = new Intent(ActivityCreatePost.this, MainActivity.class);
        }
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

    private void chooseFile(String mime){
        Intent intent = new Intent();
        intent.setType(mime);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("fileUri", mFileUri);
    }

    private void first_load(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            mFileUri = savedInstanceState.getParcelable("fileUri");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode != RESULT_CANCELED)
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
            String fi = getPath(ActivityCreatePost.this, mFileUri);
            Uri r = Uri.parse(fi);
            File f = new File(r.getPath().toString());
            String uri_final = f.getAbsolutePath();
            String ext = uri_final.substring(uri_final.lastIndexOf(".") + 1, uri_final.length());
            if(ext.equals("mp3"))
            {
                allowUploadAudio = false;
            }
            LinearLayout attachLayout = (LinearLayout) findViewById(R.id.create_post_linear_attach_container);
            temp_file_name.add(uri_final);
            attachInLayout(temp_file_name.size() - 1, attachLayout, uri_final,"gen");
        }
        else
        {
            LinearLayout attachLayout = (LinearLayout) findViewById(R.id.create_post_linear_attach_container);
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
                    String[] values = new String[]{getToken(),getParam(),HelperGlobal.getYoutubeID(params[0])};
                    String url_builder = HelperGlobal.U_POST_UPLOAD;
                    if(HelperGlobal.checkConnection(ActivityCreatePost.this))
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
            Toast.makeText(ActivityCreatePost.this,f + " " + getResources().getString(R.string.create_post_upload_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isSuccess)
            {
                Toast.makeText(ActivityCreatePost.this,getResources().getString(R.string.create_post_upload_done), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ActivityCreatePost.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(taskUploader != null)
        {
            taskUploader = null;
        }
    }

    private void dialogVideo()
    {
        final Dialog d = new Dialog(ActivityCreatePost.this);
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
                    Toast.makeText(ActivityCreatePost.this, getResources().getString(R.string.create_post_youtube_invalid_url), Toast.LENGTH_SHORT).show();
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
