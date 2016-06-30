package com.mouj.app.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.view.ViewDialogMessage;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ekobudiarto on 10/13/15.
 */
public class HelperGlobal {


    //public static String BASE_URL = "http://cupslice.com/masjid/";
    public static String BASE_URL = "http://www.cupslice.com/masjid/";
    public static String F_URL = "index.php/api/v1/";
    public static String U_SIGNUP = BASE_URL + F_URL + "signup";
    public static String U_RESET = BASE_URL + F_URL + "forgot";
    public static String U_LOGIN = BASE_URL + F_URL + "login";
    public static String U_GROUP = BASE_URL + F_URL + "get-roles";
    public static String U_STATE = BASE_URL + F_URL + "get-state";
    public static String U_STATUS = BASE_URL + F_URL + "get-status";
    public static String U_CONFIG = BASE_URL + F_URL + "get-config";
    public static String U_STEP_ONE_MASJID = BASE_URL + F_URL + "step-completed";
    public static String U_STEP_FOLLOWING = BASE_URL + F_URL + "step-following";
    public static String U_STEP_FOLLOWING_SAVE = BASE_URL + F_URL + "step-following-save";
    public static String U_PROFILE = BASE_URL + F_URL + "get-profile";
    public static String U_PROFILE_FOLLOW = BASE_URL + F_URL + "profile-follow";
    public static String U_UPLOAD = BASE_URL + F_URL + "profile-upload";
    public static String U_SAVE_PROFILE = BASE_URL + F_URL + "profile-save";
    public static String U_PROFILE_ADD_MASJID = BASE_URL + F_URL + "profile-add-masjid";
    public static String U_LIST_SCH = BASE_URL + F_URL + "schedule-users-get";
    public static String U_SAVE_SCH = BASE_URL + F_URL + "schedule-users-save";
    public static String U_SCH_TIMELINE = BASE_URL + F_URL + "schedule-users-timeline";
    //public static String U_SCH_DET = BASE_URL + F_URL + "schedule-users-detail";
    public static String U_SCH_DET = BASE_URL + F_URL + "schedule-detail";
    public static String U_SCH_DEL = BASE_URL + F_URL + "schedule-users-delete";
    public static String U_SCH_REPOST = BASE_URL + F_URL + "schedule-users-addtome";
    public static String U_POST_CR = BASE_URL + F_URL + "content-user-save";
    public static String U_POST_REPOST = BASE_URL + F_URL + "content-user-repost";
    public static String U_POST_UPLOAD = BASE_URL + F_URL + "content-upload";
    //public static String U_POST_MAIN = BASE_URL + F_URL + "content-user-get";
    public static String U_POST_MAIN = BASE_URL + F_URL + "content-user-list";
    public static String U_POST_DET = BASE_URL + F_URL + "content-user-detail";
    public static String U_POST_DEL = BASE_URL + F_URL + "content-user-delete";
    public static String U_SRCH_POST = BASE_URL + F_URL + "search-post";
    public static String U_SRCH_USER = BASE_URL + F_URL + "search-users";
    public static String U_SRCH_TAG = BASE_URL + F_URL + "search-tag";
    //public static String U_SRCH_NOTIF = BASE_URL + F_URL + "search-notif";
    public static String U_SRCH_NOTIF = BASE_URL + F_URL + "notification-list";
    public static String BASE_UPLOAD = BASE_URL +"UPLOADED/";
    public static String U_POST_REPORT = BASE_URL + F_URL + "content-user-report";
    public static String U_GROUP_FOLLOWERS = BASE_URL + F_URL + "group-followers";
    public static String U_GROUP_FOLLOWERS_SEARCH = BASE_URL + F_URL + "group-followers-search";
    public static String U_GROUP_SAVE = BASE_URL + F_URL + "group-save";
    public static String U_GROUP_ADD_MEMBER = BASE_URL + F_URL + "group-add-member";
    public static String U_GROUP_UPLOAD = BASE_URL + F_URL + "group-upload-avatar";
    public static String U_GROUP_LIST = BASE_URL + F_URL + "group-list";
    public static String U_GROUP_LIST_POST = BASE_URL + F_URL + "group-list-post";
    public static String U_GROUP_DEL = BASE_URL + F_URL + "group-delete";
    public static String U_GROUP_DETAIL = BASE_URL + F_URL + "group-detail";
    public static String U_GROUP_REPOST = BASE_URL + F_URL + "group-share-post";
    public static String U_GROUP_REQUEST_JOIN = BASE_URL + F_URL + "group-request-join";
    public static String U_GROUP_NOTIF = BASE_URL + F_URL + "group-notif";
    public static String U_GROUP_REJECT_JOIN = BASE_URL + F_URL + "group-reject-join";
    public static String U_GROUP_DEL_MEMBER = BASE_URL + F_URL + "group-delete-member";
    public static String U_GROUP_DEL_POST = BASE_URL + F_URL + "group-delete-post";

