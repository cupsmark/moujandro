package com.mouj.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionSchedule;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ekobudiarto on 6/23/16.
 */
public class FragmentScheduleConnection extends BaseFragment {

    public static final String TAG_SCHEDULE_CONNECTION = "tag:fragment-schedule-connection";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;

    ViewText form_title, caption_users_target;
    ViewEditText value_title, value_address, value_description;
    ViewButton value_time, value_target, value_date, button_convert;
    RelativeLayout button_save, btn_pick_foto,btn_pick_video, btn_pick_sounds, btn_pick_doc, layout_button_pick;
    String selected_target = "", selected_date = "", selected_time = "", extra_mode, singleId = "", extra_src,value_rep_id = "0", selected_target_name = "", content_type = "schedule";
    int day,month,year, hour, minute, counterDateSet;
    Map<String, String> param;
    String edit_id,youtube_url;
    ArrayList<String> temp_file_id, temp_file_name;
    boolean allowUploadVideo = true, allowUploadAudio = true, isAudio = false;
    private static final int PICK_FROM_FILE = 3;
    private Uri mFileUri;
    int counterUpload = 0;
    UploadTask taskUploader;
    LayoutInflater inflater;
    ImageButton imagebutton_back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_schedule_connection, container, false);
        first_load(savedInstanceState);
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
        param = new HashMap<String, String>();
        temp_file_id = new ArrayList<String>();
        temp_file_name = new ArrayList<String>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        form_title = (ViewText) activity.findViewById(R.id.schedule_connection_users_fullname);
        caption_users_target = (ViewText) activity.findViewById(R.id.schedule_connection_caption_users_target);
        value_title = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_title);
        value_target = (ViewButton) activity.findViewById(R.id.schedule_connection_value_users_target);
        value_address = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_address);
        value_time = (ViewButton) activity.findViewById(R.id.schedule_connection_value_time);
        value_date = (ViewButton) activity.findViewById(R.id.schedule_connection_value_date);
        value_description = (ViewEditText) activity.findViewById(R.id.schedule_connection_value_description);
        button_save = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_save);
        button_convert = (ViewButton) activity.findViewById(R.id.schedule_connection_button_convert);
        btn_pick_foto = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_photo);
        btn_pick_video = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_video);
        btn_pick_sounds = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_sounds);
        btn_pick_doc = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_doc);
        layout_button_pick = (RelativeLayout) activity.findViewById(R.id.schedule_connection_button_pick);
        imagebutton_back = (ImageButton) activity.findViewById(R.id.schedule_connection_imagebutton_back);

        form_title.setBold();
        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        executeAccess();
        if(extra_mode.equals("edit"))
        {
            fetchEdit();
        }
        else {
            setDefault();
        }
    }

    private void executeAccess()
    {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = (c.get(Calendar.MONTH)) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        counterDateSet = 0;
        form_title.setText(activity.getUserData().get(3));
        if(activity.getUgid().equals("5"))
        {
            caption_users_target.setText(activity.getResources().getString(R.string.schedule_connection_target_masjid_caption));
            value_target.setText(activity.getResources().getString(R.string.schedule_connection_target_masjid));
            value_target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentScheduleConnectionGetMasjid masjid = new FragmentScheduleConnectionGetMasjid();
                    masjid.setFragmentSource(FragmentScheduleConnection.this);
                    iFragment.onNavigate(masjid,FragmentSearchMasjid.TAG_SEARCH_TAB_MASJID, false, "");
                }
            });
        }
        else
        {
            caption_users_target.setText(activity.getResources().getString(R.string.schedule_connection_target_ustadz));
            value_target.setText(activity.getResources().getString(R.string.schedule_connection_target_ustadz));
            value_target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentScheduleConnectionGetUstadz ustadz = new FragmentScheduleConnectionGetUstadz();
                    ustadz.setFragmentSource(FragmentScheduleConnection.this);
                    iFragment.onNavigate(ustadz,FragmentSearchUstadz.TAG_SEARCH_TAB_USTADZ, false, "");
                }
            });
        }
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        value_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimePicker();
            }
        });
        value_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();
            }
        });
    }

    public void setExtra(String mode, String target, String src)
    {
        this.extra_mode = mode;
        this.singleId = target;
        this.extra_src = src;
    }

    private void fetchEdit()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg;
            String edit_title, edit_desc, edit_date, edit_users_target, edit_address;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionSchedule schedule = new ActionSchedule(activity);
                schedule.setParam(activity.getParam(), activity.getToken());
                schedule.setSingleID(singleId);
                schedule.executeScheduleDetail();
                if(schedule.getSuccess())
                {
                    isSuccess = true;
                    edit_id = schedule.getSchID().get(0);
                    edit_title = schedule.getSchTitle().get(0);
                    edit_desc = schedule.getSchDesc().get(0);
                    edit_date = schedule.getSchDate().get(0);
                    edit_address = schedule.getSchLocation().get(0);
                    selected_target = schedule.getSchUserTargetID().get(0);
                    selected_target_name = schedule.getSchUserTargetName().get(0);
                }
                else {
                    isSuccess = false;
                    msg = schedule.getMessage();
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
                    //value_title.setFocusable(false);
                    //value_title.setInputType(InputType.TYPE_NULL);
                    value_title.setText(edit_title);
                    //value_target.setEnabled(false);
                    value_target.setText(selected_target_name);
                    //value_address.setInputType(InputType.TYPE_NULL);
                    value_address.setText(edit_address);
                    value_description.setText(edit_desc);
                    button_convert.setVisibility(View.VISIBLE);
                    button_convert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            content_type = "article";
                            save();
                        }
                    });
                    layout_button_pick.setVisibility(View.VISIBLE);
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
                                Toast.makeText(activity, getResources().getString(R.string.create_post_maximum_upload_video), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(activity, getResources().getString(R.string.create_post_maximum_upload_audio), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btn_pick_doc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseFile("text/plain");
                        }
                    });
                    String[] dateSplit = edit_date.split(" ");
                    if(dateSplit.length == 2 )
                    {
                        String dates = textDateConverted(edit_date, false);
                        String times = textDateConverted(edit_date, true);
                        selected_date = dateSplit[0];
                        selected_time = dateSplit[1];
                        value_date.setText(dates);
                        value_time.setText(times);
                    }
                    else {
                        setDefault();
                    }


                }
                if(!isSuccess)
                {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void save()
    {
        new AsyncTask<Void, Integer, String>()
        {
            ViewLoadingDialog dialog;
            boolean isSuccess = false;
            String msg = "", ntype = "", ntitle = "", nmessage = "", nticker = "";
            String finalTitle, finalUsersTarget, finalAddress, finalTime, finalDesc;
            ArrayList<String> dateList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(activity);
                dialog.setCancelable(false);
                dialog.show();

                dateList = new ArrayList<String>();
                finalTitle = value_title.getText().toString();
                finalUsersTarget = selected_target;
                finalAddress = value_address.getText().toString();
                finalTime = selected_date + " " +selected_time;
                finalDesc = value_description.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {
                String field_target = "";
                String values_target = "";
                String media_builder = "";
                if(temp_file_id.size() > 0)
                {
                    for(int i = 0;i < temp_file_id.size();i++)
                    {
                        media_builder += "," + temp_file_id.get(i);
                    }
                    media_builder.substring(1);
                }
                if(extra_mode.equals("edit"))
                {
                    field_target = "target";
                    values_target = singleId;
                }
                String[] field = new String[]{"token","param","ttl","usc","dst","des","loc","mode-action",field_target,"creator_type","cut","content_type", "media"};
                String[] value = new String[]{activity.getToken(),activity.getParam(),finalTitle,value_rep_id,finalTime,finalDesc, finalAddress,extra_mode,values_target, "connection", selected_target,content_type, media_builder};

                ActionSchedule schedule = new ActionSchedule(activity);
                schedule.setParam(activity.getParam(),activity. getToken());
                schedule.setArrayPOST(field, value);
                schedule.executeSave();
                if(schedule.getSuccess())
                {
                    isSuccess = schedule.getSuccess();
                    ntype = schedule.getNType();
                    ntitle = schedule.getNTitle();
                    nmessage = schedule.getNMessage();
                    nticker = schedule.getNTicker();
                    if(schedule.getNDateList().size() > 0)
                    {
                        dateList.addAll(schedule.getNDateList());
                    }
                }
                else {
                    isSuccess = false;
                }
                msg = schedule.getMessage();
                return null;
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
                    if(dateList.size() > 0)
                    {
                        String token = activity.getToken();
                        String param = activity.getParam();
                        for(int i = 0;i < dateList.size();i++)
                        {
                            HelperGlobal.setAlarmManager(activity, 1000 + i, ntype, ntitle, nmessage, nticker, token, param, dateList.get(i), "run.schedule");
                        }
                    }
                    activity.onBackPressed();
                }
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

            }
        }.execute();
    }

    private void setDefault()
    {
        String d = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
        String dates = textDateConverted(d, false);
        String times = textDateConverted(d, true);
        value_date.setText(dates);
        value_time.setText(times);
        selected_date = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day);
        selected_time = Integer.toString(hour)+":"+Integer.toString(minute);
    }

    private void setDatePicker()
    {
        final DatePickerDialog dialogs = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int years, int monthOfYears, int dayOfMonths) {
                if(counterDateSet % 2 == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dNow = null;
                    try {
                        String nows = sdf.format(new Date());
                        dNow = sdf.parse(nows);
                        Date dSet = sdf.parse(Integer.toString(years)+"-"+Integer.toString(monthOfYears + 1)+"-"+
                                Integer.toString(dayOfMonths));

                        if(dSet.before(dNow))
                        {
                            Toast.makeText(activity, getResources().getString(R.string.v1_schedule_new_invalid_date), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            year = years;
                            month = monthOfYears + 1;
                            day = dayOfMonths;
                            String dates = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
                            dates = textDateConverted(dates,false);
                            selected_date = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day);
                            value_date.setText(dates);
                        }
                    } catch (ParseException e) {
                        //e.printStackTrace();
                        Toast.makeText(activity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                counterDateSet++;
            }
        },year, month - 1, day);
        dialogs.setCancelable(true);
        dialogs.show();
        dialogs.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                counterDateSet = 0;
            }
        });
    }

    private void setTimePicker()
    {
        TimePickerDialog timePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                if(counterDateSet % 2 == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    Date dNow = new Date();
                    try {
                        String nows = sdf.format(new Date());
                        dNow = sdf.parse(nows);
                        Date dSet = sdf.parse(Integer.toString(year)+"-"+Integer.toString(month)+"-"+
                                Integer.toString(day) + " " + Integer.toString(hourOfDay) + ":" + Integer.toString(minutes) +
                                ":00");

                        if(dSet.before(dNow))
                        {
                            Toast.makeText(activity, getResources().getString(R.string.v1_schedule_new_invalid_date), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            hour = hourOfDay;
                            minute = minutes;

                            String dates = Integer.toString(year) +"-"+Integer.toString(month)+"-"+Integer.toString(day)+" "+Integer.toString(hour)+":"+Integer.toString(minute);
                            dates = textDateConverted(dates, true);
                            selected_time = Integer.toString(hour) + ":" + Integer.toString(minute)+":00";
                            value_time.setText(dates);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                counterDateSet++;
            }
        }, hour, minute, true);
        timePicker.setCancelable(false);
        timePicker.show();
        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                counterDateSet = 0;
            }
        });
    }

    private String textDateConverted(String date, boolean isTime)
    {
        try{
            String zone = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateNormal = sdf.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.US);
            if(isTime)
            {
                sdf2 = new SimpleDateFormat("hh:mm aa");
                zone = "";
            }
            String currents = sdf2.format(dateNormal);
            return currents + zone;
        }catch (ParseException ex)
        {
            return "";
        }
    }

    @Override
    public void onUpdateUI() {
        super.onUpdateUI();
        param = getParameter();
        selected_target = param.get("idTarget");
        value_target.setText(param.get("nameTarget"));
        value_address.setText(param.get("addressTarget"));
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
            LinearLayout attachLayout = (LinearLayout) activity.findViewById(R.id.schedule_connection_linear_media_item);
            temp_file_name.add(uri_final);
            attachInLayout(temp_file_name.size() - 1, attachLayout, uri_final,"gen");
        }
        else
        {
            LinearLayout attachLayout = (LinearLayout) activity.findViewById(R.id.schedule_connection_linear_media_item);
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
