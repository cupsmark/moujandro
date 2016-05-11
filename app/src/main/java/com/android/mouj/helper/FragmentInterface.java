package com.android.mouj.helper;

import com.android.mouj.BaseFragment;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 10/20/15.
 */
public interface FragmentInterface{
    void onNavigate(BaseFragment src, String TAG, boolean isBackStack, String BACKSTACK);
    void sendParameter(BaseFragment fragment);
}