    public static int APP_MAX_GROUP = 10;



    public static boolean checkConnection(Context context)
    {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static String GetJSON(String url)
    {
        InputStream is = null;
        String result = "";
        int timeoutConnection = 180000;
        // HTTP
        try {
            HttpParams param = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(param, timeoutConnection);

            HttpClient httpclient = new DefaultHttpClient(param); // for port 80 requests!
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            return null;
        }


        return result;
    }


    public static String PostData(String urls,String[] field, String[] value)
    {
        InputStream is = null;
        String result = "";

        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpPost httpPost = new HttpPost(urls);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> paramPost = new ArrayList<NameValuePair>();
            for(int i = 0;i < field.length;i++)
            {
                paramPost.add(new BasicNameValuePair(field[i],value[i]));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(paramPost));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(UnsupportedEncodingException e) {
            return "0";
        } catch (ClientProtocolException e) {
            return "0";
        } catch (IOException e) {
            return "0";
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            return "0";
        }


        return result;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public final static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    public final static boolean isValidEditText(String value) {
        if (value != null && value.length() > 0) {
            return true;
        }
        return false;
    }

    public static Bitmap BitmapDownload(String url)
    {
        Bitmap result = null;
        try{
            HttpUriRequest request = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(entity);

                result = BitmapFactory.decodeByteArray(bytes, 0,
                        bytes.length);
            } else {
                throw new IOException("Download failed, HTTP response code "
                        + statusCode + " - " + statusLine.getReasonPhrase());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static void SaveBitmapLocal(String filename,Context context,Bitmap bmp)
    {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap GetBitmapLocal(String filename,Context context)
    {
        Bitmap result = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
            result = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean CheckInternalImage(Context mContext, String filename)
    {
        String path = mContext.getFilesDir().getAbsolutePath() + "/" + filename;
        File file = new File(path);
        return file.exists();
    }

    public static String GetInternalPath(Context mContext, String filename)
    {
        return "file://" +mContext.getFilesDir().getAbsolutePath() + "/" + filename;
    }

    public static String GetDeviceID(Context c)
    {
        TelephonyManager tm = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String GetProvider(Context c)
    {
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    public static String GetDeviceVersion()
    {
        return Build.VERSION.RELEASE;
    }

    public static String GetDeviceBrand()
    {
        return Build.BRAND;
    }

    public static int GetDimension(String widthOrHeight, Context context)
    {
        int result = 0;
        //int height = 0;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(android.os.Build.VERSION.SDK_INT > 12)
        {
            Point size = new Point();
            display.getSize(size);
            if(widthOrHeight.equals("w"))
            {
                result = size.x;
            }
            else
            {
                result = size.y;
            }
        }
        else
        {
            if(widthOrHeight.equals("w"))
            {
                result = display.getWidth();
            }
            else
            {
                result = display.getHeight();
            }
        }
        return result;
    }

    public static String Upload(String urls, String uriFile, String[] field, String[] value)
    {
        String res = "0";
        String fileName = uriFile;
        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(uriFile);

        if (!sourceFile.isFile()) {
            return "0";
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urls);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("file", fileName);


                dos = new DataOutputStream(conn.getOutputStream());

                for(int i = 0;i < field.length;i++){
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+field[i]+"\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // assign value
                    dos.writeBytes(value[i]);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                }

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=" + fileName  + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
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
                }

                //close the streams //
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
                }
                catch (IOException es)
                {
                    es.printStackTrace();
                    res = "0";
                }
                finally {
                    in.close();
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                res = "0";
            } catch (Exception e) {
                e.printStackTrace();
                res = "0";
            }
            return res;

        } // End else block
    }

    public static void ExitApp(final Activity activity, String title)
    {

        final ViewDialogMessage dialog = new ViewDialogMessage(activity);
        dialog.setTitle(title);
        dialog.setMessage(activity.getResources().getString(R.string.exit_application));
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    public static String MP3MillisToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int MP3GetProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int MP3ProgressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }


    public static boolean isValidYoutubeUrl(String url) {

        if (url == null || url.equals("")) {
            return false;
        }
        else
        {
            String pattern = "^https?://.*(?:youtu.be/|v/|u/\\\\w/|embed/|watch?v=)([^#&?]*).*$";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url);

            if(matcher.find()){
                System.out.println(matcher.group());
                return true;
            }
        }
        // In other any case
        return false;
    }

    public static String getYoutubeID(String url) {

        String rs = "";
        if (url == null || url.equals("")) {
            return rs;
        }
        else
        {
            String pattern = "^https?://.*(?:youtu.be/|v/|u/\\\\w/|embed/|watch?v=)([^#&?]*).*$";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url);

            if(matcher.matches()){
                rs = matcher.group(1);
            }
        }
        // = In other any case
        return rs;
    }

    public static Bitmap ResizeBitmap(String url, int maxWidth, int maxHeight, boolean increase){

        Bitmap bm = BitmapFactory.decodeFile(url);
        int bmpWidth = bm.getWidth();
        int bmpHeight = bm.getHeight();

        int newWidth = 0;
        int newHeight = 0;

        if(bmpWidth < maxWidth || bmpHeight < maxHeight)
        {
            if(increase){
                if(bmpWidth > bmpHeight)
                {
                    double ratio = ((double) maxWidth) / bmpWidth;
                    newWidth = (int) (ratio * bmpWidth);
                    newHeight = (int) (ratio * bmpHeight);
                }
                else
                {
                    double ratio = ((double) maxHeight) / bmpHeight;
                    newWidth = (int) (ratio * bmpWidth);
                    newHeight = (int) (ratio * bmpHeight);
                }
            }
            else {
                newWidth = bmpWidth;
                newHeight = bmpHeight;
            }


        }
        else
        {
            if(bmpWidth > bmpHeight)
            {
                double ratio = ((double) maxWidth) / bmpWidth;
                newWidth = (int) (ratio * bmpWidth);
                newHeight = (int) (ratio * bmpHeight);
            }
            else
            {
                double ratio = ((double) maxHeight) / bmpHeight;
                newWidth = (int) (ratio * bmpWidth);
                newHeight = (int) (ratio * bmpHeight);
            }
        }


        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        //Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, newWidth, newHeight);
        return resizedBitmap;
    }

    public static ArrayList<String> getAddressMaps(Context context, double latitude, double longitude)
    {
        ArrayList<String> result = new ArrayList<String>();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(latitude, longitude, 1);
            result.add(0, listAddress.get(0).getLocality()); //City
            result.add(1, listAddress.get(0).getAdminArea()); //State
            result.add(2, listAddress.get(0).getPostalCode()); //Postal Code
            result.add(3, listAddress.get(0).getCountryName()); //Country Name
            result.add(4, listAddress.get(0).getCountryCode()); //Country Code
            result.add(5, listAddress.get(0).getSubAdminArea()); //kota
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return result;
    }

    public static void SendAnalytic(Tracker tracker, String screen) {
        if (tracker != null) {
            tracker.setScreenName(screen);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void setAlarmManager(Context context, int alarmID, String type, String title, String message, String ticker, String token, String param, String date, String action)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date d = sdf.parse(date);
            Intent receiver = new Intent(context, ReceiverNotification.class);
            receiver.putExtra("type", type);
            receiver.putExtra("title", title);
            receiver.putExtra("message", message);
            receiver.putExtra("ticker", ticker);
            receiver.putExtra("token", token);
            receiver.putExtra("param", param);
            receiver.setAction(action);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmID,receiver,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(Build.VERSION.SDK_INT >= 19)
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);
            }
            else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);
            }
        } catch (ParseException e) {
            //e.printStackTrace();
            Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void setFilterPreference(Context mContext,String filterAccess, String filterUsers, String filterPost)
    {
        SharedPreferences sp = mContext.getSharedPreferences("com_android_mouj_home_filter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("access", filterAccess);
        editor.putString("users", filterUsers);
        editor.putString("post", filterPost);
        editor.commit();
    }

    public static ArrayList<String> getFilterPreference(Context mContext)
    {
        ArrayList<String> data = new ArrayList<String>();
        SharedPreferences sp = mContext.getSharedPreferences("com_android_mouj_home_filter", Context.MODE_PRIVATE);
        data.add(sp.getString("access", "a"));
        data.add(sp.getString("users", "a"));
        data.add(sp.getString("post", "a"));
        return data;
    }

    public static void setTurnOnFeatureConnection(Context mContext,String isEnabled)
    {
        boolean enabled = false;
        if(isEnabled.equals("1"))
        {
            enabled = true;
        }
        SharedPreferences sp = mContext.getSharedPreferences("turn_on_connection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("enabled", enabled);
        editor.commit();
    }

    public static boolean getTurnOnFeatureConnection(Context mContext)
    {
        SharedPreferences sp = mContext.getSharedPreferences("turn_on_connection", Context.MODE_PRIVATE);
        return sp.getBoolean("enabled", false);
    }

    public static void removeFragmentParent(BaseActivity activity)
    {
        List<Fragment> lists = activity.getSupportFragmentManager().getFragments();
        if(lists != null)
        {
            BaseFragment fragment = (BaseFragment) lists.get(lists.size() - 1);
            if(fragment != null)
            {
                activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }


    public static String getCompleteAddressString(BaseActivity activity, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                strAdd = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
           strAdd = "";
        }
        return strAdd;
    }

}
