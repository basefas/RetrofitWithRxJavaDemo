package com.basefas.rx.retrofitwithrxjavademo.network;

/**
 * http异常错误
 */
class ApiException extends RuntimeException {

    ApiException(String detailMessage) {
        super(detailMessage);
    }
}
