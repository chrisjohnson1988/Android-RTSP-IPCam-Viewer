package com.github.warren_bank.rtsp_ipcam_viewer.common.helpers;

import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class KeepVideoPlaying implements Runnable {

    private final Handler handler;
    private final SimpleExoPlayer exoPlayer;
    private final VideoPlayer videoPlayer;
    private long contentPosition;

    public KeepVideoPlaying(Handler handler, SimpleExoPlayer exoPlayer, VideoPlayer videoPlayer) {
        this.handler = handler;
        this.exoPlayer = exoPlayer;
        this.videoPlayer = videoPlayer;
    }

    public void run() {
        if(!videoPlayer.isStopped()) {
            if (!videoPlayer.isPaused() && contentPosition == exoPlayer.getContentPosition()) {
                videoPlayer.startVideo();
            }
            contentPosition = exoPlayer.getContentPosition();
            handler.postDelayed(this, 3000);
        }
    }
}
