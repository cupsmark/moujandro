package com.mouj.app.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mouj.app.R;
import com.mouj.app.helper.HelperGlobal;
import com.mouj.app.helper.HelperGoogle;
import com.mouj.app.models.ActionSignup;
import com.mouj.app.models.ActionUGroup;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewDialogList;
import com.mouj.app.view.ViewEditText;
import com.mouj.app.view.ViewLoadingDialog;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivitySignup extends Activity {

    ViewButton button_roles, button_submit;
    String selected_value, selected_title;
    ViewEditText edit_fullname, edit_username,edit_phone, edit_email, edit_password, edit_confirm;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_signup);
        initComponent();
        GoogleAnalyticsInit();
    }

    private void GoogleAnalyticsInit()
    {
        tracker = ((HelperGoogle) this.getApplication()).getTracker(HelperGoogle.TrackerName.APP_TRACKER);
        HelperGlobal.SendAnalytic(tracker, "Page Registrasi");
    }
    

    private void initComponent()
    {
        selected_value = "0";
        selected_title = "";
        edit_fullname = (ViewEditText) findViewById(R.id.signup_edittext_fullname);
        edit_fullname.setRegular();
        edit_username = (ViewEditText) findViewById(R.id.signup_edittext_username);
        edit_username.setRegular();
        edit_phone = (ViewEditText) findViewById(R.id.signup_edittext_phone);
        edit_phone.setRegular();
        edit_email = (ViewEditText) findViewById(R.id.signup_edittext_email);
        edit_email.setRegular();
        edit_password = (ViewEditText) findViewById(R.id.signup_edittext_password);
        edit_password.setRegular();
        edit_confirm = (ViewEditText) findViewById(R.id.signup_edittext_confirm);
        edit_confirm.setRegular();
        button_roles = (ViewButton) findViewById(R.id.signup_button_roles);
        button_roles.setRegular();
        button_roles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_roles();
            }
        });
        button_submit = (ViewButton) findViewById(R.id.signup_button_register);
        button_submit.setRegular();
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute_action();
            }
        });
    }

    private void execute_action()
    {
        new AsyncTask<Void, Integer, String>(){

            ViewLoadingDialog dialog;
            String fullname, username, phone, email, password, confirm, message;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivitySignup.this);
                dialog.setCancelable(false);
                dialog.show();

                fullname = edit_fullname.getText().toString();
                username = edit_username.getText().toString();
                phone = edit_phone.getText().toString();
                email = edit_email.getText().toString();
                password = edit_password.getText().toString();
                confirm = edit_confirm.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {
                String flag = "";
                if(HelperGlobal.isValidEditText(fullname) && HelperGlobal.isValidEditText(username) && HelperGlobal.isValidEditText(phone)
                        && HelperGlobal.isValidEditText(email) && HelperGlobal.isValidEmail(email) && HelperGlobal.isValidEditText(password)
                        && HelperGlobal.isValidEditText(confirm) && (confirm.equals(password) && !selected_value.equals("0")))
                {
                    ArrayList<String> field = new ArrayList<String>();
                    field.add("token");
                    field.add("f");
                    field.add("u");
                    field.add("pn");
                    field.add("e");
                    field.add("pwd");
                    field.add("r");
                    ArrayList<String> values = new ArrayList<String>();
                    values.add(HelperGlobal.GetDeviceID(ActivitySignup.this));
                    values.add(fullname);
                    values.add(username);
                    values.add(phone);
                    values.add(email);
                    values.add(password);
                    values.add(selected_value);
                    ActionSignup signup = new ActionSignup(ActivitySignup.this);
                    signup.setParam(field, values);
                    signup.executePost();
                    if(signup.getSuccess())
                    {
                        flag = "1";
                    }
                    else
                    {
                        flag = "0";
                    }
                    message = signup.getMessage();
                }
                else {
                    if(!HelperGlobal.isValidEditText(fullname))
                    {
                        message = "Fullname " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(username))
                    {
                        message = "Username " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(phone))
                    {
                        message = "Phone Number " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(email))
                    {
                        message = "Email " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEmail(email))
                    {
                        message = "Email " + getResources().getString(R.string.v1_signup_validation_email);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(password))
                    {
                        message = "Password " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!HelperGlobal.isValidEditText(confirm))
                    {
                        message = "Username " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                    else if(!confirm.equals(password))
                    {
                        message = "Confirm " + getResources().getString(R.string.v1_signup_validation_equal);
                        flag = "0";
                    }
                    else if(selected_value.equals("0"))
                    {
                        message = "Group " + getResources().getString(R.string.v1_signup_validation_empty);
                        flag = "0";
                    }
                }

                return flag;
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
                    Toast.makeText(ActivitySignup.this, message, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ActivitySignup.this, ActivityLogin.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(ActivitySignup.this, message, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void dialog_roles()
    {
        final ActionUGroup group = new ActionUGroup(ActivitySignup.this);
        group.execute_index();
        final ViewDialogList dialog = new ViewDialogList(ActivitySignup.this);
        dialog.setTitle(getResources().getString(R.string.v1_signup_select_group));
        dialog.setHiddenDesc(true);
        dialog.setHiddenThumb(true);
        dialog.setHiddenButtonOK(true);
        dialog.setHiddenButtonCancel(true);
        dialog.setDataID(group.getGroupID());
        dialog.setDataTitle(group.getGroupName());
        dialog.setDataThumb(new ArrayList<String>());
        dialog.setDataDesc(new ArrayList<String>());
        dialog.setCancelable(true);
        dialog.show();
        dialog.executeList();
        dialog.getListViewData().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                if(object == null)
                {
                    Toast.makeText(ActivitySignup.this, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    selected_value = object[0];
                    selected_title = object[1];
                    button_roles.setText(selected_title);
                    button_roles.setTextColor(getResources().getColor(R.color.base_black));
                    dialog.dismiss();
                }
            }
        });
        /*dialog.getButtonOK().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.getSelectedObject() == null )
                {

                }
                else {
                    String[] object = dialog.getSelectedObject();

                }
            }
        });
        dialog.getButtonCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
    }

    private void back()
    {
        Intent i = new Intent(ActivitySignup.this, ActivityLogin.class);
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
}
