package com.yk.recordlife.ui.activity.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yk.recordlife.R;
import com.yk.recordlife.ui.base.BaseActivity;
import com.yk.recordlife.ui.base.BaseFragment;
import com.yk.recordlife.utils.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private HomeViewModel viewModel;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyFragmentPagerAdapter fragmentPagerAdapter;

    private List<BaseFragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        findView();
        initData();
        bindEvent();
    }

    @Override
    protected void findView() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    @Override
    protected void initData() {
        fragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentList, titleList,
                getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        checkPermission();
    }

    @Override
    protected void bindEvent() {
    }

    private void loadFragmentAndTitleList() {
        fragmentList.clear();
        fragmentList.addAll(viewModel.getFragmentList());
        titleList.clear();
        titleList.addAll(viewModel.getTitleList());

        fragmentPagerAdapter.notifyDataSetChanged();
    }

    private void checkPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissionList.size() > 0) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, 0);
            return;
        }
        loadFragmentAndTitleList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(HomeActivity.this, "请先获取相关权限", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
                loadFragmentAndTitleList();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
