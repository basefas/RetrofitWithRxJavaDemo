package com.basefas.rx.retrofitwithrxjavademo.network;


import com.basefas.rx.retrofitwithrxjavademo.model.User;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * api接口方法
 */
interface ApiService {

    /**
     * 1. 获取用户信息
     */
    @GET(Api.USER)
    Observable<Response<User>> user(
            @Path("user_id") String userID
    );

}