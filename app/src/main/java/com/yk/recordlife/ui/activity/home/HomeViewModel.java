package com.yk.recordlife.ui.activity.home;

import android.app.Application;

import androidx.annotation.NonNull;

import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.ui.base.BaseViewModel;
import com.yk.recordlife.ui.fragment.life.LifeFragment;
import com.yk.recordlife.ui.fragment.record.RecordFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModel {
    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    List<BaseFragment> getFragmentList() {
        List<BaseFragment> list = new ArrayList<>();
        BaseFragment fragment1 = RecordFragment.newInstance();
        BaseFragment fragment2 = LifeFragment.newInstance();
        list.add(fragment1);
        list.add(fragment2);
        return list;
    }

    List<String> getTitleList() {
        List<String> list = new ArrayList<>();
        list.add("记录");
        list.add("生活");
        return list;
    }
}
