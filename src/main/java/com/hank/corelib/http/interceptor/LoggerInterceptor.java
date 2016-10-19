package com.hank.corelib.http.interceptor;

/**
 * Created by hank on 2016/8/12.
 */

import android.text.TextUtils;

import com.hank.corelib.logger.Logger;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by zhy on 16/3/1.
 */
public class LoggerInterceptor implements Interceptor {
    public static final String TAG = "Logger";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            //===>response log
            StringBuffer buffer = new StringBuffer();
            buffer.append("========response'log======="+"\n");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            buffer.append("url : " + clone.request().url()+"\n");
            buffer.append("code : " + clone.code()+"\n");
            buffer.append("protocol : " + clone.protocol()+"\n");
            if (!TextUtils.isEmpty(clone.message()))
                buffer.append("message : " + clone.message()+"\n");

            if (showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        buffer.append("responseBody's contentType : " + mediaType.toString()+"\n");
                        if (isText(mediaType)) {
                            String resp = body.string();
                            buffer.append("responseBody's content : " + resp+"\n");
                            body = ResponseBody.create(mediaType, resp);
                            buffer.append( "========response'log=======end");
                            Logger.d(tag, buffer.toString());
                            return response.newBuilder().body(body).build();
                        } else {
                            buffer.append( "responseBody's content : " + " maybe [file part] , too large too print , ignored!"+"\n");
                        }
                    }
                }
            }
            buffer.append( "========response'log=======end");
            Logger.d(tag, buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            StringBuffer buffer = new StringBuffer();
            buffer.append("========request'log======="+"\n");
            buffer.append("method : " + request.method()+"\n");
            buffer.append("url : " + url+"\n");
            buffer.append("headers:"+"\n");
            if (headers != null && headers.size() > 0) {
                buffer.append(headers.toString()+"\n");
            }

            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    buffer.append("requestBody's contentType : " + mediaType.toString()+"\n");
                    if (isText(mediaType)) {
                        buffer.append("requestBody's content : " + bodyToString(request)+"\n");
                    } else {
                        buffer.append( "requestBody's content : " + " maybe [file part] , too large too print , ignored!\n");
                    }
                }
            }
            buffer.append("========request'log=======end"+"\n");
            Logger.d(tag, buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
