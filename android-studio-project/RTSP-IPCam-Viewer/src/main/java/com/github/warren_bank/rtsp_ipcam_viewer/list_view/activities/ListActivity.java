package com.github.warren_bank.rtsp_ipcam_viewer.list_view.activities;

import com.github.warren_bank.rtsp_ipcam_viewer.R;
import com.github.warren_bank.rtsp_ipcam_viewer.common.activities.ExitActivity;
import com.github.warren_bank.rtsp_ipcam_viewer.common.activities.FilePicker;
import com.github.warren_bank.rtsp_ipcam_viewer.common.data.SharedPrefs;
import com.github.warren_bank.rtsp_ipcam_viewer.common.data.VideoType;
import com.github.warren_bank.rtsp_ipcam_viewer.common.helpers.FileUtils;
import com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities.GridActivity;
import com.github.warren_bank.rtsp_ipcam_viewer.list_view.recycler_view.RecyclerViewInit;
import com.github.warren_bank.rtsp_ipcam_viewer.list_view.recycler_view.RecyclerViewAdapter;
import com.github.warren_bank.rtsp_ipcam_viewer.main.activities.MainActivity;
import com.github.warren_bank.rtsp_ipcam_viewer.main.dialogs.add_video.VideoDialog;
import com.github.warren_bank.rtsp_ipcam_viewer.main.dialogs.grid_view_columns.GridColumnsDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;

public final class ListActivity extends AppCompatActivity {
    private static final String EXTRA_JSON_VIDEOS = "JSON_VIDEOS";

    private static ArrayList<VideoType> videos;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.list_view_activities_listactivity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(null);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        this.videos = (intent.hasExtra(EXTRA_JSON_VIDEOS))
                ? VideoType.fromJson(intent.getStringExtra(EXTRA_JSON_VIDEOS))
                : VideoType.filterByEnabled(SharedPrefs.getVideos(this))
        ;
        this.recyclerViewAdapter = RecyclerViewInit.adapter(this, this.recyclerView, this.videos);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        recyclerView.setAdapter(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_view_activities_listactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        MainActivity.open(ListActivity.this);
        return true;
    }
}
