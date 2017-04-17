package com.basefas.rx.retrofitwithrxjavademo.network;

import android.util.Log;

import com.basefas.rx.retrofitwithrxjavademo.constant.Constant;
import com.basefas.rx.retrofitwithrxjavademo.utils.ToastUtils;

import rx.Subscriber;

/**
 * Subscriber封装类
 */
public abstract class RxSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        // e.printStackTrace();
        if (e instanceof ApiException) {
            Log.e(Constant.LOG, "Error:" + e.getMessage());
            ToastUtils.s("Error:" + e.getMessage());
            // 更多异常处理
        } else {
            Log.e(Constant.LOG, "请求失败，请稍后重试...");
        }
    }

    @Override
    public void onNext(T t) {

    }
}
