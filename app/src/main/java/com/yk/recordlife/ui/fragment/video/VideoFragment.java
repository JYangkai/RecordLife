package com.yk.recordlife.ui.fragment.video;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.yk.recordlife.R;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.ui.view.AutoFitTextureView;

import java.io.IOException;

public class VideoFragment extends BaseFragment implements TextureView.SurfaceTextureListener {
    private VideoViewModel viewModel;

    private AutoFitTextureView playView;

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
    }

    private void startPlay(Surface surface) {
        stopPlay();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.start();
        playView.setRatioSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
    }

    private void stopPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
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
