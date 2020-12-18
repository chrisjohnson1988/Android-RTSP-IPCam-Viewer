package com.github.warren_bank.rtsp_ipcam_viewer.common.helpers;

public interface VideoPlayer {
    void startVideo();
    boolean isStopped();
    boolean isPaused();
}
