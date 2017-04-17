package com.basefas.rx.retrofitwithrxjavademo.network;

import android.util.Log;

import com.basefas.rx.retrofitwithrxjavademo.constant.Constant;
import com.basefas.rx.retrofitwithrxjavademo.utils.LogUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * 仿照HttpLoggingInterceptor写的OKhttp拦截器，用于打印log
 */
class RetrofitInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * 不打印日志
         */
        NONE,
        /**
         * 打印 request 和 response 基本信息
         * <p>
         * <p>例子:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * 打印 request and response 基本信息和头文件
         * <p>
         * <p>例子:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * 打印 request and response 基本信息、头文件和body
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);
            }
        };
    }

    public RetrofitInterceptor() {
        this(Logger.DEFAULT);
    }

    public RetrofitInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Level level = Level.NONE;

    /**
     * 设置log级别
     */
    public RetrofitInterceptor setLevel(Level level) {
        if (level == null)
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        Log.d(Constant.LOG, "[HTTP] " + requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    Log.d(Constant.LOG, "[HTTP] " + "Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    Log.d(Constant.LOG, "[HTTP] " + "Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.d(Constant.LOG, "[HTTP] " + name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                Log.d(Constant.LOG, "[HTTP] " + "--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Log.d(Constant.LOG, "[HTTP] " + "--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    Log.d(Constant.LOG, "[HTTP]     " + buffer.readString(charset));
                    Log.d(Constant.LOG, "[HTTP] " + "--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    Log.d(Constant.LOG, "[HTTP] " + "--> END " + request.method()
                            + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.d(Constant.LOG, "[HTTP] " + "<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        Log.d(Constant.LOG, "[HTTP] " + "<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                Log.d(Constant.LOG, "[HTTP] " + headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                Log.d(Constant.LOG, "[HTTP] " + "<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                Log.d(Constant.LOG, "[HTTP] " + "<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        Log.d(Constant.LOG, "[HTTP] " + "Couldn't decode the response body; charset is likely malformed.");
                        Log.d(Constant.LOG, "[HTTP] " + "<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    Log.d(Constant.LOG, "[HTTP] " + "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    LogUtils.http(Constant.LOG, buffer.clone().readString(charset));
                }

                Log.d(Constant.LOG, "[HTTP] " + "<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}

