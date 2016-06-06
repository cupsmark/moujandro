package com.mouj.app.helper;

import com.mouj.app.BaseFragment;

/**
 * Created by ekobudiarto on 10/20/15.
 */
public interface FragmentInterface{
    void onNavigate(BaseFragment src, String TAG, boolean isBackStack, String BACKSTACK);
    void sendParameter(BaseFragment fragment);
}
