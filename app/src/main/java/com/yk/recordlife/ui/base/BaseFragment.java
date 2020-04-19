package com.yk.recordlife.ui.base;

import android.view.View;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected abstract void findView(View view);
    protected abstract void initData();
    protected abstract void bindEvent();
}
