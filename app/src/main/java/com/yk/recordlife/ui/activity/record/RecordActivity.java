package com.yk.recordlife.ui.activity.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.camera.core.Preview;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yk.media.opengles.view.CameraView;
import com.yk.recordlife.R;
import com.yk.recordlife.ui.activity.edit.EditActivity;
import com.yk.recordlife.ui.base.BaseActivity;
import com.yk.recordlife.utils.CameraManager;

public class RecordActivity extends BaseActivity {
    private RecordViewModel viewModel;

    private CameraView cameraView;
    private AppCompatButton recordBtn;
    private AppCompatButton switchBtn;
    private AppCompatButton concatBtn;
    private AppCompatCheckBox beautyBox;

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
        concatBtn = findViewById(R.id.concat_btn);
        beautyBox = findViewById(R.id.beauty_box);
    }

    @Override
    protected void initData() {
        cameraManager = new CameraManager();
    }

    @Override
    protected void bindEvent() {
        beautyBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cameraView != null) {
                    cameraView.openBeauty(isChecked);
                }
            }
        });

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

        concatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.startConcat();
            }
        });

        viewModel.recordResult.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String path) {
                Log.i("JOJO", "录制完成:" + path);
                Intent intent = new Intent(RecordActivity.this, EditActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
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
            viewModel.release();
        }
    }
}
