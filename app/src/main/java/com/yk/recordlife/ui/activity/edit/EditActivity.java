package com.yk.recordlife.ui.activity.edit;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yk.recordlife.R;
import com.yk.recordlife.data.adapter.FrameAdapter;
import com.yk.recordlife.data.bean.Frame;
import com.yk.recordlife.ui.base.BaseActivity;
import com.yk.recordlife.ui.fragment.video.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends BaseActivity {
    private EditViewModel viewModel;

    private RecyclerView recyclerView;
    private FrameAdapter adapter;
    private List<Frame> frameList = new ArrayList<>();

    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        viewModel = new ViewModelProvider(this).get(EditViewModel.class);
        findView();
        initData();
        bindEvent();
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.frame_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FrameAdapter(frameList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        path = getIntent().getStringExtra("path");
    }

    @Override
    protected void bindEvent() {
        viewModel.frameList.observe(this, new Observer<List<Frame>>() {
            @Override
            public void onChanged(List<Frame> list) {
                if (list == null || list.size() == 0) {
                    return;
                }
                clearFrameList(frameList);
                frameList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, VideoFragment.newInstance(path), "video")
                .commit();
        viewModel.loadFrameList(path);
    }

    private void clearFrameList(List<Frame> frameList) {
        for (Frame frame : frameList) {
            releaseBitmap(frame.getBitmap());
        }
        frameList.clear();
    }

    private void releaseBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }
}
