package com.mouj.app.fragment;

import android.os.Bundle;
import android.view.View;

import com.mouj.app.BaseActivity;
import com.mouj.app.helper.FragmentInterface;
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
    boolean fullscreen = true;

    public FragmentYoutube()
    {

    }

    public void setID(String yid)
    {
        this.yid = yid;
    }

    public static FragmentYoutube newInstance(String url, boolean fullscreen) {
        FragmentYoutube f = new FragmentYoutube();

        Bundle b = new Bundle();
        b.putString("url", url);

        f.setArguments(b);
        f.init(fullscreen);

        return f;
    }
    private void init(final boolean fullscreen)
    {
        initialize("AIzaSyDtGAzEHwvbYjWLLx6FLA_8_G2RD8e2uAI", new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.cueVideo(getArguments().getString("url"));
                    player.setShowFullscreenButton(fullscreen);
                }
            }
        });
    }
}
