package com.yk.recordlife.ui.record;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.Preview;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yk.media.opengles.view.CameraView;
import com.yk.recordlife.R;
import com.yk.recordlife.ui.base.BaseActivity;
import com.yk.recordlife.utils.CameraManager;

public class RecordActivity extends BaseActivity {
    private RecordViewModel viewModel;

    private CameraView cameraView;
    private AppCompatButton recordBtn;
    private AppCompatButton switchBtn;

    private CameraManager cameraManager;

    private boolean isRecord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_record);
        viewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        findView();
        initData();
        bindEvent();
    }

    @Override
    protected void findView() {
        cameraView = findViewById(R.id.camera_view);
        recordBtn = findViewById(R.id.record_btn);
        switchBtn = findViewById(R.id.switch_btn);
    }

    @Override
    protected void initData() {
        cameraManager = new CameraManager();
    }

    @Override
    protected void bindEvent() {
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.switchCamera();
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord) {
                    viewModel.stopRecord();
                    isRecord = false;
                    recordBtn.setText("录制");
                } else {
                    boolean isStart = viewModel.startRecord(cameraManager.getFacing(),
                            cameraView.getEglContext(), cameraView.getTextureId(),
                            cameraView.getFinalWidth(), cameraView.getFinalHeight());
                    if (isStart) {
                        isRecord = true;
                        recordBtn.setText("停止");
                    }
                }
            }
        });

        viewModel.recordResult.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String path) {
                Log.i("JOJO", "录制完成:" + path);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.openCamera(this, new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(@NonNull Preview.PreviewOutput output) {
                cameraView.setCameraSize(cameraManager.getWidth(), cameraManager.getHeight());
                cameraView.setSurfaceTexture(output.getSurfaceTexture());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cameraManager != null) {
            cameraManager.closeCamera();
        }
        if (viewModel != null) {
            viewModel.stopRecord();
        }
    }
}
