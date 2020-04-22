package com.yk.recordlife.ui.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected Context context;
    protected Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
    }

    protected abstract void findView(View view);

    protected abstract void initData();

    protected abstract void bindEvent();
}
