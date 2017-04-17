package com.basefas.rx.retrofitwithrxjavademo.network;

import retrofit2.Response;
import rx.functions.Func1;

/**
 * 解析Response的body体
 */
class ParseData<T> implements Func1<Response<T>, T> {

    @Override
    public T call(Response<T> tResponse) {
        if (tResponse.isSuccessful()) {
            return tResponse.body();
        } else if (tResponse.raw().code() == 500) {
            throw new ApiException("服务器连接失败");
        } else {
            throw new ApiException(tResponse.raw().message());
        }
    }
}
