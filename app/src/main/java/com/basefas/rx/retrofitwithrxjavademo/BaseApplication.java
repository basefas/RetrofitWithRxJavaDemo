package com.basefas.rx.retrofitwithrxjavademo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import com.basefas.rx.retrofitwithrxjavademo.utils.LogUtils;
import com.facebook.stetho.Stetho;

/**
 * Application 基类
 */
public class BaseApplication extends Application {

    private static Context context;
    private String appName;

    @Override
    public void onCreate() {
        super.onCreate();
        printAppParameter();
        Stetho.initializeWithDefaults(this);
        context = getApplicationContext();
        appName = this.getString(R.string.app_name);
    }

    private void printAppParameter() {
        LogUtils.d(appName, "----------App Start----------");
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        LogUtils.d(appName, "OS: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        LogUtils.d(appName, "Density is " + displayMetrics.density + " densityDpi is " + displayMetrics.densityDpi + " height: " + displayMetrics.heightPixels + " width: " + displayMetrics.widthPixels);
        LogUtils.d(appName, "Company is " + Build.MANUFACTURER + " Model is " + (Build.MODEL.isEmpty() ? "" : Build.MODEL.trim().replaceAll("\\s*", "")));
    }

    public static Context getContext() {
        return context;
    }

    public void exit() {
        LogUtils.d(appName, "----------App Exit----------");
        System.exit(0);
    }
}
