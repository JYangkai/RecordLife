package com.yk.recordlife.ui.activity.record;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.camera.core.Preview;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yk.media.core.bean.Section;
import com.yk.media.opengles.view.CameraView;
import com.yk.recordlife.R;
import com.yk.recordlife.ui.activity.edit.EditActivity;
import com.yk.recordlife.ui.base.BaseActivity;
import com.yk.recordlife.ui.view.MultiProgressBar;
import com.yk.recordlife.utils.CameraManager;

import java.io.Serializable;
import java.util.List;

public class RecordActivity extends BaseActivity {
    private RecordViewModel viewModel;

    private CameraView cameraView;
    private AppCompatSeekBar seekBar;
    private AppCompatButton recordBtn;
    private AppCompatButton switchBtn;
    private AppCompatButton editBtn;
    private AppCompatCheckBox beautyBox;
    private MultiProgressBar multiProgressBar;

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
        seekBar = findViewById(R.id.seek_bar);
        recordBtn = findViewById(R.id.record_btn);
        switchBtn = findViewById(R.id.switch_btn);
        editBtn = findViewById(R.id.edit_btn);
        beautyBox = findViewById(R.id.beauty_box);
        multiProgressBar = findViewById(R.id.multi_progress_bar);
    }

    @Override
    protected void initData() {
        cameraManager = new CameraManager();

        multiProgressBar.setMaxProgress(30000);
    }

    @Override
    protected void bindEvent() {
        beautyBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cameraView != null) {
                    cameraView.openBeauty(isChecked);
                }
                if (viewModel != null) {
                    viewModel.openBeauty(isChecked);
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
                    multiProgressBar.marker();
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

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Section> sectionList = viewModel.getSectionList();
                if (sectionList == null || sectionList.size() == 0) {
                    Toast.makeText(RecordActivity.this, "未开始录制", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(RecordActivity.this, EditActivity.class);
                intent.putExtra("section_list", (Serializable) sectionList);
                intent.putExtra("path", viewModel.getPath());
                startActivity(intent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float level = 0.99f - (float) progress / 100;
                cameraView.setBeautyLevel(level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        viewModel.progress.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                multiProgressBar.setProgress(progress);
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
        if (multiProgressBar != null) {
            multiProgressBar.setProgress(0);
        }
        if (viewModel != null) {
            viewModel.release();
        }
    }
}
