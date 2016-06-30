package com.mouj.app.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.R;
import com.mouj.app.helper.FragmentInterface;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.models.ActionPost;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.mouj.app.view.ViewText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 6/29/16.
 */
public class FragmentAddUstadz extends BaseFragment {

    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    public static final String TAG_ADD_USTADZ = "tag:fragment-add-ustadz";

    ImageButton imagebutton_back;
    ViewButton button_save;
    ViewEditText value_name, value_email, value_phone;
    ArrayList<String> temp_file;
    Map<String, String> param;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_add_ustadz, container, false);
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
        temp_file = new ArrayList<String>();
        imagebutton_back = (ImageButton) activity.findViewById(R.id.add_ustadz_imagebutton_back);
        button_save = (ViewButton) activity.findViewById(R.id.add_ustadz_btn_save);
        value_name = (ViewEditText) activity.findViewById(R.id.add_ustadz_edittext_fullname);
        value_email = (ViewEditText) activity.findViewById(R.id.add_ustadz_edittext_email);
        value_phone = (ViewEditText) activity.findViewById(R.id.add_ustadz_edittext_phone);

        imagebutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
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
            }

            @Override
            protected String doInBackground(Void... params) {
                if(!value_email.equals("") && !HelperGlobal.isValidEmail(finalEmail))
                {
                    isSuccess = false;
                    msg = getResources().getString(R.string.base_string_invalid_email);
                }
                else {
                    String[] field = new String[]{"token","param","mfu","mem","mph","long","lat","group"};
                    String[] value = new String[]{activity.getToken(),activity.getParam(),finalName,finalEmail,finalPhone,finalLong,finalLat,"u"};
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
    }

}
