package com.yk.recordlife.ui.fragment.record;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.yk.recordlife.R;
import com.yk.recordlife.ui.activity.record.RecordActivity;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.ui.view.RecordTextView;

public class RecordFragment extends BaseFragment {
    private RecordViewModel viewModel;

    private RecordTextView tvRecord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        return inflater.inflate(R.layout.fragment_record, container, false);
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
        tvRecord = view.findViewById(R.id.tv_record);
    }

    @Override
    protected void initData() {
        tvRecord.scaleHeight(0);
    }

    @Override
    protected void bindEvent() {
        tvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RecordActivity.class));
            }
        });
    }

    public static BaseFragment newInstance() {
        BaseFragment fragment = new RecordFragment();
        return fragment;
    }
}
