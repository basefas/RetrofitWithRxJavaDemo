package com.basefas.rx.retrofitwithrxjavademo.network;


import com.basefas.rx.retrofitwithrxjavademo.BuildConfig;
import com.basefas.rx.retrofitwithrxjavademo.model.User;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http方法库
 */
public class Http {

    private static final int DEFAULT_TIMEOUT = 5;

    private ApiService mApiService;

    private Http() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonWithNoNullConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient())
                .build();

        mApiService = retrofit.create(ApiService.class);
    }

    private static class SingletonHolder {
        private static final Http INSTANCE = new Http();
    }

    public static Http getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        //设置log拦截器，用于打印http信息
        RetrofitInterceptor loggingInterceptor = new RetrofitInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? RetrofitInterceptor.Level.BODY : RetrofitInterceptor.Level.NONE);
        builder.addInterceptor(loggingInterceptor);
        //设置Stetho拦截器，用于使用Chrome调试
        builder.addNetworkInterceptor(new StethoInterceptor());
        //设置超时时间
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置超时时间
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        return builder.build();
    }


    // 1. 获取用户信息
    public void user(Subscriber<User> subscriber,String userID) {
        Subscription subscription =
                mApiService.user(userID)
                        .map(new ParseData<User>())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
    }
}
