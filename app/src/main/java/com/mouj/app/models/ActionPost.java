package com.mouj.app.models;

import android.content.Context;

import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 11/26/15.
 */
public class ActionPost {

    Context mContext;
    boolean isSuccess = false;
    boolean isEdit = false, isDelete = false, modeAllData = false;
    String url, param, token, msg, pid, method, target, downloadLink;
    int l,o;
    String[] post_field, post_value;
    ArrayList<String> postid,posttitle,postdesc,postdate,postusers,posttype,postfile,postuserid, mediatype, mediafile, postlink, postrepcaption, postcontenttype, postcontentdatestart, postcontentrepeat, postcontentlocation, date_notif;
    String tag, timelineAll = "a", modeRepost, contentType = "article", category_timeline = "a", cateogry_users = "a", category_posting = "a", hashtag = "", fav = "", notif_type = "", notif_message = "", notif_ticker = "", notif_title = "";
    ArrayList<Boolean> postrep, postfav, showrep, postfollowed;


    public ActionPost(Context context)
    {
        this.mContext = context;
        this.url = "";
        this.param = "";
        this.token = "";
        this.msg = "";
        this.pid = "";
        this.method = "home";
        this.target = "";
        this.tag = "";
        this.modeRepost = "";
        this.l = 0;
        this.o = 0;
        postid = new ArrayList<String>();
        posttitle = new ArrayList<String>();
        postdesc = new ArrayList<String>();
        postdate = new ArrayList<String>();
        postusers = new ArrayList<String>();
        posttype = new ArrayList<String>();
        postfile = new ArrayList<String>();
        postuserid = new ArrayList<String>();
        mediatype = new ArrayList<String>();
        mediafile = new ArrayList<String>();
        postlink = new ArrayList<String>();
        postrep = new ArrayList<Boolean>();
        postfav = new ArrayList<Boolean>();
        postrepcaption = new ArrayList<String>();
        showrep = new ArrayList<Boolean>();
        postfollowed = new ArrayList<Boolean>();
        postcontenttype = new ArrayList<String>();
        postcontentdatestart = new ArrayList<String>();
        postcontentrepeat = new ArrayList<String>();
        postcontentlocation = new ArrayList<String>();
        date_notif = new ArrayList<String>();
    }

    public void setParam(String param, String token)
    {
        this.param = param;
        this.token = token;
    }

    public void setURL(String uri)
    {
        this.url = uri;
    }

    public void setArrayPOST(String[] field, String[] value)
    {
        this.post_field = field;
        this.post_value = value;
    }

    public void setLimit(int l)
    {
        this.l = l;
    }

    public void setOffset(int o)
    {
        this.o = o;
    }

    public void setPID(String pid)
    {
        this.pid = pid;
    }

    public void setMethod(String m)
    {
        this.method = m;
    }

