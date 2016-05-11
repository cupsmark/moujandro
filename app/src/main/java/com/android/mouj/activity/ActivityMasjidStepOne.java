package com.android.mouj.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.R;
import com.android.mouj.helper.HelperDB;
import com.android.mouj.helper.HelperGlobal;
import com.android.mouj.models.ActionStep;
import com.android.mouj.models.UState;
import com.android.mouj.models.UUs;
import com.android.mouj.view.ViewButton;
import com.android.mouj.view.ViewDialogList;
import com.android.mouj.view.ViewEditText;
import com.android.mouj.view.ViewLoadingDialog;
import com.android.mouj.view.ViewText;

import java.util.ArrayList;
import java.util.List;

public class ActivityMasjidStepOne extends BaseActivity {

    ViewEditText edittext_address, edittext_desc;
    ViewText viewtext_caption_city,viewtext_value_city,viewtext_caption_address,viewtext_caption_desc;
    RelativeLayout viewselect_city;
    ViewButton button_next;
    ViewLoadingDialog dialog;

    String value_city, value_address, value_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid_step_one);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init()
    {
        value_city = "0";
        value_address = "0";
        value_desc = "0";
        edittext_address = (ViewEditText) findViewById(R.id.masjid_step_one_value_address);
        edittext_address.setRegular();
        edittext_desc = (ViewEditText) findViewById(R.id.masjid_step_one_value_description);
        edittext_desc.setRegular();
        viewtext_value_city = (ViewText) findViewById(R.id.masjid_step_one_value_city);
        viewtext_value_city.setRegular();
        viewtext_caption_city = (ViewText) findViewById(R.id.masjid_step_one_caption_city);
        viewtext_caption_city.setRegular();
        viewtext_caption_address = (ViewText) findViewById(R.id.masjid_step_one_caption_address);
        viewtext_caption_address.setRegular();
        viewtext_caption_desc = (ViewText) findViewById(R.id.masjid_step_one_caption_description);
        viewtext_caption_desc.setRegular();
        viewselect_city = (RelativeLayout) findViewById(R.id.masjid_step_one_container_value_city);
        button_next = (ViewButton) findViewById(R.id.masjid_step_one_submit);
        button_next.setRegular();
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });
        viewselect_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCity();
            }
        });
    }

    private void setCity()
    {
        ArrayList<String> data_id = new ArrayList<String>();
        ArrayList<String> data_title = new ArrayList<String>();
        HelperDB db = new HelperDB(ActivityMasjidStepOne.this);
        List<UState> listCity = db.getAllUState();
        for(UState us : listCity)
        {
            data_id.add(Integer.toString(us.getStateID()));
            data_title.add(us.getStateName());
        }

        final ViewDialogList dialog = new ViewDialogList(ActivityMasjidStepOne.this);
        dialog.setTitle(getResources().getString(R.string.masjid_step_one_select_city));
        dialog.setHiddenButtonOK(true);
        dialog.setHiddenButtonCancel(true);
        dialog.setHiddenDesc(true);
        dialog.setHiddenThumb(true);
        dialog.setDataID(data_id);
        dialog.setDataTitle(data_title);
        dialog.setDataThumb(new ArrayList<String>());
        dialog.setDataDesc(new ArrayList<String>());
        dialog.setCancelable(true);
        dialog.show();
        dialog.executeList();
        dialog.getListViewData().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog.getListViewData().getAdapter().getItem(position) == null) {
                    Toast.makeText(ActivityMasjidStepOne.this, getResources().getString(R.string.base_string_no_selected), Toast.LENGTH_SHORT).show();
                } else {
                    String[] object = (String[]) dialog.getListViewData().getAdapter().getItem(position);
                    value_city = object[0];
                    viewtext_value_city.setText(object[1]);
                    dialog.dismiss();
                }
            }
        });

    }

    private void nextStep()
    {
        new AsyncTask<Void, Integer, String>(){

            String val_city, val_addr, val_desc, msg;
            boolean isSuccess = false;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ViewLoadingDialog(ActivityMasjidStepOne.this);
                dialog.setCancelable(false);
                dialog.show();

                val_city = value_city;
                val_addr = edittext_address.getText().toString();
                val_desc = edittext_desc.getText().toString();
            }

            @Override
            protected String doInBackground(Void... params) {

                String[] field = new String[]{"token","param","cit","addr","desc"};
                String[] values = new String[]{getToken(), getParam(), val_city, val_addr, val_desc};
                ActionStep step = new ActionStep(ActivityMasjidStepOne.this);
                step.setPostArray(field,values);
                step.executeSendStepOne();
                if(step.getSuccess())
                {
                    isSuccess = true;
                    HelperDB db = new HelperDB(ActivityMasjidStepOne.this);
                    UUs us = new UUs();
                    us.setUUsStep("2");
                    db.updateUUsStep(us, Integer.parseInt(getParam()));
                    db.close();

                    setStep("2");

                }
                else
                {
                    isSuccess = false;
                    msg = step.getMessage();
                }
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
                    Intent i = new Intent(ActivityMasjidStepOne.this, ActivityStepFollowing.class);
                    startActivity(i);
                    finish();
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityMasjidStepOne.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            HelperGlobal.ExitApp(ActivityMasjidStepOne.this, getResources().getString(R.string.base_string_exit_title));
        }
        return super.onKeyDown(keyCode, event);
    }
}
