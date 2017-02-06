package com.github.mvplab.net;

import android.support.annotation.NonNull;

import com.github.mvplab.AppConst;
import com.github.mvplab.BuildConfig;

import java.util.concurrent.TimeUnit;

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
    @NonNull
    private final OkHttpClient.Builder httpClientBuilder;

    public RestModule() {
        httpClientBuilder = new OkHttpClient.Builder().readTimeout(TIMEOUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        if(BuildConfig.DEBUG)
            httpClientBuilder.addInterceptor(logging);
    }

    public RestApi provideRestApi(){
        return new Retrofit.Builder()
                .baseUrl(AppConst.ENDPOINT)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestApi.class);
    }
}
