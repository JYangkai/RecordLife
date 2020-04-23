package com.yk.recordlife.ui.activity.edit;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yk.recordlife.data.bean.Frame;
import com.yk.recordlife.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditViewModel extends BaseViewModel {
    MutableLiveData<List<Frame>> frameList = new MutableLiveData<>();

    private ExecutorService service;

    public EditViewModel(@NonNull Application application) {
        super(application);
        service = Executors.newSingleThreadExecutor();
    }

    void loadFrameList(String path) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.i("JOJO", "duration:" + duration);
                List<Frame> list = new ArrayList<>();
                for (int i = 0; i < Long.parseLong(duration); i += 1000) {
                    Bitmap bitmap = retriever.getFrameAtTime(i * 1000);
                    Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,
                            bitmap.getWidth() / 10, bitmap.getHeight() / 10,
                            false);
                    Frame frame = new Frame();
                    frame.setBitmap(scaleBitmap);
                    bitmap.recycle();
                    list.add(frame);
                }
                frameList.postValue(list);
            }
        });
    }
}
