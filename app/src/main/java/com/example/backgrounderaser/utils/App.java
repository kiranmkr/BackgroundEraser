package com.example.backgrounderaser.utils;


import android.content.Context;
import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
