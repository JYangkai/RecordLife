package com.yk.recordlife.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yk.recordlife.ui.base.BaseFragment;

import java.util.List;

public class MyFragmentPager2Adapter extends FragmentStateAdapter {
    private List<BaseFragment> fragmentList;

    public MyFragmentPager2Adapter(@NonNull Fragment fragment, List<BaseFragment> fragmentList) {
        super(fragment);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
