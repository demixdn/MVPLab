package com.github.mvplab.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mvplab.AppConst;
import com.github.mvplab.BuildConfig;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date: 06.02.2017
 * Time: 13:54
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class RestModule {
    private static final int TIMEOUT = 10;
    private static final int MAX_SIZE = 4 * 1024 * 1024;
    private static final String HTTP_CACHE = "http-cache";
    @NonNull
    private final OkHttpClient.Builder httpClientBuilder;

    public RestModule(Context context) {
        httpClientBuilder = new OkHttpClient.Builder().readTimeout(TIMEOUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        if (BuildConfig.DEBUG)
            httpClientBuilder.addInterceptor(logging);
        Cache cache = new Cache(new File(context.getCacheDir(), HTTP_CACHE), MAX_SIZE);
        httpClientBuilder.cache(cache);
    }

    public RestApi provideRestApi() {
        return new Retrofit.Builder()
                .baseUrl(AppConst.ENDPOINT)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestApi.class);
    }
}
