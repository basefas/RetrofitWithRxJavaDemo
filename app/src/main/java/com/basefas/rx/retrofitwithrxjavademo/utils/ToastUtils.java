package com.basefas.rx.retrofitwithrxjavademo.utils;

import android.content.Context;
import android.widget.Toast;

import com.basefas.rx.retrofitwithrxjavademo.BaseApplication;


/**
 * Toast工具类
 */
public class ToastUtils {
    private static Context context = BaseApplication.getContext();

    private static Toast t = null;

    /**
     * 立即显示Toast，如果已有正在显示的Toast，将直接更新Toast的内容
     * @param msg Toast的内容
     * @see {@link #showShortToastImmediately(String)}
     */
    public static void s(String msg) {
        if (t == null) {
            t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            t.setText(msg);
        }

        t.show();
    }

    /**
     * 立即显示Toast，如果已有正在显示的Toast，将直接更新Toast的内容
     * @param resId Toast的内容对应的资源ID
     * @see {@link #showShortToastImmediately(int)}
     */
    public static void s(int resId) {
        if (t == null) {
            t = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            t.setText(resId);
        }

        t.show();
    }

    /**
     * 立即显示Toast，如果已有正在显示的Toast，将直接更新Toast的内容
     * @param msg Toast的内容
     */
    public static void showShortToastImmediately(String msg) {
        s(msg);
    }

    /**
     * 立即显示Toast，如果已有正在显示的Toast，将直接更新Toast的内容
     * @param resId Toast的内容对应的资源ID
     */
    public static void showShortToastImmediately(int resId) {
        s(resId);
    }

    /**
     * 显示Toast，如果已有正在显示的Toast，将等待该Toast显示完毕后再显示本Toast的内容
     * @param msg Toast的内容
     */
    public static void showShortToast(String msg) {
        t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        t.show();
    }

    /**
     * 立即显示Toast，如果已有正在显示的Toast，将等待该Toast显示完毕后再显示本Toast的内容
     * @param resId Toast的内容对应的资源ID
     */
    public static void showShortToast(int resId) {
        t = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        t.show();
    }

    public static void cancel() {
        if (t != null) {
            t.cancel();
        }
    }

}
