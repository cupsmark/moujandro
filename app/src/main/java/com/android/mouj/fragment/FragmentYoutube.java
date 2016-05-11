package com.android.mouj.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.mouj.BaseActivity;
import com.android.mouj.BaseFragment;
import com.android.mouj.R;
import com.android.mouj.helper.FragmentInterface;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by ekobudiarto on 3/16/16.
 */
public class FragmentYoutube extends YouTubePlayerSupportFragment{

    String yid = "", API_KEY = "";
    public static final String TAG_YOUTUBE_FRAGMENT = "tag:fragment-youtube-player";
    View main_view;
    FragmentInterface iFragment;
    BaseActivity activity;
    YouTubePlayerView youtubePlayerView;
    private static final int RECOVERY_REQUEST = 1;
    YouTubePlayerView player;

    public FragmentYoutube()
    {

    }

    public void setID(String yid)
    {
        this.yid = yid;
    }

    public static FragmentYoutube newInstance(String url) {
        FragmentYoutube f = new FragmentYoutube();

        Bundle b = new Bundle();
        b.putString("url", url);

        f.setArguments(b);
        f.init();

        return f;
    }
    private void init()
    {
        initialize("AIzaSyDtGAzEHwvbYjWLLx6FLA_8_G2RD8e2uAI", new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.cueVideo(getArguments().getString("url"));
                }
            }
        });
    }
}
