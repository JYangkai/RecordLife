package com.yk.recordlife.ui.fragment.video;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.yk.recordlife.R;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.ui.view.AutoFitTextureView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VideoFragment extends BaseFragment implements TextureView.SurfaceTextureListener {
    private VideoViewModel viewModel;

    private AutoFitTextureView playView;
    private ProgressBar progressBar;

    private String path;

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
        initData();
        bindEvent();
    }

    @Override
    protected void findView(View view) {
        playView = view.findViewById(R.id.play_view);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    protected void initData() {
        if (getArguments() == null) {
            return;
        }
        path = getArguments().getString("path");
    }

    @Override
    protected void bindEvent() {
        playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                    startProgressBarTimer();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playView.isAvailable()) {
            startPlay(new Surface(playView.getSurfaceTexture()));
        } else {
            playView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlay();
        stopProgressBarTimer();
    }

    private void startPlay(Surface surface) {
        stopPlay();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.start();
        startProgressBarTimer();
        playView.setRatioSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
    }

    private void stopPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private Timer progressBarTimer;

    private void startProgressBarTimer() {
        stopProgressBarTimer();
        long duration = mediaPlayer.getDuration();
        progressBar.setMax((int) duration);
        progressBarTimer = new Timer();
        progressBarTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer == null) {
                            stopProgressBarTimer();
                            return;
                        }
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                });
            }
        }, 0, 20);
    }

    private void stopProgressBarTimer() {
        if (progressBarTimer != null) {
            progressBarTimer.cancel();
            progressBarTimer = null;
        }
    }

    public static BaseFragment newInstance(String path) {
        BaseFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startPlay(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopPlay();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
