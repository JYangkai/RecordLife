package com.yk.recordlife.ui.fragment.life;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yk.recordlife.data.bean.Video;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.ui.base.BaseViewModel;
import com.yk.recordlife.ui.fragment.video.VideoFragment;
import com.yk.recordlife.utils.LocalMediaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LifeViewModel extends BaseViewModel {
    private ExecutorService service;

    MutableLiveData<List<BaseFragment>> fragmentList = new MutableLiveData<>();

    public LifeViewModel(@NonNull Application application) {
        super(application);
        service = Executors.newSingleThreadExecutor();
    }

    void loadFragmentList() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                List<Video> list = LocalMediaUtils.getLocalVideoAll(context);
                List<BaseFragment> fragments = new ArrayList<>();
                for (Video video : list) {
                    fragments.add(VideoFragment.newInstance(video.getPath()));
                }
                fragmentList.postValue(fragments);
            }
        });
    }
}
