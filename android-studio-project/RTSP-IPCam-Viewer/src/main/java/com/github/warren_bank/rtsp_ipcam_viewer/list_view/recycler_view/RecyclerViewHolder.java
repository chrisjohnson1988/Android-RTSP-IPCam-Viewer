package com.github.warren_bank.rtsp_ipcam_viewer.list_view.recycler_view;

import com.github.warren_bank.rtsp_ipcam_viewer.R;
import com.github.warren_bank.rtsp_ipcam_viewer.common.data.VideoType;
import com.github.warren_bank.rtsp_ipcam_viewer.common.helpers.KeepVideoPlaying;
import com.github.warren_bank.rtsp_ipcam_viewer.common.helpers.VideoPlayer;
import com.github.warren_bank.rtsp_ipcam_viewer.fullscreen_view.activities.VideoActivity;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspDefaultClient;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.rtsp.core.Client;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public final class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener, VideoPlayer {

    private PlayerView view;
    private TextView title;
    private SimpleExoPlayer exoPlayer;
    private DefaultHttpDataSourceFactory dataSourceFactory;
    private GestureDetector gestureDetector;
    private boolean stopped;

    private VideoType data;

    public RecyclerViewHolder(View view) {
        this(view, 0);
    }

    public RecyclerViewHolder(View view, int defaultHeight) {
        super(view);

        this.view  = (PlayerView) view;
        this.title = (TextView) view.findViewById(R.id.exo_title);

        if (defaultHeight > 0) {
            this.view.setMinimumHeight(defaultHeight);
            this.title.setMaxHeight(defaultHeight);

            if (this.title.getTextSize() > defaultHeight) {
                this.title.setTextSize(
                    (defaultHeight > 10)
                      ? (float) (defaultHeight - 2)
                      : 0f
                );
            }
        }

        Context context = view.getContext();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        DefaultLoadControl loadControl =  new DefaultLoadControl.Builder()
                .setBufferDurationsMs(100, 500, 100, 100)
                .createDefaultLoadControl();
        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl);

        String userAgent = context.getResources().getString(R.string.user_agent);
        this.dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        this.gestureDetector = new GestureDetector(this);

        this.view.setOnTouchListener(this);
        this.view.setUseController(false);
        this.view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        this.view.setPlayer(this.exoPlayer);

        this.exoPlayer.setVolume(0f);  // mute all videos in list view

        Handler handler = new Handler();
        handler.post(new KeepVideoPlaying(handler, exoPlayer, this));
    }

    @Override
    public void startVideo() {
        Uri uri = Uri.parse(data.URL_low_res);
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

        play();
    }

    public void bind(VideoType data) {
        this.data = data;
        this.title.setText(data.title);

        startVideo();
    }

    public void play() {
        try {
            exoPlayer.setPlayWhenReady(true);
        }
        catch (Exception e){}
    }

    public void stop() {

        try {
            stopped = true;
            exoPlayer.stop(true);
            exoPlayer.release();
        }
        catch (Exception e){}
    }

    @Override
    public boolean isPaused(){
        return false;
    }

    @Override
    public boolean isStopped(){
        return stopped;
    }

    // open selected video in fullscreen view
    private void doOnClick() {
        VideoActivity.open(
            view.getContext(),
            (data.URL_high_res != null) ? data.URL_high_res : data.URL_low_res
        );
    }

    // interface: View.OnTouchListener

    public boolean onTouch(View v, MotionEvent e) {
        return this.gestureDetector.onTouchEvent(e);
    }

    // interface: GestureDetector.OnGestureListener

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        doOnClick();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onDown(MotionEvent e) {return false;}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {return false;}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return false;}

    @Override
    public void onShowPress(MotionEvent e) {}
}
