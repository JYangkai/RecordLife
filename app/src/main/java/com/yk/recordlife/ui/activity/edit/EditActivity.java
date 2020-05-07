package com.yk.recordlife.ui.activity.edit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yk.media.core.bean.Section;
import com.yk.media.utils.FileUtils;
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

    private List<Section> sectionList;

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
        sectionList = (List<Section>) getIntent().getSerializableExtra("section_list");
        path = getIntent().getStringExtra("path");
    }

    @Override
    protected void bindEvent() {
        viewModel.mFrame.observe(this, new Observer<Frame>() {
            @Override
            public void onChanged(Frame frame) {
                if (frame == null || frame.getBitmap() == null || frame.getBitmap().isRecycled()) {
                    return;
                }
                frameList.add(frame);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.concatPath.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String concatPath) {
                if (TextUtils.isEmpty(concatPath)) {
                    return;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, VideoFragment.newInstance(concatPath), "video")
                        .commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadFrameListForSectionList(path, sectionList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearFrameList(frameList);
        deleteSectionList();
        if (viewModel != null) {
            viewModel.stopConcat();
        }
    }

    private void clearFrameList(List<Frame> frameList) {
        if (frameList == null || frameList.size() == 0) {
            return;
        }
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

    private void deleteSectionList() {
        if (sectionList == null || sectionList.size() == 0) {
            return;
        }
        for (Section section : sectionList) {
            FileUtils.deleteFile(section.getPath());
        }
        sectionList.clear();
        sectionList = null;
    }
}