    public void setContentType(String type)
    {
        this.contentType = type;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public void setModeAllData(boolean modeAllData)
    {
        this.modeAllData = modeAllData;
    }
    public void setFilterParameter(String paramAll)
    {
        this.timelineAll = paramAll;
    }
    public void setCategoryTimeline(String ct)
    {
        this.category_timeline = ct;
    }

    public void setCategoryUsers(String us)
    {
        this.cateogry_users = us;
    }

    public void setCategoryPosting(String cp)
    {
        this.contentType = cp;
    }

    public void setHashTag(String tags)
    {
        this.hashtag = tags;
    }

    public void setFavorite(String fav)
    {
        this.fav = fav;
    }

    public void executeSave()
    {
        setURL(HelperGlobal.U_POST_CR);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeList()
    {

        setURL(HelperGlobal.U_POST_MAIN);
        String url_builder = this.url +"?token="+token+"&param="+param+"&l="+Integer.toString(l)+"&o="+Integer.toString(o)+"&method="+method+"&tag="+tag;
        if(method.equals("view"))
        {
            url_builder += "&target="+target+"&hashtag="+hashtag;
        }
        url_builder += "&type="+contentType+"&ct="+category_timeline+"&cu="+cateogry_users+"&cp="+category_posting+"&hashtag="+hashtag+"&fav="+fav;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(url_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectMain = new JSONObject(response);
                    if(objectMain.getBoolean("status"))
                    {
                        JSONArray arr = objectMain.getJSONArray("data");
                        for(int i = 0;i < arr.length();i++)
                        {
                            JSONObject objectChild = arr.getJSONObject(i);
                            postid.add(objectChild.getString("i"));
                            posttitle.add(objectChild.getString("t"));
                            postdesc.add(objectChild.getString("d"));
                            postdate.add(objectChild.getString("da"));
                            postusers.add(objectChild.getString("u"));
                            posttype.add(objectChild.getString("typ"));
                            postfile.add(objectChild.getString("f"));
                            postuserid.add(objectChild.getString("ukey"));
                            postlink.add(objectChild.getString("don"));
                            postfav.add(objectChild.getBoolean("fav"));
                            postrep.add(objectChild.getBoolean("rep"));
                            postrepcaption.add(objectChild.getString("repcap"));
                            showrep.add(objectChild.getBoolean("showrep"));
                            postfollowed.add(objectChild.getBoolean("followed"));
                            postcontenttype.add(objectChild.getString("content_type"));
                        }
                        isSuccess = true;
                        this.o = l + o;
                        msg = objectMain.getString("msg");
                    }
                    else {
                        isSuccess = false;
                        msg = objectMain.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    msg = ex.getMessage().toString();
                }
            }
            else {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeDetail()
    {
        setURL(HelperGlobal.U_POST_DET);
        String uri_builder = this.url + "?token="+token+"&param="+param+"&pid="+pid;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.GetJSON(uri_builder.replace(" ", "%20"));
            if(response != null)
            {
                try{
                    JSONObject objectDetail = new JSONObject(response);
                    if(objectDetail.getBoolean("status"))
                    {
                        JSONArray arr = objectDetail.getJSONArray("data");
                        JSONObject objectChild = arr.getJSONObject(0);
                        postid.add(objectChild.getString("i"));
                        posttitle.add(objectChild.getString("t"));
                        postdesc.add(objectChild.getString("d"));
                        postdate.add(objectChild.getString("da"));
                        postusers.add(objectChild.getString("u"));
                        downloadLink = objectChild.getString("don");
                        postfav.add(objectChild.getBoolean("fav"));
                        postrep.add(objectChild.getBoolean("rep"));
                        postrepcaption.add(objectChild.getString("repcap"));
                        showrep.add(objectChild.getBoolean("showrep"));
                        postcontenttype.add(objectChild.getString("content_type"));
                        postcontentdatestart.add(objectChild.getString("content_date_start"));
                        postcontentrepeat.add(objectChild.getString("content_repeat"));
                        postcontentlocation.add(objectChild.getString("content_location"));
                        JSONArray arrMedia = objectDetail.getJSONArray("media");
                        isEdit = objectDetail.getBoolean("ed");
                        isDelete = objectDetail.getBoolean("de");
                        for(int i = 0;i < arrMedia.length();i++)
                        {
                            JSONObject objMedia = arrMedia.getJSONObject(i);
                            mediatype.add(objMedia.getString("ty"));
                            mediafile.add(objMedia.getString("fi"));
                        }
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                        msg = objectDetail.getString("msg");
                    }
                }catch (JSONException ex)
                {
                    isSuccess = false;
                    msg = ex.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else{
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeDelete()
    {
        setURL(HelperGlobal.U_POST_DEL);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeReportPost()
    {
        setURL(HelperGlobal.U_POST_REPORT);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }


    public void executeAddMasjid()
    {
        setURL(HelperGlobal.U_PROFILE_ADD_MASJID);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public void executeRepost()
    {
        setURL(HelperGlobal.U_POST_REPOST);
        String url_builder = this.url;
        if(HelperGlobal.checkConnection(mContext))
        {
            String response = HelperGlobal.PostData(url_builder, post_field, post_value);
            if(response != null)
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("status"))
                    {
                        isSuccess = true;
                        modeRepost = object.getString("mode");
                        if(modeRepost.equals("add-schedule"))
                        {
                            JSONArray arr = object.getJSONArray("date_list");
                            for(int i = 0;i < arr.length();i++)
                            {
                                JSONObject objectChild = arr.getJSONObject(i);
                                date_notif.add(objectChild.getString("date"));
                            }
                            notif_type = object.getString("n_type");
                            notif_title = object.getString("n_title");
                            notif_message = object.getString("n_message");
                            notif_ticker = object.getString("n_ticker");
                        }
                    }
                    else
                    {
                        isSuccess = false;
                    }
                    msg = object.getString("msg");
                } catch (JSONException e) {
                    isSuccess = false;
                    msg = e.getMessage().toString();
                }
            }
            else
            {
                isSuccess = false;
                msg = mContext.getResources().getString(R.string.main_null_json);
            }
        }
        else
        {
            isSuccess = false;
            msg = mContext.getResources().getString(R.string.main_no_internet);
        }
    }

    public boolean getSuccess()
    {
        return this.isSuccess;
    }

    public String getMessage()
    {
        return this.msg;
    }

    public int getLimit()
    {
        return this.l;
    }

    public int getOffset()
    {
        return this.o;
    }
    public boolean getEdit()
    {
        return this.isEdit;
    }

    public String getDownloadLink()
    {
        return this.downloadLink;
    }
    public String getModeRepost()
    {
        return this.modeRepost;
    }

    public boolean getDelete()
    {
        return this.isDelete;
    }
    public ArrayList<String> getPID()
    {
        return this.postid;
    }
    public ArrayList<String> getPTitle()
    {
        return this.posttitle;
    }
    public ArrayList<String> getPDesc()
    {
        return this.postdesc;
    }
    public ArrayList<String> getPDate()
    {
        return this.postdate;
    }
    public ArrayList<String> getPUsers()
    {
        return this.postusers;
    }
    public ArrayList<String> getPType()
    {
        return this.posttype;
    }
    public ArrayList<String> getPFiles()
    {
        return this.postfile;
    }
    public ArrayList<String> getPUserid()
    {
        return this.postuserid;
    }
    public ArrayList<String> getMediaType()
    {
        return this.mediatype;
    }
    public ArrayList<String> getMediaFile()
    {
        return this.mediafile;
    }
    public ArrayList<String> getPDon()
    {
        return this.postlink;
    }
    public ArrayList<Boolean> getPRep()
    {
        return this.postrep;
    }
    public ArrayList<Boolean> getPFav()
    {
        return this.postfav;
    }
    public ArrayList<String> getPRepCaption()
    {
        return this.postrepcaption;
    }
    public ArrayList<Boolean> getPShowRep()
    {
        return this.showrep;
    }
    public ArrayList<Boolean> getPFollowed()
    {
        return this.postfollowed;
    }
    public ArrayList<String> getPContentType()
    {
        return this.postcontenttype;
    }
    public ArrayList<String> getPContentDateStart()
    {
        return this.postcontentdatestart;
    }
    public ArrayList<String> getPContentRepeat()
    {
        return this.postcontentrepeat;
    }
    public ArrayList<String> getPContentLocation()
    {
        return this.postcontentlocation;
    }

    public ArrayList<String> getDateList()
    {
        return date_notif;
    }
    public String getNotifTitle()
    {
        return notif_title;
    }
    public String getNotifTicker()
    {
        return notif_ticker;
    }
    public String getNotifMessage()
    {
        return notif_message;
    }
    public String getNotifType()
    {
        return notif_type;
    }
}
