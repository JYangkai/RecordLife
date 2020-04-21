package com.yk.recordlife.ui.fragment.life;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.yk.recordlife.R;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.utils.MyFragmentPager2Adapter;

import java.util.ArrayList;
import java.util.List;

public class LifeFragment extends BaseFragment {
    private LifeViewModel viewModel;

    private ViewPager2 viewPager2;
    private List<BaseFragment> fragmentList = new ArrayList<>();
    private MyFragmentPager2Adapter fragmentPager2Adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LifeViewModel.class);
        return inflater.inflate(R.layout.fragment_life, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
        initData();
        bindEvent();
    }

    @Override
    protected void findView(View view) {
        viewPager2 = view.findViewById(R.id.view_pager_2);
    }

    @Override
    protected void initData() {
        fragmentPager2Adapter = new MyFragmentPager2Adapter(this, fragmentList);
        viewPager2.setAdapter(fragmentPager2Adapter);
    }

    @Override
    protected void bindEvent() {
        viewModel.fragmentList.observe(this, new Observer<List<BaseFragment>>() {
            @Override
            public void onChanged(List<BaseFragment> fragments) {
                if (fragments == null || fragments.size() == 0) {
                    return;
                }
                fragmentList.clear();
                fragmentList.addAll(fragments);
                fragmentPager2Adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadFragmentList();
    }

    public static BaseFragment newInstance() {
        BaseFragment fragment = new LifeFragment();
        return fragment;
    }
}
