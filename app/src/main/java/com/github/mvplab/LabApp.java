package com.github.mvplab;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.mvplab.data.cache.MemoryCache;
import com.github.mvplab.data.db.DatabaseSource;
import com.github.mvplab.data.db.DatabaseSourceImpl;
import com.github.mvplab.data.executor.JobExecutor;
import com.github.mvplab.data.net.RestApi;
import com.github.mvplab.data.net.RestModule;
import com.github.mvplab.data.repository.PostsRepository;
import com.github.mvplab.data.repository.PostsRepositoryImpl;
import com.github.mvplab.ui.MainActivity;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenterImpl;
import com.github.mvplab.ui.listposts.view.PostsView;
import com.github.mvplab.ui.navigation.Navigator;
import com.github.mvplab.ui.navigation.NavigatorImpl;
import com.github.mvplab.ui.post.presenter.PostPresenter;
import com.github.mvplab.ui.post.presenter.PostPresenterImpl;
import com.github.mvplab.ui.post.view.PostView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Date: 06.02.2017
 * Time: 13:25
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class LabApp extends Application {

    private static final int TIMEOUT = 10;
    private static final int MAX_SIZE = 4 * 1024 * 1024;
    private static final String HTTP_CACHE = "http-cache";

    private static LabApp application;

    private MemoryCache memoryCache;
    private PostsRepository postsRepository;
    private DatabaseSource databaseSource;
    private OkHttpClient okHttpClient;
    private JobExecutor jobExecutor;
    private RestApi restApi;
    private Navigator navigator;

    private PostPresenter postPresenter;
    private PostsPresenter postsPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static LabApp getApplication() {
        return application;
    }

    //<editor-fold desc="Dependency injection">
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder().readTimeout(TIMEOUT, TimeUnit.SECONDS);
            if (BuildConfig.DEBUG)
                httpClientBuilder.addInterceptor(getLoggingInterceptor());
            httpClientBuilder.cache(getCache());
            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }

    @NonNull
    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

    @NonNull
    private Cache getCache() {
        return new Cache(new File(application.getCacheDir(), HTTP_CACHE), MAX_SIZE);
    }

    private MemoryCache getMemoryCache() {
        if (memoryCache == null)
            memoryCache = MemoryCache.create();
        return memoryCache;
    }

    private PostsRepository getPostsRepository() {
        if (postsRepository == null)
            postsRepository = new PostsRepositoryImpl(getRestApi(), getDatabaseSource(), getMemoryCache(), getJobExecutor());
        return postsRepository;
    }

    private DatabaseSource getDatabaseSource() {
        if (databaseSource == null)
            databaseSource = new DatabaseSourceImpl(application);
        return databaseSource;
    }

    private RestApi getRestApi() {
        if (restApi == null)
            restApi = new RestModule(getOkHttpClient()).provideRestApi(getEndpoint());
        return restApi;
    }

    @NonNull
    private String getEndpoint() {
        return AppConst.ENDPOINT;
    }

    private JobExecutor getJobExecutor() {
        if (jobExecutor == null)
            jobExecutor = new JobExecutor();
        return jobExecutor;
    }

    private Navigator getNavigator() {
        if(navigator == null)
            navigator = new NavigatorImpl();
        return navigator;
    }

    private PostPresenter getPostPresenter() {
        if (postPresenter == null)
            postPresenter = new PostPresenterImpl(getPostsRepository());
        return postPresenter;
    }

    private PostsPresenter getPostsPresenter() {
        if (postsPresenter == null)
            postsPresenter = new PostsPresenterImpl(getPostsRepository());
        return postsPresenter;
    }

    public void inject(@NonNull MainActivity mainActivity){
        mainActivity.setNavigator(getNavigator());
    }

    public void inject(@NonNull PostView postView) {
        postView.bindPresenter(getPostPresenter());
        postView.getPresenter().bindView(postView);
    }

    public void inject(@NonNull PostsView postsView) {
        postsView.bindPresenter(getPostsPresenter());
        postsView.getPresenter().bindView(postsView);
    }
    //</editor-fold>
}
