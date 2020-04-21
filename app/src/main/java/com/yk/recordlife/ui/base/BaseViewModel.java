package com.yk.recordlife.ui.base;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public abstract class BaseViewModel extends AndroidViewModel {
    protected Context context;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }
}
