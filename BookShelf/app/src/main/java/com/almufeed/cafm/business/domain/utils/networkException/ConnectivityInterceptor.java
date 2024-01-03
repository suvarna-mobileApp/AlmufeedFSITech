package com.almufeed.cafm.business.domain.utils.networkException;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {

    private final Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

   /* @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }*/

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }
        Request originalRequest = chain.request();

        // Create a new request with the Content-Type header
        Request newRequest = originalRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(newRequest);
    }
}
