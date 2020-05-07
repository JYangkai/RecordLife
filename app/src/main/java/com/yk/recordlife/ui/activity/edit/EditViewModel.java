package com.yk.recordlife.ui.activity.edit;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yk.media.core.MediaConcat;
import com.yk.media.core.OnConcatListener;
import com.yk.media.core.bean.Section;
import com.yk.recordlife.data.bean.Frame;
import com.yk.recordlife.ui.base.BaseViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditViewModel extends BaseViewModel implements OnConcatListener {
    MutableLiveData<Frame> mFrame = new MutableLiveData<>();
    MutableLiveData<String> concatPath = new MutableLiveData<>();

    private ExecutorService service;

    public EditViewModel(@NonNull Application application) {
        super(application);
        service = Executors.newSingleThreadExecutor();
    }

    void loadFrameListForSectionList(String path, List<Section> sectionList) {
        if (sectionList == null || sectionList.size() == 0) {
            return;
        }
        service.execute(new Runnable() {
            @Override
            public void run() {
                for (Section section : sectionList) {
                    loadFrameList(section.getPath());
                }
                startConcat(sectionList, path);
            }
        });
    }

    private int time = 0;

    private void loadFrameList(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        for (int i = 0; i < Long.parseLong(duration); i += 1000) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 10, bitmap.getHeight() / 10,
                    false);
            Frame frame = new Frame();
            frame.setBitmap(scaleBitmap);
            frame.setTime(time + i / 1000);
            bitmap.recycle();
            mFrame.postValue(frame);
        }
        time += (Long.parseLong(duration) / 1000 + 1);
    }

    private MediaConcat mediaConcat;

    private void initMediaConcat() {
        if (mediaConcat == null) {
            mediaConcat = new MediaConcat();
            mediaConcat.setOnConcatListener(this);
        }
    }

    private void startConcat(List<Section> sectionList, String path) {
        initMediaConcat();
        if (mediaConcat != null) {
            mediaConcat.startConcat(path, sectionList);
        }
    }

    void stopConcat() {
        if (mediaConcat != null) {
            mediaConcat.stopConcat();
        }
    }

    @Override
    public void onConcatStart() {

    }

    @Override
    public void onConcatProgress(float progress) {

    }

    @Override
    public void onConcatStop() {

    }

    @Override
    public void onConcatComplete(String path) {
        concatPath.postValue(path);
    }

    @Override
    public void onConcatError(String error) {

    }
}
