package com.mouj.app.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mouj.app.BaseActivity;
import com.mouj.app.BaseFragment;
import com.mouj.app.MainActivity;
import com.mouj.app.R;
import com.mouj.app.fragment.FragmentGroupEmpty;
import com.mouj.app.fragment.FragmentGroupList;
import com.mouj.app.models.ActionGroup;
import com.mouj.app.view.ViewButton;
import com.mouj.app.view.ViewLoading;

public class ActivityGroup extends BaseActivity {

    RelativeLayout base_container;
    FragmentGroupEmpty emptyGroup;
    ViewButton button_next;
    int steps = 0;
    ViewLoading loading;
    ImageButton imagebutton_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        getList();
        //switchFragment();
    }

    private void init() {

        imagebutton_menu = (ImageButton) findViewById(R.id.group_imagebutton_menu);
        imagebutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggleMenu(true);
                back();
            }
        });


    }

    private void getList()
    {
        new AsyncTask<Void, Integer, String>(){

            boolean isSuccess = false;
            String msg;
            int countGroup = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = (ViewLoading) findViewById(R.id.group_main_loading);
                loading.setImageResource(R.drawable.custom_loading);
                loading.show();
                msg = "";
            }

            @Override
            protected String doInBackground(Void... params) {
                ActionGroup group = new ActionGroup(ActivityGroup.this);
                group.setParam(getParam(), getToken());
                group.setL(10);
                group.setO(0);
                group.executeListGroup();
                if(group.getSuccess())
                {
                    isSuccess = true;
                    countGroup = group.getGRID().size();
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
                if(loading.isShown())
                {
                    loading.dismiss();
                }
                if(isSuccess)
                {
                    if(countGroup <= 0)
                    {
                        addFragmentEmpty();
                    }
                    else
                    {
                        addFragmentGroupList();
                    }
                }
                if(!isSuccess)
                {
                    Toast.makeText(ActivityGroup.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    private void switchFragment()
    {
        int countgr = Integer.parseInt(getHasGR());
        if(countgr <= 0)
        {
            addFragmentEmpty();
        }
        else
        {
            addFragmentGroupList();
        }
    }

    private void addFragmentEmpty()
    {
        emptyGroup = new FragmentGroupEmpty();
        emptyGroup.setU(getUgid());
        getSupportFragmentManager().beginTransaction().
                add(R.id.group_main_container, emptyGroup, emptyGroup.TAG_GROUP_EMPTY).
                addToBackStack(null)
                .commit();
    }

    private void addFragmentGroupList()
    {
        FragmentGroupList groupList = new FragmentGroupList();
        groupList.setParam(getParam(), getToken());
        groupList.setUGroup(getUgid());
        getSupportFragmentManager().beginTransaction().
                add(R.id.group_main_container, groupList, FragmentGroupList.TAG_GROUP_LIST).
                addToBackStack(null)
                .commit();
    }

    private void addFragmentChoiceMember()
    {

    }


    private void back()
    {
        //HelperGlobal.ExitApp(ActivityGroup.this, getResources().getString(R.string.base_string_exit_title));
        Intent i = new Intent(ActivityGroup.this,MainActivity.class);
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
    public void onNavigate(BaseFragment src, String TAG, boolean isBackStack, String BACKSTACK) {
        super.onNavigate(src, TAG, isBackStack, BACKSTACK);
    }

    @Override
    public void sendParameter(BaseFragment fragment) {
        super.sendParameter(fragment);
    }
}
