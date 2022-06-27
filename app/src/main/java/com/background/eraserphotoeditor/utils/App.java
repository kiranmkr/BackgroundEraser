package com.background.eraserphotoeditor.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
