package com.github.warren_bank.rtsp_ipcam_viewer.fullscreen_view.activities;

import com.github.warren_bank.rtsp_ipcam_viewer.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;

import com.github.warren_bank.rtsp_ipcam_viewer.common.helpers.KeepVideoPlaying;
import com.github.warren_bank.rtsp_ipcam_viewer.common.helpers.VideoPlayer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.rtsp.RtspDefaultClient;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.rtsp.core.Client;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity implements VideoPlayer {
    private static final String EXTRA_URL = "URL";

    private SimpleExoPlayer exoPlayer;
    private DefaultHttpDataSourceFactory dataSourceFactory;
    private String url;
    private boolean stopped;
    private boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.fullscreen_view_activities_videoactivity);

        TextureView view = findViewById(R.id.player_view);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        DefaultLoadControl loadControl =  new DefaultLoadControl.Builder()
                .setBufferDurationsMs(100, 500, 100, 100)
                .createDefaultLoadControl();

        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, loadControl);
        String userAgent = getResources().getString(R.string.user_agent);
        this.dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        this.exoPlayer.setVideoTextureView(view);

        Handler handler = new Handler();
        handler.post(new KeepVideoPlaying(handler, exoPlayer, this));

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_URL)) {
            this.url = intent.getStringExtra(EXTRA_URL);
        }
    }

    @Override
    public void startVideo() {
        prepare();
        play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopped = true;

        release();
    }


    @Override
    public boolean isStopped(){
        return stopped;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    private void prepare() {
        Uri uri = Uri.parse(this.url);
        MediaSource source;

        if (Util.isRtspUri(uri)) {
            source = new RtspMediaSource.Factory(RtspDefaultClient.factory()
                .setFlags(Client.FLAG_ENABLE_RTCP_SUPPORT)
                .setNatMethod(Client.RTSP_NAT_DUMMY))
                .createMediaSource(uri);
        } else {
            source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        }

        exoPlayer.prepare(source);
    }

    private void play() {
        try {
            exoPlayer.setPlayWhenReady(true);
        }
        catch (Exception e){}
    }

    private void stop() {
        try {
            exoPlayer.stop(true);
        }
        catch (Exception e){}
    }

    private void release() {
        try {
            exoPlayer.release();
        }
        catch (Exception e){}
    }

    public static void open(Context context, String url) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }
}
