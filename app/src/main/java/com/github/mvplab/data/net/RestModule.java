package com.github.mvplab.data.net;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
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
    @NonNull
    private final OkHttpClient httpClient;

    public RestModule(@NonNull OkHttpClient httpClient) {

        this.httpClient = httpClient;
    }

    public RestApi provideRestApi(String endpoint) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestApi.class);
    }
}
