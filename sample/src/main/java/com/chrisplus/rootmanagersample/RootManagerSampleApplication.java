package com.chrisplus.rootmanagersample;

import com.squareup.leakcanary.LeakCanary;

import android.app.Application;

public class RootManagerSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
